import { Component, OnInit } from '@angular/core';
import { Product } from '../../../core/models/product.model';
import { ProductService, ProductFormData, ProductContract } from '../../../core/services/product.service';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-admin-products',
  templateUrl: './admin-products.component.html',
  styleUrls: ['./admin-products.component.scss']
})
export class AdminProductsComponent implements OnInit {
  products: Product[] = [];
  filteredProducts: Product[] = [];
  loading = false;
  error: string | null = null;
  searchTerm = '';
  stockFilter = 'all';
  
  // Dialogue de produit
  showDialog = false;
  dialogProduct?: Product;
  isEditMode = false;
  
  // Prix d'achat temporaires (en attendant l'ajout au backend)
  private buyPrices = new Map<number, number>();
  
  // Statistiques
  totalProducts = 0;
  stockOkCount = 0;
  lowStockCount = 0;
  outOfStockCount = 0;
  totalValue = 0;

  constructor(
    private productService: ProductService,
    private notificationService: NotificationService
  ) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading = true;
    this.error = null;
    
    this.productService.getProducts().subscribe({
      next: (products) => {
        this.products = products;
        this.calculateStats();
        this.applyFilters();
        this.checkStockAlerts(); // ← NOUVEAU: Vérifier les alertes
        this.loading = false;
        console.log('✅ Produits chargés:', products.length);
        
        // Notification de succès
        this.notificationService.showSuccess(
          'Produits chargés', 
          `${products.length} produits trouvés`
        );
      },
      error: (error) => {
        console.error('❌ Erreur lors du chargement des produits:', error);
        this.error = 'Impossible de charger les produits';
        this.loading = false;
        
        // Notification d'erreur
        this.notificationService.showError(
          'Erreur de chargement',
          'Impossible de charger les produits'
        );
      }
    });
  }

  calculateStats(): void {
    this.totalProducts = this.products.length;
    this.stockOkCount = this.products.filter(p => p.quantity > 10).length;
    this.lowStockCount = this.products.filter(p => p.quantity > 0 && p.quantity <= 10).length;
    this.outOfStockCount = this.products.filter(p => p.quantity <= 0).length;
    this.totalValue = this.products.reduce((sum, p) => sum + (p.price * p.quantity), 0);
  }

  applyFilters(): void {
    let filtered = this.products;

    // Filtre par recherche
    if (this.searchTerm.trim()) {
      const term = this.searchTerm.toLowerCase();
      filtered = filtered.filter(p => 
        p.name.toLowerCase().includes(term) ||
        p.reference.toLowerCase().includes(term) ||
        (p.marque && p.marque.toLowerCase().includes(term))
      );
    }

    // Filtre par stock
    switch (this.stockFilter) {
      case 'ok':
        filtered = filtered.filter(p => p.quantity > 10);
        break;
      case 'low':
        filtered = filtered.filter(p => p.quantity > 0 && p.quantity <= 10);
        break;
      case 'out':
        filtered = filtered.filter(p => p.quantity <= 0);
        break;
    }

    this.filteredProducts = filtered;
  }

  setStockFilter(filter: string): void {
    this.stockFilter = filter;
    this.applyFilters();
  }

  getStockBadgeClass(product: Product): string {
    if (product.quantity <= 0) return 'badge-danger';
    if (product.quantity <= 10) return 'badge-warning';
    return 'badge-success';
  }

  getStockStatusText(product: Product): string {
    if (product.quantity <= 0) return 'RUPTURE';
    if (product.quantity <= 10) return 'STOCK FAIBLE';
    return 'EN STOCK';
  }

  getStockClass(product: Product): string {
    if (product.quantity <= 0) return 'danger';
    if (product.quantity <= 10) return 'warning';
    return 'success';
  }

  getMeasureText(measure: any): string {
    switch (measure) {
      case 1: return 'unités';  // Measure.Unit
      case 2: return 'kg';      // Measure.Kilogramme
      case 3: return 'L';       // Measure.Liter
      default: return 'unités';
    }
  }

  // Actions
  addProduct(): void {
    this.dialogProduct = undefined;
    this.isEditMode = false;
    this.showDialog = true;
  }

  editProduct(product: Product): void {
    this.dialogProduct = {
      ...product,
      // Ajouter le prix d'achat depuis notre Map temporaire
      buyPrice: this.buyPrices.get(product.id) || 0
    } as any;
    this.isEditMode = true;
    this.showDialog = true;
  }

  manageStock(product: Product): void {
    const newQuantity = prompt(`Stock actuel de "${product.name}": ${product.quantity}\nNouveau stock:`, product.quantity.toString());
    if (newQuantity !== null && !isNaN(Number(newQuantity))) {
      const oldQuantity = product.quantity;
      const quantity = Number(newQuantity);
      const diff = quantity - oldQuantity;
      
      product.quantity = quantity;
      this.calculateStats();
      this.applyFilters();
      this.checkStockAlerts(); // Revérifier les alertes
      
      // Notifications intelligentes
      const emoji = diff > 0 ? '📈' : diff < 0 ? '📉' : '🔄';
      const changeText = diff > 0 ? `+${diff}` : diff < 0 ? `${diff}` : 'inchangé';
      
      this.notificationService.showSuccess(
        `${emoji} Stock mis à jour`,
        `"${product.name}": ${oldQuantity} → ${quantity} (${changeText})`
      );
      
      // Alerte si c'est critique
      const threshold = this.getAlertThreshold(product);
      if (quantity <= 0) {
        this.notificationService.showError(
          '🚨 Stock épuisé',
          `"${product.name}" n'a plus de stock !`
        );
      } else if (quantity <= threshold * 0.3) {
        this.notificationService.showWarning(
          '⚠️ Stock critique',
          `"${product.name}" approche du stock zéro (${quantity} restant)`
        );
      }
    }
  }

  deleteProduct(product: Product): void {
    if (confirm(`Êtes-vous sûr de vouloir supprimer "${product.name}" ?\n\nCette action est irréversible et supprimera le produit de la base de données.`)) {
      // VRAI APPEL API POUR SUPPRIMER
      this.productService.deleteProduct(product.id).subscribe({
        next: () => {
          console.log('✅ Produit supprimé de la base:', product.id);
          
          // Supprimer de la liste locale
          this.products = this.products.filter(p => p.id !== product.id);
          this.buyPrices.delete(product.id);
          
          this.calculateStats();
          this.applyFilters();
          this.checkStockAlerts();
          
          // Notification de suppression
          this.notificationService.showInfo(
            '🗑️ Produit supprimé',
            `"${product.name}" a été retiré de la base de données`
          );
          
          // Alerter si c'était un produit avec beaucoup de stock
          if (product.quantity > 50) {
            this.notificationService.showWarning(
              '⚠️ Stock important supprimé',
              `Vous avez supprimé "${product.name}" qui avait ${product.quantity} unités en stock`
            );
          }
        },
        error: (error) => {
          console.error('❌ Erreur lors de la suppression:', error);
          this.notificationService.showError(
            'Erreur de suppression',
            `Impossible de supprimer "${product.name}" de la base de données`
          );
        }
      });
    }
  }

  // Gestion du dialogue
  onDialogClose(): void {
    this.showDialog = false;
    this.dialogProduct = undefined;
    this.isEditMode = false;
  }

  onProductSaved(productData: any): void {
    if (this.isEditMode && this.dialogProduct) {
      // VRAI APPEL API POUR MODIFIER
      const updateData: ProductFormData = {
        name: productData.name,
        reference: productData.reference,
        description: productData.description || '',
        marque: productData.marque || '',
        sellPrice: productData.sellPrice,
        buyPrice: productData.buyPrice || 0,
        quantity: productData.quantity,
        measure: productData.measure,
        alertThreshold: productData.alertThreshold || 10
      };

      this.productService.updateProduct(this.dialogProduct.id, updateData).subscribe({
        next: (updatedProduct: any) => {
          console.log('✅ Produit mis à jour en base:', updatedProduct);
          
          // Mettre à jour dans la liste locale
          const index = this.products.findIndex(p => p.id === this.dialogProduct!.id);
          if (index !== -1) {
            this.products[index] = {
              ...this.products[index],
              name: updatedProduct.name,
              reference: updatedProduct.reference,
              description: updatedProduct.description,
              marque: updatedProduct.marque,
              price: updatedProduct.prices?.find((p: any) => p.side === 'Sell')?.amount || productData.sellPrice,
              quantity: updatedProduct.quantity,
              measure: updatedProduct.measure
            };
          }
          
          // Sauvegarder le prix d'achat
          if (productData.buyPrice > 0) {
            this.buyPrices.set(this.dialogProduct!.id, productData.buyPrice);
          }
          
          // Notifications intelligentes
          this.notificationService.showSuccess(
            '✏️ Produit modifié',
            `"${productData.name}" a été mis à jour en base de données`
          );
          
          this.calculateStats();
          this.applyFilters();
          this.checkStockAlerts();
        },
        error: (error) => {
          console.error('❌ Erreur lors de la mise à jour:', error);
          this.notificationService.showError(
            'Erreur de modification',
            `Impossible de modifier "${productData.name}". Vérifiez la référence unique.`
          );
        }
      });
    } else {
      // VRAI APPEL API POUR CRÉER
      const createData: ProductFormData = {
        name: productData.name,
        reference: productData.reference,
        description: productData.description || '',
        marque: productData.marque || '',
        sellPrice: productData.sellPrice,
        buyPrice: productData.buyPrice || 0,
        quantity: productData.quantity,
        measure: productData.measure,
        alertThreshold: productData.alertThreshold || 10
      };

      this.productService.createProduct(createData).subscribe({
        next: (newProduct: any) => {
          console.log('✅ Nouveau produit créé en base:', newProduct);
          
          // Ajouter à la liste locale
          const product: Product = {
            id: newProduct.id,
            name: newProduct.name,
            reference: newProduct.reference,
            description: newProduct.description,
            marque: newProduct.marque,
            price: newProduct.prices?.find((p: any) => p.side === 'Sell')?.amount || productData.sellPrice,
            quantity: newProduct.quantity,
            measure: newProduct.measure,
            creationDate: newProduct.creationDate
          };
          
          this.products.push(product);
          
          // Sauvegarder le prix d'achat
          if (productData.buyPrice > 0) {
            this.buyPrices.set(product.id, productData.buyPrice);
          }
          
          // Notification de création
          this.notificationService.showSuccess(
            '🎉 Nouveau produit créé',
            `"${productData.name}" a été ajouté au catalogue et sauvegardé en base`
          );
          
          // Si le stock est faible dès la création
          const threshold = this.getAlertThreshold(product);
          if (productData.quantity <= threshold) {
            this.notificationService.showWarning(
              '⚠️ Stock faible dès la création',
              `"${productData.name}" a un stock de ${productData.quantity} (seuil: ${threshold})`
            );
          }
          
          this.calculateStats();
          this.applyFilters();
          this.checkStockAlerts();
        },
        error: (error) => {
          console.error('❌ Erreur lors de la création:', error);
          this.notificationService.showError(
            'Erreur de création',
            `Impossible de créer "${productData.name}". La référence "${productData.reference}" existe peut-être déjà.`
          );
        }
      });
    }
    
    this.onDialogClose();
  }

  // ← NOUVEAU: Vérification automatique des stocks
  checkStockAlerts(): void {
    const productsWithThreshold = this.products.map(p => ({
      ...p,
      alertThreshold: this.getAlertThreshold(p)
    }));
    
    this.notificationService.checkStockLevels(productsWithThreshold);
  }

  // ← NOUVEAU: Seuil d'alerte par défaut ou personnalisé
  private getAlertThreshold(product: Product): number {
    // Pour l'instant, utiliser des seuils par défaut basés sur le type
    const measure = Number(product.measure);
    switch (measure) {
      case 1: return 10; // Unités
      case 2: return 5;  // Kg
      case 3: return 2;  // Litres
      default: return 10;
    }
  }
} 