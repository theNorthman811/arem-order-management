import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Order } from '../../../core/models/order.model';
import { OrderService } from '../../../core/services/order.service';
import { AuthService } from '../../../core/services/auth.service';
import { ConfirmDialogComponent } from '../../../shared/components/confirm-dialog/confirm-dialog.component';
import { finalize, catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-order-list',
  templateUrl: './order-list.component.html',
  styleUrls: ['./order-list.component.scss']
})
export class OrderListComponent implements OnInit {
  orders: Order[] = [];
  filteredOrders: Order[] = [];
  loading = false;
  error: string | null = null;
  
  // Filter and sort properties
  statusFilter: string = '';
  sortBy: string = 'date-desc';
  searchTerm: string = '';

  constructor(
    private orderService: OrderService,
    private authService: AuthService,
    private router: Router,
    private dialog: MatDialog,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['/auth/login']);
      return;
    }
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading = true;
    this.error = null;
    
    // Si c'est un admin, récupérer toutes les commandes, sinon récupérer les commandes du client connecté
    const isAdmin = this.authService.isAdmin();
    const ordersObservable = isAdmin ? 
      this.orderService.getAllOrders() : 
      this.orderService.getMyOrders();
    
    ordersObservable
      .pipe(
        catchError(error => {
          console.error('Error loading orders:', error);
          if (error.status === 401) {
            this.error = 'Session expirée. Veuillez vous reconnecter.';
            this.authService.logout();
            this.router.navigate(['/auth/login']);
          } else if (error.status === 403) {
            this.error = 'Vous n\'avez pas les permissions nécessaires pour accéder aux commandes.';
          } else {
            this.error = 'Une erreur est survenue lors du chargement des commandes. Veuillez réessayer.';
          }
          return of([]);
        }),
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe(orders => {
        console.log('Orders received:', orders);
        this.orders = orders;
        this.filteredOrders = orders;
      });
  }

  applyFilter(event: Event): void {
    this.searchTerm = (event.target as HTMLInputElement).value.toLowerCase();
    this.applyFilters();
  }

  applyFilters(): void {
    this.filteredOrders = this.orders.filter(order => {
      // Search filter
      const matchesSearch = !this.searchTerm || 
        order.id.toString().includes(this.searchTerm) ||
        order.status.toLowerCase().includes(this.searchTerm) ||
        order.customerId.toString().includes(this.searchTerm);
      
      // Status filter
      const matchesStatus = !this.statusFilter || order.status === this.statusFilter;
      
      return matchesSearch && matchesStatus;
    });
    
    this.applySort();
  }

  applySort(): void {
    this.filteredOrders.sort((a, b) => {
      switch (this.sortBy) {
        case 'date-desc':
          return new Date(b.orderDate).getTime() - new Date(a.orderDate).getTime();
        case 'date-asc':
          return new Date(a.orderDate).getTime() - new Date(b.orderDate).getTime();
        case 'total-desc':
          return b.totalAmount - a.totalAmount;
        case 'total-asc':
          return a.totalAmount - b.totalAmount;
        case 'id-desc':
          return b.id - a.id;
        case 'id-asc':
          return a.id - b.id;
        default:
          return 0;
      }
    });
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.statusFilter = '';
    this.sortBy = 'date-desc';
    this.filteredOrders = [...this.orders];
    this.applySort();
  }

  createOrder(): void {
    this.router.navigate(['/orders/create']);
  }

  viewOrder(order: Order): void {
    this.router.navigate(['/orders', order.id]);
  }

  editOrder(order: Order): void {
    this.router.navigate(['/orders/edit', order.id]);
  }

  deleteOrder(orderId: number): void {
    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: 'Confirmation de suppression',
        message: 'Êtes-vous sûr de vouloir supprimer cette commande ?'
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.orderService.deleteOrder(orderId)
          .pipe(
            catchError(error => {
              console.error('Error deleting order:', error);
              this.snackBar.open(
                'Une erreur est survenue lors de la suppression de la commande.',
                'Fermer',
                { duration: 5000 }
              );
              return of(undefined);
            })
          )
          .subscribe(() => {
            this.loadOrders();
            this.snackBar.open('Commande supprimée avec succès', 'Fermer', {
              duration: 3000
            });
          });
      }
    });
  }

  calculateOrderTotal(order: Order): number {
    if (!order.items || order.items.length === 0) {
      return 0;
    }
    return order.items.reduce((total, item) => {
      return total + (item.quantity * (item.unitPrice || 0));
    }, 0);
  }
} 