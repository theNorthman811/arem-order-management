import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

export interface CartItem {
  id: number;
  cartId: number;
  productId: number;
  quantity: number;
  product: any;
  subtotal: number;
}

export interface Cart {
  id: number;
  customerId: number;
  items: CartItem[];
  creationDate: string;
  modificationDate: string;
  isActive: boolean;
  total: number;
  itemCount: number;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = environment.apiUrl;
  private cartSubject = new BehaviorSubject<Cart | null>(null);
  public cart$ = this.cartSubject.asObservable();

  constructor(private http: HttpClient) {}

  /**
   * Récupère le panier actif de l'utilisateur connecté
   */
  getActiveCart(customerId?: number): Observable<Cart> {
    if (customerId) {
      // Pour admin : utiliser l'ID spécifique
      return this.http.get<Cart>(`${this.apiUrl}/api/v1/cart/customer/${customerId}`)
        .pipe(
          tap(cart => this.cartSubject.next(cart)),
          catchError(error => {
            console.error('Erreur lors de la récupération du panier:', error);
            throw error;
          })
        );
    } else {
      // Pour utilisateur connecté : utiliser l'endpoint automatique
      return this.http.get<Cart>(`${this.apiUrl}/api/v1/cart/my-cart`)
        .pipe(
          tap(cart => this.cartSubject.next(cart)),
          catchError(error => {
            console.error('Erreur lors de la récupération du panier:', error);
            throw error;
          })
        );
    }
  }

  /**
   * Ajoute un produit au panier de l'utilisateur connecté
   */
  addProductToCart(customerId: number, productId: number, quantity: number = 1): Observable<Cart> {
    const params = { productId: productId.toString(), quantity: quantity.toString() };
    return this.http.post<Cart>(`${this.apiUrl}/api/v1/cart/add-product`, null, { params })
      .pipe(
        tap(cart => this.cartSubject.next(cart)),
        catchError(error => {
          console.error('Erreur lors de l\'ajout au panier:', error);
          throw error;
        })
      );
  }

  /**
   * Met à jour la quantité d'un produit dans le panier
   */
  updateQuantity(cartItemId: number, quantity: number): Observable<CartItem> {
    const params = { quantity: quantity.toString() };
    return this.http.put<CartItem>(`${this.apiUrl}/api/v1/cart/item/${cartItemId}`, null, { params })
      .pipe(
        tap(() => this.refreshCart()),
        catchError(error => {
          console.error('Erreur lors de la mise à jour de la quantité:', error);
          throw error;
        })
      );
  }

  /**
   * Supprime un produit du panier
   */
  removeProductFromCart(cartItemId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/v1/cart/item/${cartItemId}`)
      .pipe(
        tap(() => this.refreshCart()),
        catchError(error => {
          console.error('Erreur lors de la suppression du produit:', error);
          throw error;
        })
      );
  }

  /**
   * Vide le panier
   */
  clearCart(customerId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/api/v1/cart/customer/${customerId}/clear`)
      .pipe(
        tap(() => this.cartSubject.next(null)),
        catchError(error => {
          console.error('Erreur lors du vidage du panier:', error);
          throw error;
        })
      );
  }

  /**
   * Vérifie si un produit est dans le panier
   */
  isProductInCart(customerId: number, productId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.apiUrl}/api/v1/cart/customer/${customerId}/check/${productId}`)
      .pipe(
        catchError(error => {
          console.error('Erreur lors de la vérification du panier:', error);
          return [false];
        })
      );
  }

  /**
   * Récupère la quantité d'un produit dans le panier
   */
  getProductQuantityInCart(customerId: number, productId: number): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/api/v1/cart/customer/${customerId}/quantity/${productId}`)
      .pipe(
        catchError(error => {
          console.error('Erreur lors de la récupération de la quantité:', error);
          return [0];
        })
      );
  }

  /**
   * Récupère le panier actuel
   */
  getCurrentCart(): Cart | null {
    return this.cartSubject.value;
  }

  /**
   * Rafraîchit le panier (pour les mises à jour)
   */
  private refreshCart(): void {
    const currentCart = this.cartSubject.value;
    if (currentCart) {
      this.getActiveCart(currentCart.customerId).subscribe();
    }
  }

  /**
   * Met à jour le panier local
   */
  updateCart(cart: Cart): void {
    this.cartSubject.next(cart);
  }

  /**
   * Vide le panier local
   */
  clearLocalCart(): void {
    this.cartSubject.next(null);
  }
}

