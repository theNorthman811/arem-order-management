import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { Order, Measure } from '../models/order.model';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = `${environment.apiUrl}/api/v1/orders`;

  constructor(private http: HttpClient) {}

  getAllOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(this.apiUrl).pipe(
      catchError(this.handleError)
    );
  }

  getOrders(): Observable<Order[]> {
    console.log('Fetching orders from:', this.apiUrl);
    return this.http.get<Order[]>(this.apiUrl).pipe(
      tap(orders => console.log('Fetched orders:', orders)),
      catchError(this.handleError)
    );
  }

  getMyOrders(): Observable<Order[]> {
    console.log('Fetching my orders from:', `${this.apiUrl}/my-orders`);
    return this.http.get<Order[]>(`${this.apiUrl}/my-orders`).pipe(
      tap(orders => console.log('Fetched my orders:', orders)),
      catchError(this.handleError)
    );
  }

  /**
   * Processus de checkout simplifié - transforme le panier en commande
   */
  checkout(): Observable<any> {
    console.log('Processing checkout...');
    return this.http.post<any>(`${this.apiUrl}/checkout`, {}).pipe(
      tap(response => console.log('Checkout response:', response)),
      catchError(this.handleError)
    );
  }

  getOrder(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/${id}`).pipe(
      catchError(this.handleError)
    );
  }

  createOrder(customerId: number): Observable<Order> {
    return this.http.post<Order>(`${this.apiUrl}/create`, null, {
      params: {
        customerId: customerId.toString()
      }
    }).pipe(
      catchError(this.handleError)
    );
  }

  updateOrderStatus(orderId: number, status: string): Observable<Order> {
    return this.http.put<Order>(`${this.apiUrl}/${orderId}/status`, { status }).pipe(
      catchError(this.handleError)
    );
  }

  deleteOrder(orderId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${orderId}`).pipe(
      catchError(this.handleError)
    );
  }

  addItemToOrder(orderId: number, productId: number, quantity: number): Observable<Order> {
    console.log('Adding item to order:', { orderId, productId, quantity });
    return this.http.post<Order>(`${this.apiUrl}/${orderId}/items`, null, {
      params: {
        productId: productId.toString(),
        quantity: quantity.toString()
      }
    }).pipe(
      tap(order => console.log('Added item to order:', order)),
      catchError(error => {
        console.error('Error adding item to order:', error);
        return this.handleError(error);
      })
    );
  }

  updateOrderItem(orderId: number, itemId: number, quantity: number): Observable<Order> {
    return this.http.put<Order>(`${this.apiUrl}/${orderId}/items/${itemId}`, {
      quantity
    }).pipe(
      tap(order => console.log('Updated order item:', order)),
      catchError(this.handleError)
    );
  }

  removeItemFromOrder(orderId: number, itemId: number): Observable<Order> {
    return this.http.delete<Order>(`${this.apiUrl}/${orderId}/items/${itemId}`).pipe(
      catchError(this.handleError)
    );
  }

  updateOrder(orderId: number, order: Order): Observable<Order> {
    return this.http.put<Order>(`${this.apiUrl}/${orderId}`, order).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    console.error('An error occurred:', error);
    let errorMessage = 'Une erreur est survenue';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = error.error.message;
    } else {
      // Server-side error
      if (error.error?.message) {
        errorMessage = error.error.message;
      } else if (error.status === 0) {
        errorMessage = 'Impossible de contacter le serveur';
      } else if (error.status === 404) {
        errorMessage = 'Ressource non trouvée';
      }
    }
    
    return throwError(() => new Error(errorMessage));
  }
} 