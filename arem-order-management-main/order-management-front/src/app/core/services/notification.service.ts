import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';

export interface Notification {
  id: string;
  type: 'success' | 'warning' | 'error' | 'info' | 'stock-alert';
  title: string;
  message: string;
  timestamp: Date;
  productId?: number;
  productName?: string;
  currentStock?: number;
  threshold?: number;
  autoClose?: boolean;
  duration?: number;
}

export interface StockAlert {
  productId: number;
  productName: string;
  currentStock: number;
  threshold: number;
  severity: 'low' | 'critical' | 'out';
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private notifications$ = new BehaviorSubject<Notification[]>([]);
  private stockAlerts$ = new BehaviorSubject<StockAlert[]>([]);
  
  constructor() {
    console.log('🔔 NotificationService initialisé');
  }

  // Observables publics
  getNotifications(): Observable<Notification[]> {
    return this.notifications$.asObservable();
  }

  getStockAlerts(): Observable<StockAlert[]> {
    return this.stockAlerts$.asObservable();
  }

  // Méthodes de notification
  showSuccess(title: string, message: string, autoClose = true): void {
    this.addNotification({
      type: 'success',
      title,
      message,
      autoClose,
      duration: 3000
    });
  }

  showError(title: string, message: string, autoClose = false): void {
    this.addNotification({
      type: 'error',
      title,
      message,
      autoClose,
      duration: 5000
    });
  }

  showWarning(title: string, message: string, autoClose = true): void {
    this.addNotification({
      type: 'warning',
      title,
      message,
      autoClose,
      duration: 4000
    });
  }

  showInfo(title: string, message: string, autoClose = true): void {
    this.addNotification({
      type: 'info',
      title,
      message,
      autoClose,
      duration: 3000
    });
  }

  // Alerte de stock spécialisée avec navigation
  showStockAlert(productId: number, productName: string, currentStock: number, threshold: number): void {
    const severity = this.getStockSeverity(currentStock, threshold);
    const emoji = severity === 'out' ? '🚨' : severity === 'critical' ? '⚠️' : '📉';
    
    // Ajouter à la liste des alertes stock
    const stockAlert: StockAlert = {
      productId,
      productName,
      currentStock,
      threshold,
      severity
    };
    
    const currentAlerts = this.stockAlerts$.value;
    const existingIndex = currentAlerts.findIndex(alert => alert.productId === productId);
    
    if (existingIndex >= 0) {
      currentAlerts[existingIndex] = stockAlert;
    } else {
      currentAlerts.push(stockAlert);
    }
    
    this.stockAlerts$.next([...currentAlerts]);

    // Messages intelligents selon la gravité
    let actionMessage = '';
    switch (severity) {
      case 'out':
        actionMessage = '🚨 URGENT: Commande fournisseur requise immédiatement';
        break;
      case 'critical':
        actionMessage = '⚠️ ATTENTION: Planifier une commande fournisseur rapidement';
        break;
      case 'low':
        actionMessage = '📉 INFO: Surveiller et prévoir un réapprovisionnement';
        break;
    }

    // Notification toast avec action
    this.addNotification({
      type: 'stock-alert',
      title: `${emoji} Alerte Stock - ${productName}`,
      message: `Stock: ${currentStock}/${threshold} - ${actionMessage}`,
      productId,
      productName,
      currentStock,
      threshold,
      autoClose: severity === 'low', // Auto-close seulement pour les alertes légères
      duration: severity === 'out' ? 10000 : 5000 // Plus long pour les critiques
    });

    // Son d'alerte (si critique)
    if (severity === 'out' || severity === 'critical') {
      this.playAlertSound();
    }
  }

  // NOUVELLE: Notification avec action de commande fournisseur
  showSupplierOrderSuggestion(productId: number, productName: string, currentStock: number): void {
    this.addNotification({
      type: 'info',
      title: '🏭 Suggestion de commande',
      message: `"${productName}" - Stock: ${currentStock}. Cliquez pour voir les fournisseurs disponibles.`,
      productId,
      productName,
      currentStock,
      autoClose: false
    });
  }

  // Vérification automatique des stocks
  checkStockLevels(products: any[]): void {
    const lowStockProducts = products.filter(p => this.isLowStock(p.quantity, p.alertThreshold || 10));
    
    // Supprimer les alertes pour les produits qui ne sont plus en stock faible
    const currentAlerts = this.stockAlerts$.value;
    const validAlerts = currentAlerts.filter(alert => 
      lowStockProducts.some(p => p.id === alert.productId)
    );
    
    // Ajouter de nouvelles alertes
    lowStockProducts.forEach(product => {
      const threshold = product.alertThreshold || 10;
      const existingAlert = validAlerts.find(alert => alert.productId === product.id);
      
      if (!existingAlert) {
        this.showStockAlert(product.id, product.name, product.quantity, threshold);
      }
    });
    
    this.stockAlerts$.next(validAlerts);
  }

  // Utilitaires
  private addNotification(notification: Partial<Notification>): void {
    const newNotification: Notification = {
      id: this.generateId(),
      timestamp: new Date(),
      autoClose: true,
      duration: 3000,
      ...notification
    } as Notification;

    const currentNotifications = this.notifications$.value;
    this.notifications$.next([newNotification, ...currentNotifications]);

    // Auto-suppression
    if (newNotification.autoClose) {
      setTimeout(() => {
        this.removeNotification(newNotification.id);
      }, newNotification.duration);
    }
  }

  removeNotification(id: string): void {
    const currentNotifications = this.notifications$.value;
    this.notifications$.next(currentNotifications.filter(n => n.id !== id));
  }

  removeStockAlert(productId: number): void {
    const currentAlerts = this.stockAlerts$.value;
    this.stockAlerts$.next(currentAlerts.filter(alert => alert.productId !== productId));
  }

  clearAllNotifications(): void {
    this.notifications$.next([]);
  }

  clearAllStockAlerts(): void {
    this.stockAlerts$.next([]);
  }

  // Helpers
  private isLowStock(currentStock: number, threshold: number): boolean {
    return currentStock <= threshold;
  }

  private getStockSeverity(currentStock: number, threshold: number): 'low' | 'critical' | 'out' {
    if (currentStock <= 0) return 'out';
    if (currentStock <= threshold * 0.3) return 'critical';
    return 'low';
  }

  private generateId(): string {
    return Date.now().toString() + Math.random().toString(36).substr(2, 9);
  }

  private playAlertSound(): void {
    try {
      // Son d'alerte simple (optionnel)
      const audioContext = new (window.AudioContext || (window as any).webkitAudioContext)();
      const oscillator = audioContext.createOscillator();
      const gainNode = audioContext.createGain();
      
      oscillator.connect(gainNode);
      gainNode.connect(audioContext.destination);
      
      oscillator.frequency.setValueAtTime(800, audioContext.currentTime);
      gainNode.gain.setValueAtTime(0.1, audioContext.currentTime);
      
      oscillator.start();
      oscillator.stop(audioContext.currentTime + 0.2);
    } catch (error) {
      console.log('Son d\'alerte non disponible');
    }
  }

  // Statistiques
  getNotificationStats(): { total: number; stockAlerts: number; unread: number } {
    const notifications = this.notifications$.value;
    const stockAlerts = this.stockAlerts$.value;
    
    return {
      total: notifications.length,
      stockAlerts: stockAlerts.length,
      unread: notifications.length + stockAlerts.length
    };
  }
} 