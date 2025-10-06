
























































































































import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CartItem } from '../../models/cart.model';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-cart-item',
  templateUrl: './cart-item.component.html',
  styleUrls: ['./cart-item.component.scss']
})
export class CartItemComponent {
  @Input() item!: CartItem;
  @Output() quantityChanged = new EventEmitter<CartItem>();
  @Output() itemRemoved = new EventEmitter<CartItem>();
  
  isUpdating = false;
  
  constructor(private cartService: CartService) { }
  
  updateQuantity(quantity: number): void {
    if (quantity < 1) return;
    
    this.isUpdating = true;
    
    this.cartService.updateCartItem(this.item.productId, quantity).subscribe({
      next: () => {
        this.isUpdating = false;
        this.quantityChanged.emit(this.item);
      },
      error: (error) => {
        console.error('Error updating cart item:', error);
        this.isUpdating = false;
      }
    });
  }
  
  removeItem(): void {
    this.isUpdating = true;
    
    this.cartService.removeFromCart(this.item.productId).subscribe({
      next: () => {
        this.isUpdating = false;
        this.itemRemoved.emit(this.item);
      },
      error: (error) => {
        console.error('Error removing cart item:', error);
        this.isUpdating = false;
      }
    });
  }
  
  incrementQuantity(): void {
    this.updateQuantity(this.item.quantity + 1);
  }
  
  decrementQuantity(): void {
    if (this.item.quantity > 1) {
      this.updateQuantity(this.item.quantity - 1);
    }
  }
}
























































































































