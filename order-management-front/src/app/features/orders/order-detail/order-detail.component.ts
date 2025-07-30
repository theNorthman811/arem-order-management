import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, finalize } from 'rxjs/operators';
import { of } from 'rxjs';
import { OrderService } from '../../../core/services/order.service';
import { ProductService } from '../../../core/services/product.service';
import { Order, OrderItem, Measure } from '../../../core/models/order.model';
import { Product } from '../../../core/models/product.model';
import { OrderItemDialogComponent } from '../components/order-item-dialog/order-item-dialog.component';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';

@Component({
  selector: 'app-order-detail',
  templateUrl: './order-detail.component.html',
  styleUrls: ['./order-detail.component.scss']
})
export class OrderDetailComponent implements OnInit {
  order: Order | null = null;
  products: Product[] = [];
  loading = false;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService,
    private productService: ProductService,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.loadOrder();
    this.loadProducts();
  }

  loadOrder(): void {
    const orderId = Number(this.route.snapshot.paramMap.get('id'));
    if (orderId) {
      this.loading = true;
      this.orderService.getOrder(orderId).pipe(
        catchError(error => {
          console.error('Error loading order:', error);
          this.error = error.message || 'Erreur lors du chargement de la commande';
          return of(null);
        }),
        finalize(() => {
          this.loading = false;
        })
      ).subscribe(order => {
        this.order = order;
      });
    }
  }

  loadProducts(): void {
    this.productService.getProducts().pipe(
      catchError(error => {
        console.error('Error loading products:', error);
        this.snackBar.open('Erreur lors du chargement des produits', 'Fermer', {
          duration: 3000
        });
        return of([]);
      })
    ).subscribe(products => {
      this.products = products;
    });
  }

  addItemToOrder(item: Partial<OrderItem>): void {
    if (!this.order || !item.productId || !item.quantity) return;

    this.loading = true;
    this.orderService.addItemToOrder(
      this.order.id,
      item.productId,
      item.quantity
    ).pipe(
      catchError(error => {
        console.error('Error adding item:', error);
        this.snackBar.open(error.message || 'Erreur lors de l\'ajout de l\'article', 'Fermer', {
          duration: 3000
        });
        return of(null);
      }),
      finalize(() => {
        this.loading = false;
      })
    ).subscribe(updatedOrder => {
      if (updatedOrder) {
        this.order = updatedOrder;
        this.snackBar.open('Article ajouté avec succès', 'Fermer', {
          duration: 3000
        });
      }
    });
  }

  updateItemInOrder(itemId: number, item: Partial<OrderItem>): void {
    if (!this.order || !item.quantity) return;

    this.loading = true;
    this.orderService.updateOrderItem(
      this.order.id,
      itemId,
      item.quantity
    ).pipe(
      catchError(error => {
        console.error('Error updating item:', error);
        this.snackBar.open(error.message || 'Erreur lors de la mise à jour de l\'article', 'Fermer', {
          duration: 3000
        });
        return of(null);
      }),
      finalize(() => {
        this.loading = false;
      })
    ).subscribe(updatedOrder => {
      if (updatedOrder) {
        this.order = updatedOrder;
        this.snackBar.open('Article mis à jour avec succès', 'Fermer', {
          duration: 3000
        });
      }
    });
  }

  openAddItemDialog(): void {
    if (!this.order) return;

    const dialogRef = this.dialog.open(OrderItemDialogComponent, {
      width: '500px',
      data: {
        products: this.products,
        mode: 'add'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && this.order) {
        this.addItemToOrder(result);
      }
    });
  }

  openEditItemDialog(item: OrderItem): void {
    if (!this.order) return;

    const dialogRef = this.dialog.open(OrderItemDialogComponent, {
      width: '500px',
      data: {
        item,
        products: this.products,
        mode: 'edit'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result && this.order) {
        this.updateItemInOrder(item.id!, result);
      }
    });
  }

  removeItem(itemId: number): void {
    if (!this.order) return;

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Supprimer l\'article',
        message: 'Êtes-vous sûr de vouloir supprimer cet article ?'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loading = true;
        this.orderService.removeItemFromOrder(this.order!.id, itemId).pipe(
          catchError(error => {
            console.error('Error removing item:', error);
            this.snackBar.open(error.message || 'Erreur lors de la suppression de l\'article', 'Fermer', {
              duration: 3000
            });
            return of(null);
          }),
          finalize(() => {
            this.loading = false;
          })
        ).subscribe(updatedOrder => {
          if (updatedOrder) {
            this.order = updatedOrder;
            this.snackBar.open('Article supprimé avec succès', 'Fermer', {
              duration: 3000
            });
          }
        });
      }
    });
  }

  deleteOrder(): void {
    if (!this.order) return;

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Supprimer la commande',
        message: 'Êtes-vous sûr de vouloir supprimer cette commande ?'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.loading = true;
        this.orderService.deleteOrder(this.order!.id).pipe(
          catchError(error => {
            console.error('Error deleting order:', error);
            this.snackBar.open(error.message || 'Erreur lors de la suppression de la commande', 'Fermer', {
              duration: 3000
            });
            return of(null);
          }),
          finalize(() => {
            this.loading = false;
          })
        ).subscribe(() => {
          this.snackBar.open('Commande supprimée avec succès', 'Fermer', {
            duration: 3000
          });
          this.goBack();
        });
        }
    });
  }

  goBack(): void {
    this.router.navigate(['/orders']);
  }
} 