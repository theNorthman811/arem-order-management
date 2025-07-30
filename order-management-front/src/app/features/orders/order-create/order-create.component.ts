import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { Order } from '../../../core/models/order.model';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-order-create',
  templateUrl: './order-create.component.html',
  styleUrls: ['./order-create.component.scss']
})
export class OrderCreateComponent implements OnInit {
  order: Order | null = null;
  customerId: number = 0;
  isEditMode = false;
  loading = false;
  error: string | null = null;

  constructor(
    private orderService: OrderService,
    private router: Router,
    private route: ActivatedRoute,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    const orderId = this.route.snapshot.paramMap.get('id');
    if (orderId) {
      this.isEditMode = true;
      this.loadOrder(+orderId);
    }
  }

  loadOrder(orderId: number): void {
    this.loading = true;
    this.orderService.getOrder(orderId).subscribe({
      next: (order) => {
        this.order = order;
        this.customerId = order.customerId;
        this.loading = false;
      },
      error: (error) => {
        console.error('Error loading order:', error);
        this.error = 'Erreur lors du chargement de la commande';
        this.loading = false;
        this.snackBar.open(this.error, 'Fermer', { duration: 5000 });
      }
    });
  }

  onSubmit(): void {
    if (this.isEditMode && this.order) {
      // Pour l'instant, nous ne pouvons modifier que le statut
      // Vous pouvez ajouter d'autres champs modifiables selon vos besoins
      this.orderService.updateOrderStatus(this.order.id, this.order.status).subscribe({
        next: (updatedOrder) => {
          this.snackBar.open('Commande mise à jour avec succès', 'Fermer', { duration: 3000 });
          this.router.navigate(['/orders', updatedOrder.id]);
        },
        error: (error) => {
          console.error('Error updating order:', error);
          this.snackBar.open('Erreur lors de la mise à jour de la commande', 'Fermer', { duration: 5000 });
        }
      });
    } else if (this.customerId) {
      this.orderService.createOrder(this.customerId).subscribe({
        next: (response) => {
          this.snackBar.open('Commande créée avec succès', 'Fermer', { duration: 3000 });
          this.router.navigate(['/orders', response.id]);
        },
        error: (error) => {
          console.error('Error creating order:', error);
          this.snackBar.open('Erreur lors de la création de la commande', 'Fermer', { duration: 5000 });
        }
      });
    }
  }

  goBack(): void {
    this.router.navigate(['/orders']);
  }
} 