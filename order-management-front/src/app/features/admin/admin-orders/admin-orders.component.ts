import { Component, OnInit } from '@angular/core';
import { OrderService } from '../../../core/services/order.service';
import { CustomerService } from '../../../core/services/customer.service';
import { ProductService } from '../../../core/services/product.service';
import { Order } from '../../../core/models/order.model';
import { Customer } from '../../../core/models/customer.model';
import { Product } from '../../../core/models/product.model';
// Temporairement retiré pour éviter la dépendance circulaire
// import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-admin-orders',
  templateUrl: './admin-orders.component.html',
  styleUrls: ['./admin-orders.component.scss']
})
export class AdminOrdersComponent implements OnInit {
  orders: Order[] = [];
  customers: Customer[] = [];
  products: Product[] = [];
  filteredOrders: Order[] = [];
  loading = false;
  
  // Filtres
  selectedCustomerId: number | null = null;
  selectedStatus: string = 'all';
  searchTerm = '';
  sortBy = 'date-desc';

  // Statistiques calculées
  pendingOrdersCount = 0;
  processingOrdersCount = 0;
  shippedOrdersCount = 0;
  deliveredOrdersCount = 0;

  // Statuts disponibles avec workflow logique
  orderStatuses = [
    { value: 'PENDING', label: '⏳ En attente', color: 'warning' },
    { value: 'PROCESSING', label: '⚙️ En traitement', color: 'info' },
    { value: 'SHIPPED', label: '🚚 Expédiée', color: 'primary' },
    { value: 'DELIVERED', label: '✅ Livrée', color: 'success' },
    { value: 'CANCELLED', label: '❌ Annulée', color: 'danger' }
  ];

  constructor(
    private orderService: OrderService,
    private customerService: CustomerService,
    private productService: ProductService
    // Retiré temporairement : private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.loading = true;
    
    // Charger les commandes
    this.orderService.getOrders().subscribe({
      next: (orders) => {
        this.orders = orders;
        this.calculateStats();
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des commandes:', error);
        alert('❌ Erreur lors du chargement des commandes');
        this.loading = false;
      }
    });

    // Charger les clients
    this.customerService.getCustomers().subscribe({
      next: (customers) => {
        this.customers = customers;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des clients:', error);
      }
    });

    // Charger les produits
    this.productService.getProducts().subscribe({
      next: (products) => {
        this.products = products;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des produits:', error);
      }
    });
  }

  calculateStats(): void {
    this.pendingOrdersCount = this.orders.filter(o => o.status === 'PENDING').length;
    this.processingOrdersCount = this.orders.filter(o => o.status === 'PROCESSING').length;
    this.shippedOrdersCount = this.orders.filter(o => o.status === 'SHIPPED').length;
    this.deliveredOrdersCount = this.orders.filter(o => o.status === 'DELIVERED').length;
  }

  applyFilters(): void {
    let filtered = this.orders;

    // Filtre par client
    if (this.selectedCustomerId) {
      filtered = filtered.filter(order => order.customerId === this.selectedCustomerId);
    }

    // Filtre par statut
    if (this.selectedStatus !== 'all') {
      filtered = filtered.filter(order => order.status === this.selectedStatus);
    }

    // Filtre par recherche
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(order => 
        order.id.toString().includes(term) ||
        this.getCustomerName(order.customerId).toLowerCase().includes(term)
      );
    }

    // Tri
    filtered.sort((a, b) => {
      switch (this.sortBy) {
        case 'date-desc':
          return new Date(b.orderDate).getTime() - new Date(a.orderDate).getTime();
        case 'date-asc':
          return new Date(a.orderDate).getTime() - new Date(b.orderDate).getTime();
        case 'total-desc':
          return b.totalAmount - a.totalAmount;
        case 'total-asc':
          return a.totalAmount - b.totalAmount;
        case 'status':
          return a.status.localeCompare(b.status);
        default:
          return 0;
      }
    });

    this.filteredOrders = filtered;
  }

  getCustomerName(customerId: number): string {
    const customer = this.customers.find(c => c.id === customerId);
    return customer ? `${customer.firstName} ${customer.lastName}` : 'Client inconnu';
  }

  getCustomerEmail(customerId: number): string {
    const customer = this.customers.find(c => c.id === customerId);
    return customer ? customer.email : '';
  }

  getStatusInfo(status: string): { label: string; color: string } {
    const statusInfo = this.orderStatuses.find(s => s.value === status);
    return statusInfo || { label: status, color: 'default' };
  }

  updateOrderStatus(order: Order, newStatus: string): void {
    // Vérifier que le statut est différent
    if (order.status === newStatus) {
      alert('ℹ️ Le statut est déjà à jour');
      return;
    }
    
    console.log(`Mise à jour statut commande #${order.id}: ${order.status} → ${newStatus}`);
    
    this.orderService.updateOrderStatus(order.id!, newStatus).subscribe({
      next: (updatedOrder) => {
        console.log('Statut mis à jour avec succès:', updatedOrder);
        alert(`✅ Statut de la commande #${order.id} mis à jour vers "${this.getStatusInfo(newStatus).label}"`);
        this.loadData(); // Recharger toutes les données pour mettre à jour les stats
      },
      error: (error: any) => {
        console.error('Erreur lors de la mise à jour du statut:', error);
        alert('❌ Erreur lors de la mise à jour du statut');
      }
    });
  }

  createOrderForCustomer(): void {
    // TODO: Implémenter la création de commande pour un client
    alert('🚧 Fonctionnalité de création de commande à venir');
  }

  viewOrderDetails(order: Order): void {
    // TODO: Implémenter la vue détaillée de la commande
    alert(`🚧 Détails de la commande #${order.id} à venir`);
  }

  onFilterChange(): void {
    this.applyFilters();
  }

  clearFilters(): void {
    this.selectedCustomerId = null;
    this.selectedStatus = 'all';
    this.searchTerm = '';
    this.sortBy = 'date-desc';
    this.applyFilters();
  }
} 