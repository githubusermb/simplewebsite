








































































































































































import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Order } from '../../models/order.model';
import { OrderService } from '../../services/order.service';

@Component({
  selector: 'app-order-confirmation',
  templateUrl: './order-confirmation.component.html',
  styleUrls: ['./order-confirmation.component.scss']
})
export class OrderConfirmationComponent implements OnInit {
  order: Order | null = null;
  isLoading = true;
  error: string | null = null;
  
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const orderId = params['id'];
      this.loadOrder(orderId);
    });
  }
  
  loadOrder(orderId: string): void {
    this.isLoading = true;
    
    this.orderService.getOrder(orderId).subscribe({
      next: (order) => {
        this.order = order;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading order:', error);
        this.error = 'Failed to load order details. Please try again later.';
        this.isLoading = false;
      }
    });
  }
  
  continueShopping(): void {
    this.router.navigate(['/products']);
  }
  
  viewOrders(): void {
    this.router.navigate(['/orders']);
  }
  
  getOrderDate(): string {
    if (!this.order) return '';
    return new Date(this.order.createdAt).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }
  
  getEstimatedDelivery(): string {
    if (!this.order) return '';
    
    // Calculate estimated delivery date (5-7 business days from order date)
    const orderDate = new Date(this.order.createdAt);
    const deliveryDate = new Date(orderDate);
    deliveryDate.setDate(orderDate.getDate() + 7); // Simple estimation
    
    return deliveryDate.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }
}








































































































































































