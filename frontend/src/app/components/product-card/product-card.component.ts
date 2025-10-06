





































































































import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Product } from '../../models/product.model';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-product-card',
  templateUrl: './product-card.component.html',
  styleUrls: ['./product-card.component.scss']
})
export class ProductCardComponent {
  @Input() product!: Product;
  @Output() addedToCart = new EventEmitter<Product>();
  
  isAddingToCart = false;
  
  constructor(private cartService: CartService) { }
  
  addToCart(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    
    this.isAddingToCart = true;
    
    this.cartService.addToCart(this.product, 1).subscribe({
      next: () => {
        this.isAddingToCart = false;
        this.addedToCart.emit(this.product);
      },
      error: (error) => {
        console.error('Error adding product to cart:', error);
        this.isAddingToCart = false;
      }
    });
  }
}





































































































