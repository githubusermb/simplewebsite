

















































import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Order } from '../models/order.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private apiUrl = `${environment.apiBaseUrl}/orders`;
  
  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) { }
  
  // Create a new order
  createOrder(orderData: Partial<Order>): Observable<Order> {
    return this.http.post<Order>(this.apiUrl, orderData);
  }
  
  // Get order by ID
  getOrder(orderId: string): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/${orderId}`);
  }
  
  // Get all orders for current user
  getUserOrders(): Observable<Order[]> {
    const user = this.authService.currentUserValue;
    if (!user) {
      throw new Error('User must be logged in to view orders');
    }
    
    return this.http.get<Order[]>(`${environment.apiBaseUrl}/customers/${user.customerId}/orders`);
  }
  
  // Update order status (admin only)
  updateOrderStatus(orderId: string, status: string): Observable<Order> {
    return this.http.put<Order>(`${this.apiUrl}/${orderId}`, { status });
  }
  
  // Cancel order
  cancelOrder(orderId: string): Observable<Order> {
    return this.http.put<Order>(`${this.apiUrl}/${orderId}/cancel`, {});
  }
}

















































