import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ProductService } from '../../core/services/product.service';
import { CartService } from '../../core/services/cart.service';
import { Product } from '../../core/models/product.model';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  products: Product[] = [];
  loading = true;
  error = '';
  searchTerm = '';
  selectedCategory = '';
  currentUrl = '';
  currentTime = '';

  constructor(
    private router: Router,
    private authService: AuthService,
    private productService: ProductService,
    private cartService: CartService
  ) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true;
    this.productService.getPublicProducts().subscribe({
      next: (products) => {
        this.products = products;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Erreur lors du chargement des produits';
        this.loading = false;
        console.error('Erreur:', error);
      }
    });
  }

  onLogin(): void {
    console.log('Navigation vers login...');
    this.router.navigate(['/auth/login']);
  }

  onRegister(): void {
    console.log('Navigation vers register...');
    this.router.navigate(['/auth/register']);
  }

  onProductClick(product: Product): void {
    if (this.authService.isAuthenticated()) {
      // Si connecté, rediriger selon le rôle
      if (this.authService.isAdmin()) {
        this.router.navigate(['/admin/products']);
      } else {
        this.router.navigate(['/products']);
      }
    } else {
      // Si pas connecté, demander de se connecter
      this.onLogin();
    }
  }

  get filteredProducts(): Product[] {
    return this.products.filter(product => 
      product.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
      product.description?.toLowerCase().includes(this.searchTerm.toLowerCase())
    );
  }

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  isAdmin(): boolean {
    return this.authService.isAdmin();
  }



  onSearchInput(event: any): void {
    console.log('Search input:', event.target.value);
    console.log('Search term:', this.searchTerm);
    console.log('Filtered products count:', this.filteredProducts.length);
  }

  addToCart(product: Product): void {
    if (!this.isAuthenticated()) {
      this.router.navigate(['/auth/login']);
      return;
    }

    this.cartService.addProductToCart(0, product.id!, 1).subscribe({
      next: () => {
        console.log('Produit ajouté au panier');
        // Optionnel: Afficher une notification
      },
      error: (error) => {
        console.error('Erreur lors de l\'ajout au panier:', error);
      }
    });
  }
} 