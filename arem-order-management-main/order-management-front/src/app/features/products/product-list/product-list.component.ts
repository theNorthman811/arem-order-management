import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs';
import { Product } from '../../../core/models/product.model';
import { ProductService } from '../../../core/services/product.service';
import { AuthService } from '../../../core/services/auth.service';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { ProductDialogComponent, ProductDialogData } from '../product-dialog/product-dialog.component';
import { CartService } from '../../../core/services/cart.service';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  filteredProducts: Product[] = [];
  loading = false;
  error: string | null = null;
  
  // Filter and sort properties
  searchTerm: string = '';
  sortBy: string = 'name-asc';

  constructor(
    private productService: ProductService,
    private authService: AuthService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar,
    private cartService: CartService // Ajout injection CartService
  ) {}

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      this.snackBar.open('Accès non autorisé', 'Fermer', { duration: 3000 });
      return;
    }
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true;
    this.error = null;
    
    this.productService.getPublicProducts()
      .pipe(
        catchError(error => {
          console.error('Error loading products:', error);
          this.error = error.message || 'Erreur lors du chargement des produits';
          return of([]);
        }),
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe(products => {
        this.products = products;
        this.applyFilters();
      });
  }

  applyFilter(event: Event): void {
    this.searchTerm = (event.target as HTMLInputElement).value.toLowerCase();
    this.applyFilters();
  }

  applyFilters(): void {
    this.filteredProducts = this.products.filter(product => {
      return !this.searchTerm || 
        product.name.toLowerCase().includes(this.searchTerm) ||
        product.reference.toLowerCase().includes(this.searchTerm) ||
        (product.marque && product.marque.toLowerCase().includes(this.searchTerm));
    });
    
    this.applySort();
  }

  applySort(): void {
    this.filteredProducts.sort((a, b) => {
      switch (this.sortBy) {
        case 'name-asc':
          return a.name.localeCompare(b.name);
        case 'name-desc':
          return b.name.localeCompare(a.name);
        case 'reference-asc':
          return a.reference.localeCompare(b.reference);
        case 'reference-desc':
          return b.reference.localeCompare(a.reference);
        case 'price-asc':
          return a.price - b.price;
        case 'price-desc':
          return b.price - a.price;
        case 'quantity-asc':
          return a.quantity - b.quantity;
        case 'quantity-desc':
          return b.quantity - a.quantity;
        default:
          return 0;
      }
    });
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.sortBy = 'name-asc';
    this.filteredProducts = [...this.products];
    this.applySort();
  }

  createProduct(): void {
    const dialogRef = this.dialog.open(ProductDialogComponent, {
      width: '600px',
      data: { isEdit: false } as ProductDialogData
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loading = true;
        this.productService.createProduct(result)
          .pipe(
            catchError(error => {
              console.error('Error creating product:', error);
              this.snackBar.open(
                'Une erreur est survenue lors de la création du produit.',
                'Fermer',
                { duration: 5000 }
              );
              return of(undefined);
            }),
            finalize(() => {
              this.loading = false;
            })
          )
          .subscribe(() => {
            this.loadProducts();
            this.snackBar.open('Produit créé avec succès', 'Fermer', {
              duration: 3000
            });
          });
      }
    });
  }

  editProduct(product: Product): void {
    const dialogRef = this.dialog.open(ProductDialogComponent, {
      width: '600px',
      data: { product, isEdit: true } as ProductDialogData
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loading = true;
        this.productService.updateProduct(product.id!, result)
          .pipe(
            catchError(error => {
              console.error('Error updating product:', error);
              this.snackBar.open(
                'Une erreur est survenue lors de la modification du produit.',
                'Fermer',
                { duration: 5000 }
              );
              return of(undefined);
            }),
            finalize(() => {
              this.loading = false;
            })
          )
          .subscribe(() => {
            this.loadProducts();
            this.snackBar.open('Produit modifié avec succès', 'Fermer', {
              duration: 3000
            });
          });
      }
    });
  }

  deleteProduct(productId: number): void {
    const product = this.products.find(p => p.id === productId);
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Confirmation de suppression',
        message: `Êtes-vous sûr de vouloir supprimer le produit "${product?.name}" ?`
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loading = true;
        this.productService.deleteProduct(productId)
          .pipe(
            catchError(error => {
              console.error('Error deleting product:', error);
              this.snackBar.open(
                'Une erreur est survenue lors de la suppression du produit.',
                'Fermer',
                { duration: 5000 }
              );
              return of(undefined);
            }),
            finalize(() => {
              this.loading = false;
            })
          )
          .subscribe(() => {
            this.loadProducts();
            this.snackBar.open('Produit supprimé avec succès', 'Fermer', {
              duration: 3000
            });
          });
      }
    });
  }

  addToCart(product: Product): void {
    this.authService.getCurrentUser().subscribe({
      next: (currentUser) => {
        if (!currentUser || !currentUser.id) {
          this.snackBar.open('Veuillez vous connecter pour ajouter des produits au panier', 'Fermer', { duration: 3000 });
          return;
        }

        this.cartService.addProductToCart(currentUser.id, product.id!, 1).subscribe({
          next: () => {
            this.snackBar.open('Produit ajouté au panier !', 'Fermer', { duration: 2000 });
          },
          error: (error) => {
            console.error('Erreur lors de l\'ajout au panier:', error);
            this.snackBar.open('Erreur lors de l\'ajout au panier', 'Fermer', { duration: 3000 });
          }
        });
      },
      error: (error) => {
        console.error('Erreur lors de la récupération de l\'utilisateur:', error);
        this.snackBar.open('Veuillez vous connecter pour ajouter des produits au panier', 'Fermer', { duration: 3000 });
      }
    });
  }

  getStockStatus(quantity: number): { status: string; color: string } {
    if (quantity <= 0) {
      return { status: 'Rupture', color: 'error' };
    } else if (quantity <= 10) {
      return { status: 'Faible', color: 'warning' };
    } else {
      return { status: 'Disponible', color: 'success' };
    }
  }
}
