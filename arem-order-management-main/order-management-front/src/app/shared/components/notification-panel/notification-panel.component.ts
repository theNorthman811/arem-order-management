import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subscription } from 'rxjs';
import { NotificationService, Notification, StockAlert } from '../../../core/services/notification.service';

@Component({
  selector: 'app-notification-panel',
  template: `
    <div class="notification-container">
      <!-- Toast Notifications -->
      <div class="toast-container">
        <div *ngFor="let notification of notifications" 
             class="toast-notification"
             [ngClass]="'toast-' + notification.type"
             [@slideIn]>
          <div class="toast-icon">
            <i [ngClass]="getIconClass(notification.type)"></i>
          </div>
          <div class="toast-content">
            <div class="toast-title">{{ notification.title }}</div>
            <div class="toast-message">{{ notification.message }}</div>
            <div class="toast-time">{{ notification.timestamp | date:'HH:mm:ss' }}</div>
          </div>
          <button class="toast-close" (click)="closeNotification(notification.id)">
            <i class="fas fa-times"></i>
          </button>
        </div>
      </div>

      <!-- Stock Alerts Panel -->
      <div class="stock-alerts-panel" *ngIf="stockAlerts.length > 0">
        <div class="panel-header">
          <h3>
            <i class="fas fa-exclamation-triangle"></i>
            Alertes de Stock ({{ stockAlerts.length }})
          </h3>
          <button class="clear-all-btn" (click)="clearAllStockAlerts()">
            <i class="fas fa-broom"></i>
            Tout effacer
          </button>
        </div>
        
        <div class="alerts-list">
          <div *ngFor="let alert of stockAlerts" 
               class="stock-alert-item"
               [ngClass]="'alert-' + alert.severity"
               (click)="onStockAlertClick(alert)"
               title="Cliquer pour voir les détails">
            <div class="alert-icon">
              <i [ngClass]="getStockIconClass(alert.severity)"></i>
            </div>
            <div class="alert-content">
              <div class="alert-product">{{ alert.productName }}</div>
              <div class="alert-details">
                <span class="current-stock">Stock: {{ alert.currentStock }}</span>
                <span class="threshold">Seuil: {{ alert.threshold }}</span>
                <span class="alert-severity" [ngClass]="'severity-' + alert.severity">
                  {{ getStatusText(alert.severity) }}
                </span>
              </div>
            </div>
            <div class="alert-actions">
              <button class="action-btn restock-btn" 
                      (click)="openRestockDialog(alert); $event.stopPropagation()"
                      title="Réapprovisionner">
                <i class="fas fa-plus"></i>
              </button>
              <button class="action-btn dismiss-btn" 
                      (click)="dismissStockAlert(alert.productId); $event.stopPropagation()"
                      title="Ignorer cette alerte">
                <i class="fas fa-times"></i>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .notification-container {
      position: fixed;
      top: 20px;
      right: 20px;
      z-index: 10000;
      max-width: 400px;
      width: 100%;
    }

    .toast-container {
      margin-bottom: 20px;
    }

    .toast-notification {
      background: white;
      border-radius: 12px;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
      margin-bottom: 12px;
      padding: 16px;
      display: flex;
      align-items: flex-start;
      gap: 12px;
      border-left: 4px solid #e2e8f0;
      animation: slideInRight 0.3s ease;
      position: relative;
      overflow: hidden;
    }

    @keyframes slideInRight {
      from {
        transform: translateX(100%);
        opacity: 0;
      }
      to {
        transform: translateX(0);
        opacity: 1;
      }
    }

    .toast-success { border-left-color: #10b981; }
    .toast-error { border-left-color: #ef4444; }
    .toast-warning { border-left-color: #f59e0b; }
    .toast-info { border-left-color: #3b82f6; }
    .toast-stock-alert { border-left-color: #f59e0b; }

    .toast-icon {
      flex-shrink: 0;
      width: 24px;
      height: 24px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 16px;
    }

    .toast-success .toast-icon { color: #10b981; }
    .toast-error .toast-icon { color: #ef4444; }
    .toast-warning .toast-icon { color: #f59e0b; }
    .toast-info .toast-icon { color: #3b82f6; }
    .toast-stock-alert .toast-icon { color: #f59e0b; }

    .toast-content {
      flex: 1;
      min-width: 0;
    }

    .toast-title {
      font-weight: 600;
      color: #1f2937;
      margin-bottom: 4px;
      font-size: 14px;
    }

    .toast-message {
      color: #6b7280;
      font-size: 13px;
      line-height: 1.4;
      margin-bottom: 4px;
    }

    .toast-time {
      color: #9ca3af;
      font-size: 11px;
    }

    .toast-close {
      position: absolute;
      top: 8px;
      right: 8px;
      background: none;
      border: none;
      color: #9ca3af;
      cursor: pointer;
      padding: 4px;
      border-radius: 4px;
      transition: all 0.2s ease;

      &:hover {
        color: #6b7280;
        background: #f3f4f6;
      }
    }

    .stock-alerts-panel {
      background: white;
      border-radius: 16px;
      box-shadow: 0 12px 48px rgba(0, 0, 0, 0.15);
      overflow: hidden;
      border: 2px solid #fbbf24;
    }

    .panel-header {
      background: linear-gradient(135deg, #fbbf24, #f59e0b);
      color: white;
      padding: 16px 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      h3 {
        margin: 0;
        font-size: 16px;
        font-weight: 600;
        display: flex;
        align-items: center;
        gap: 8px;
      }

      .clear-all-btn {
        background: rgba(255, 255, 255, 0.2);
        border: none;
        color: white;
        padding: 6px 12px;
        border-radius: 8px;
        font-size: 12px;
        cursor: pointer;
        transition: all 0.2s ease;
        display: flex;
        align-items: center;
        gap: 6px;

        &:hover {
          background: rgba(255, 255, 255, 0.3);
        }
      }
    }

    .alerts-list {
      max-height: 300px;
      overflow-y: auto;
    }

    .stock-alert-item {
      padding: 16px 20px;
      border-bottom: 1px solid #f3f4f6;
      display: flex;
      align-items: center;
      gap: 12px;
      transition: all 0.2s ease;
      cursor: pointer;

      &:hover {
        background: #f9fafb;
        transform: translateX(4px);
      }

      &:last-child {
        border-bottom: none;
      }
    }

    .alert-low { border-left: 4px solid #f59e0b; }
    .alert-critical { border-left: 4px solid #ef4444; }
    .alert-out { border-left: 4px solid #dc2626; background: #fef2f2; }

    .alert-icon {
      width: 32px;
      height: 32px;
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 16px;
    }

    .alert-low .alert-icon { background: #fef3c7; color: #d97706; }
    .alert-critical .alert-icon { background: #fee2e2; color: #dc2626; }
    .alert-out .alert-icon { background: #fecaca; color: #b91c1c; }

    .alert-content {
      flex: 1;
      min-width: 0;
    }

    .alert-product {
      font-weight: 600;
      color: #1f2937;
      margin-bottom: 4px;
      font-size: 14px;
    }

    .alert-details {
      display: flex;
      flex-direction: column;
      gap: 4px;
      font-size: 12px;
      color: #6b7280;

      .current-stock {
        font-weight: 600;
      }

      .alert-severity {
        font-weight: 600;
        font-size: 10px;
        padding: 2px 6px;
        border-radius: 4px;
        text-transform: uppercase;
        letter-spacing: 0.5px;

        &.severity-low {
          background: #fef3c7;
          color: #d97706;
        }

        &.severity-critical {
          background: #fee2e2;
          color: #dc2626;
        }

        &.severity-out {
          background: #fecaca;
          color: #b91c1c;
          animation: pulse 2s infinite;
        }
      }
    }

    @keyframes pulse {
      0%, 100% { opacity: 1; }
      50% { opacity: 0.7; }
    }

    .alert-actions {
      display: flex;
      gap: 8px;
    }

    .action-btn {
      width: 32px;
      height: 32px;
      border: none;
      border-radius: 8px;
      cursor: pointer;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 14px;
      transition: all 0.2s ease;

      &.restock-btn {
        background: #ecfdf5;
        color: #059669;

        &:hover {
          background: #d1fae5;
          transform: scale(1.1);
        }
      }

      &.dismiss-btn {
        background: #f3f4f6;
        color: #6b7280;

        &:hover {
          background: #e5e7eb;
          color: #374151;
        }
      }
    }

    @media (max-width: 480px) {
      .notification-container {
        left: 20px;
        right: 20px;
        max-width: none;
      }
    }
  `]
})
export class NotificationPanelComponent implements OnInit, OnDestroy {
  notifications: Notification[] = [];
  stockAlerts: StockAlert[] = [];
  
  private subscriptions: Subscription[] = [];

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    // S'abonner aux notifications
    this.subscriptions.push(
      this.notificationService.getNotifications().subscribe(notifications => {
        this.notifications = notifications;
      })
    );

    // S'abonner aux alertes de stock
    this.subscriptions.push(
      this.notificationService.getStockAlerts().subscribe(alerts => {
        this.stockAlerts = alerts;
      })
    );
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(sub => sub.unsubscribe());
  }

  closeNotification(id: string): void {
    this.notificationService.removeNotification(id);
  }

  dismissStockAlert(productId: number): void {
    this.notificationService.removeStockAlert(productId);
    this.notificationService.showInfo(
      'Alerte ignorée',
      'L\'alerte de stock a été supprimée'
    );
  }

  clearAllStockAlerts(): void {
    const count = this.stockAlerts.length;
    this.notificationService.clearAllStockAlerts();
    this.notificationService.showSuccess(
      'Alertes effacées',
      `${count} alerte(s) de stock supprimée(s)`
    );
  }

  openRestockDialog(alert: StockAlert): void {
    const quantity = prompt(`Ajouter du stock pour "${alert.productName}" ?\nStock actuel: ${alert.currentStock}\nQuantité à ajouter:`, '10');
    
    if (quantity && !isNaN(Number(quantity))) {
      const addedQuantity = Number(quantity);
      const newStock = alert.currentStock + addedQuantity;
      
      this.notificationService.showSuccess(
        'Stock mis à jour',
        `"${alert.productName}": ${alert.currentStock} → ${newStock} (+${addedQuantity})`
      );
      
      // Supprimer l'alerte si le stock est maintenant suffisant
      if (newStock > alert.threshold) {
        this.dismissStockAlert(alert.productId);
      }
    }
  }

  getIconClass(type: string): string {
    switch (type) {
      case 'success': return 'fas fa-check-circle';
      case 'error': return 'fas fa-times-circle';
      case 'warning': return 'fas fa-exclamation-triangle';
      case 'info': return 'fas fa-info-circle';
      case 'stock-alert': return 'fas fa-boxes';
      default: return 'fas fa-bell';
    }
  }

  getStockIconClass(severity: string): string {
    switch (severity) {
      case 'out': return 'fas fa-times-circle';
      case 'critical': return 'fas fa-exclamation-triangle';
      case 'low': return 'fas fa-exclamation-circle';
      default: return 'fas fa-info-circle';
    }
  }

  // NOUVEAU: Clic sur l'alerte pour voir les détails du produit
  onStockAlertClick(alert: StockAlert): void {
    const message = `
📦 Produit: ${alert.productName}
📊 Stock actuel: ${alert.currentStock}
⚠️ Seuil d'alerte: ${alert.threshold}
🚨 Statut: ${this.getStatusText(alert.severity)}

💡 Actions recommandées:
• Vérifier les commandes en cours
• Passer une commande fournisseur
• Mettre à jour le seuil d'alerte
`;

    this.notificationService.showInfo(
      `🔍 Détails - ${alert.productName}`,
      message
    );
  }

  // Méthode publique pour obtenir le texte du statut
  getStatusText(severity: string): string {
    switch (severity) {
      case 'out': return 'STOCK ÉPUISÉ 🚨';
      case 'critical': return 'STOCK CRITIQUE ⚠️';
      case 'low': return 'STOCK FAIBLE 📉';
      default: return 'NORMAL ✅';
    }
  }
} 