import { Component, OnInit } from '@angular/core';
import { CustomerService } from '../../../core/services/customer.service';
import { Customer } from '../../../core/models/customer.model';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-client-list',
  templateUrl: './client-list.component.html',
  styleUrls: ['./client-list.component.scss']
})
export class ClientListComponent implements OnInit {
  customers: Customer[] = [];
  filteredCustomers: Customer[] = [];
  loading = false;
  searchTerm = '';
  sortBy = 'name';
  sortOrder: 'asc' | 'desc' = 'asc';

  constructor(
    private customerService: CustomerService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.loading = true;
    this.customerService.getCustomers().subscribe({
      next: (customers) => {
        this.customers = customers;
        this.applyFilters();
        this.loading = false;
      },
      error: (error) => {
        console.error('Erreur lors du chargement des clients:', error);
        this.notificationService.showError('Erreur de chargement', 'Impossible de charger la liste des clients');
        this.loading = false;
      }
    });
  }

  applyFilters(): void {
    let filtered = this.customers;

    // Filtre par recherche
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(customer =>
        customer.firstName.toLowerCase().includes(term) ||
        customer.lastName.toLowerCase().includes(term) ||
        customer.email.toLowerCase().includes(term)
      );
    }

    // Tri
    filtered.sort((a, b) => {
      let aValue: string;
      let bValue: string;

      switch (this.sortBy) {
        case 'name':
          aValue = `${a.firstName} ${a.lastName}`.toLowerCase();
          bValue = `${b.firstName} ${b.lastName}`.toLowerCase();
          break;
        case 'email':
          aValue = a.email.toLowerCase();
          bValue = b.email.toLowerCase();
          break;
        default:
          aValue = a.firstName.toLowerCase();
          bValue = b.firstName.toLowerCase();
      }

      if (this.sortOrder === 'asc') {
        return aValue.localeCompare(bValue);
      } else {
        return bValue.localeCompare(aValue);
      }
    });

    this.filteredCustomers = filtered;
  }

  onSearchChange(): void {
    this.applyFilters();
  }

  onSortChange(): void {
    this.applyFilters();
  }

  deleteCustomer(customer: Customer): void {
    if (customer.id && confirm(`Êtes-vous sûr de vouloir supprimer le client ${customer.firstName} ${customer.lastName} ?`)) {
      this.customerService.deleteCustomer(customer.id).subscribe({
        next: () => {
          this.notificationService.showSuccess('Client supprimé', `${customer.firstName} ${customer.lastName} a été supprimé avec succès`);
          this.loadCustomers();
        },
        error: (error) => {
          console.error('Erreur lors de la suppression:', error);
          this.notificationService.showError('Erreur de suppression', 'Impossible de supprimer le client');
        }
      });
    }
  }

  openCreateDialog(): void {
    // TODO: Implémenter la création de client
    this.notificationService.showInfo('Fonctionnalité en développement', 'La création de client sera bientôt disponible');
  }

  editCustomer(customer: Customer): void {
    // TODO: Implémenter l'édition de client
    this.notificationService.showInfo('Fonctionnalité en développement', 'L\'édition de client sera bientôt disponible');
  }
}
