import { Component, OnInit, OnDestroy } from '@angular/core';
import { CartService } from '../../core/services/cart.service';
import { OrderService } from '../../core/services/order.service';
import { AuthService } from '../../core/services/auth.service';
import { Cart, CartItem } from '../../core/services/cart.service';
import { Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { finalize } from 'rxjs/operators';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss']
})
export class CartComponent implements OnInit, OnDestroy {
  cart: Cart | null = null;
  loading = true;
  error: string | null = null;
  isProcessingCheckout = false;
  private cartSubscription?: Subscription;

  constructor(
    private cartService: CartService,
    private orderService: OrderService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCart();
    this.cartSubscription = this.cartService.cart$.subscribe(cart => {
      this.cart = cart;
    });
  }

  ngOnDestroy(): void {
    if (this.cartSubscription) {
      this.cartSubscription.unsubscribe();
    }
  }

  loadCart(): void {
    this.loading = true;
    this.error = null;

    this.cartService.getActiveCart().subscribe({
      next: (cart) => {
        this.cart = cart;
        this.loading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement du panier:', error);
        this.error = 'Erreur lors du chargement du panier';
        this.loading = false;
      }
    });
  }

  updateQuantity(cartItem: CartItem, newQuantity: number): void {
    if (newQuantity <= 0) {
      this.removeItem(cartItem);
      return;
    }

    this.cartService.updateQuantity(cartItem.id, newQuantity).subscribe({
      next: () => {
        console.log('Quantité mise à jour');
      },
      error: (error) => {
        console.error('Erreur lors de la mise à jour de la quantité:', error);
        this.error = 'Erreur lors de la mise à jour de la quantité';
      }
    });
  }

  onQuantityChange(event: Event, cartItem: CartItem): void {
    const target = event.target as HTMLInputElement;
    const newQuantity = parseInt(target.value, 10);
    
    if (isNaN(newQuantity) || newQuantity < 1) {
      // Remettre la valeur précédente si invalide
      target.value = cartItem.quantity.toString();
      return;
    }
    
    this.updateQuantity(cartItem, newQuantity);
  }

  removeItem(cartItem: CartItem): void {
    this.cartService.removeProductFromCart(cartItem.id).subscribe({
      next: () => {
        console.log('Produit supprimé du panier');
      },
      error: (error) => {
        console.error('Erreur lors de la suppression du produit:', error);
        this.error = 'Erreur lors de la suppression du produit';
      }
    });
  }

  clearCart(): void {
    if (!this.cart) return;

    this.cartService.clearCart(this.cart.customerId).subscribe({
      next: () => {
        console.log('Panier vidé');
        this.cart = null;
      },
      error: (error) => {
        console.error('Erreur lors du vidage du panier:', error);
        this.error = 'Erreur lors du vidage du panier';
      }
    });
  }

  proceedToCheckout(): void {
    if (this.cart && this.cart.items.length > 0 && !this.isProcessingCheckout) {
      this.isProcessingCheckout = true;
      this.error = null;
      
      console.log('Starting checkout process...');
      
      this.orderService.checkout()
        .pipe(
          finalize(() => {
            this.isProcessingCheckout = false;
          })
        )
        .subscribe({
          next: (response) => {
            console.log('Checkout successful:', response);
            if (response.success) {
              // Afficher message de succès
              alert(`Commande créée avec succès !\nNuméro: ${response.orderId}\nTotal: ${response.totalAmount}€`);
              
              // Recharger le panier (qui devrait être vide maintenant)
              this.loadCart();
              
              // Rediriger vers la liste des commandes
              this.router.navigate(['/orders']);
            } else {
              this.error = response.message || 'Erreur lors de la création de la commande';
            }
          },
          error: (error) => {
            console.error('Checkout failed:', error);
            if (error.error && error.error.error) {
              this.error = error.error.error;
            } else if (error.status === 401) {
              this.error = 'Session expirée. Veuillez vous reconnecter.';
              this.authService.logout();
              this.router.navigate(['/auth/login']);
            } else {
              this.error = 'Erreur lors de la création de la commande. Veuillez réessayer.';
            }
          }
        });
    }
  }

  continueShopping(): void {
    this.router.navigate(['/products']);
  }

  getTotal(): number {
    return this.cart?.total || 0;
  }

  getItemCount(): number {
    return this.cart?.itemCount || 0;
  }

  isCartEmpty(): boolean {
    return !this.cart || this.cart.items.length === 0;
  }
}
