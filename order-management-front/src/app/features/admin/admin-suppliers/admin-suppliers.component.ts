import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-admin-suppliers',
  template: `
    <div class="admin-suppliers">
      <div class="header">
        <h2>🏭 Gestion des Fournisseurs</h2>
        <button class="btn btn-primary" (click)="addSupplier()">
          <i class="fas fa-plus"></i> Ajouter un fournisseur
        </button>
      </div>

      <div class="suppliers-grid">
        <div class="supplier-card" *ngFor="let supplier of suppliers">
          <div class="supplier-header">
            <h4>{{ supplier.name }}</h4>
            <span class="badge bg-success" *ngIf="supplier.active">Actif</span>
            <span class="badge bg-secondary" *ngIf="!supplier.active">Inactif</span>
          </div>
          <div class="supplier-info">
            <p><i class="fas fa-envelope"></i> {{ supplier.email }}</p>
            <p><i class="fas fa-phone"></i> {{ supplier.phone }}</p>
            <p><i class="fas fa-map-marker-alt"></i> {{ supplier.address }}</p>
          </div>
          <div class="supplier-actions">
            <button class="btn btn-sm btn-outline-primary" (click)="editSupplier(supplier)">
              <i class="fas fa-edit"></i> Modifier
            </button>
            <button class="btn btn-sm btn-outline-success" (click)="createOrder(supplier)">
              <i class="fas fa-shopping-cart"></i> Commander
            </button>
          </div>
        </div>
      </div>

      <!-- Message si pas de fournisseurs -->
      <div *ngIf="suppliers.length === 0" class="no-suppliers">
        <i class="fas fa-industry fa-3x text-muted"></i>
        <h4>Aucun fournisseur</h4>
        <p>Commencez par ajouter vos fournisseurs pour pouvoir passer des commandes.</p>
        <button class="btn btn-primary" (click)="addSupplier()">
          <i class="fas fa-plus"></i> Ajouter le premier fournisseur
        </button>
      </div>
    </div>
  `,
  styles: [`
    .admin-suppliers {
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

    .suppliers-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
      gap: 20px;
      margin-bottom: 30px;
    }

    .supplier-card {
      background: white;
      border-radius: 12px;
      padding: 20px;
      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
      transition: transform 0.2s ease;
    }

    .supplier-card:hover {
      transform: translateY(-2px);
    }

    .supplier-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 15px;
    }

    .supplier-info p {
      margin: 8px 0;
      color: #6c757d;
    }

    .supplier-info i {
      width: 20px;
      margin-right: 8px;
    }

    .supplier-actions {
      display: flex;
      gap: 10px;
      margin-top: 15px;
    }

    .no-suppliers {
      text-align: center;
      padding: 60px 20px;
      color: #6c757d;
    }

    .no-suppliers i {
      margin-bottom: 20px;
      opacity: 0.5;
    }
  `]
})
export class AdminSuppliersComponent implements OnInit {
  suppliers: any[] = [
    // Données d'exemple
    {
      id: 1,
      name: 'Fournisseur Papeterie Pro',
      email: 'contact@papeterie-pro.com',
      phone: '01 23 45 67 89',
      address: '123 Rue du Commerce, Paris',
      active: true
    },
    {
      id: 2,
      name: 'Café Import Europe',
      email: 'commandes@cafe-import.eu',
      phone: '04 56 78 90 12',
      address: '456 Avenue des Importateurs, Lyon',
      active: true
    }
  ];

  constructor() { }

  ngOnInit(): void {
    console.log('Admin Suppliers component loaded');
  }

  addSupplier(): void {
    alert('🚧 Fonctionnalité "Ajouter fournisseur" à implémenter');
  }

  editSupplier(supplier: any): void {
    alert(`🚧 Modification de ${supplier.name} à implémenter`);
  }

  createOrder(supplier: any): void {
    alert(`🚧 Commande auprès de ${supplier.name} à implémenter`);
  }
} 