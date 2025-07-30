import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-admin-supplier-orders',
  template: `
    <div class="admin-supplier-orders">
      <div class="header">
        <h2>📦 Commandes Fournisseurs</h2>
        <button class="btn btn-primary" (click)="createNewOrder()">
          <i class="fas fa-plus"></i> Nouvelle commande
        </button>
      </div>

      <!-- Statistiques rapides -->
      <div class="stats-overview">
        <div class="stat-item pending">
          <div class="stat-icon">
            <i class="fas fa-clock"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ pendingOrders }}</div>
            <div class="stat-label">En attente</div>
          </div>
        </div>
        <div class="stat-item delivered">
          <div class="stat-icon">
            <i class="fas fa-truck"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ deliveredOrders }}</div>
            <div class="stat-label">Livrées</div>
          </div>
        </div>
        <div class="stat-item total">
          <div class="stat-icon">
            <i class="fas fa-euro-sign"></i>
          </div>
          <div class="stat-content">
            <div class="stat-number">{{ totalAmount }}€</div>
            <div class="stat-label">Total ce mois</div>
          </div>
        </div>
      </div>

      <!-- Liste des commandes -->
      <div class="orders-table">
        <table class="table table-hover">
          <thead>
            <tr>
              <th>ID</th>
              <th>Fournisseur</th>
              <th>Date</th>
              <th>Produits</th>
              <th>Total</th>
              <th>Statut</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            <tr *ngFor="let order of supplierOrders">
              <td><strong>#{{ order.id }}</strong></td>
              <td>{{ order.supplierName }}</td>
              <td>{{ order.orderDate | date:'dd/MM/yyyy' }}</td>
              <td>{{ order.itemsCount }} articles</td>
              <td><strong>{{ order.totalAmount | currency:'EUR' }}</strong></td>
              <td>
                <span class="badge" [ngClass]="getStatusClass(order.status)">
                  {{ getStatusLabel(order.status) }}
                </span>
              </td>
              <td>
                <button class="btn btn-sm btn-outline-primary" (click)="viewOrder(order)">
                  <i class="fas fa-eye"></i>
                </button>
                <button class="btn btn-sm btn-outline-success" 
                        *ngIf="order.status === 'PENDING'"
                        (click)="markAsReceived(order)">
                  <i class="fas fa-check"></i> Reçue
                </button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Message si pas de commandes -->
      <div *ngIf="supplierOrders.length === 0" class="no-orders">
        <i class="fas fa-shipping-fast fa-3x text-muted"></i>
        <h4>Aucune commande fournisseur</h4>
        <p>Passez votre première commande pour réapprovisionner votre stock.</p>
      </div>
    </div>
  `,
  styles: [`
    .admin-supplier-orders {
      padding: 20px;
    }

    .header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 30px;
      padding-bottom: 15px;
      border-bottom: 2px solid #e9ecef;
    }

    .stats-overview {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
      gap: 20px;
      margin-bottom: 30px;
    }

    .stat-item {
      background: white;
      border-radius: 12px;
      padding: 25px;
      display: flex;
      align-items: center;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
      border-left: 4px solid #ddd;
    }

    .stat-item.pending {
      border-left-color: #ffc107;
    }

    .stat-item.delivered {
      border-left-color: #28a745;
    }

    .stat-item.total {
      border-left-color: #17a2b8;
    }

    .stat-icon {
      font-size: 24px;
      margin-right: 15px;
      width: 50px;
      height: 50px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: white;
      background: linear-gradient(45deg, #007bff, #0056b3);
    }

    .stat-content .stat-number {
      font-size: 28px;
      font-weight: bold;
      margin-bottom: 5px;
    }

    .stat-content .stat-label {
      font-size: 14px;
      color: #6c757d;
      font-weight: 500;
    }

    .orders-table {
      background: white;
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
    }

    .table {
      margin-bottom: 0;
    }

    .table thead {
      background: #f8f9fa;
    }

    .table thead th {
      border: none;
      font-weight: 600;
      color: #495057;
      padding: 15px;
    }

    .table tbody td {
      padding: 15px;
      border-top: 1px solid #e9ecef;
      vertical-align: middle;
    }

    .badge.bg-warning {
      background-color: #ffc107 !important;
      color: #856404;
    }

    .badge.bg-success {
      background-color: #28a745 !important;
      color: white;
    }

    .no-orders {
      text-align: center;
      padding: 60px 20px;
      color: #6c757d;
    }

    .no-orders i {
      margin-bottom: 20px;
      opacity: 0.5;
    }
  `]
})
export class AdminSupplierOrdersComponent implements OnInit {
  supplierOrders: any[] = [
    {
      id: 1,
      supplierName: 'Papeterie Pro',
      orderDate: new Date('2025-07-20'),
      itemsCount: 5,
      totalAmount: 250.00,
      status: 'PENDING'
    },
    {
      id: 2,
      supplierName: 'Café Import Europe',
      orderDate: new Date('2025-07-18'),
      itemsCount: 3,
      totalAmount: 180.50,
      status: 'DELIVERED'
    }
  ];

  pendingOrders = 1;
  deliveredOrders = 1;
  totalAmount = 430.50;

  constructor() { }

  ngOnInit(): void {
    console.log('Admin Supplier Orders component loaded');
  }

  createNewOrder(): void {
    alert('🚧 Fonctionnalité "Nouvelle commande fournisseur" à implémenter');
  }

  viewOrder(order: any): void {
    alert(`🚧 Détails de la commande #${order.id} à implémenter`);
  }

  markAsReceived(order: any): void {
    order.status = 'DELIVERED';
    this.pendingOrders--;
    this.deliveredOrders++;
    alert(`✅ Commande #${order.id} marquée comme reçue !`);
  }

  getStatusClass(status: string): string {
    switch (status) {
      case 'PENDING': return 'bg-warning';
      case 'DELIVERED': return 'bg-success';
      default: return 'bg-secondary';
    }
  }

  getStatusLabel(status: string): string {
    switch (status) {
      case 'PENDING': return '⏳ En attente';
      case 'DELIVERED': return '✅ Livrée';
      default: return status;
    }
  }
} 