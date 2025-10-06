
















































































































































































import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Product } from '../../models/product.model';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
  styleUrls: ['./product-detail.component.scss']
})
export class ProductDetailComponent implements OnInit {
  product: Product | null = null;
  quantity = 1;
  isLoading = true;
  error: string | null = null;
  isAddingToCart = false;
  
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private cartService: CartService
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const productId = params['id'];
      this.loadProduct(productId);
    });
  }
  
  loadProduct(productId: string): void {
    this.isLoading = true;
    
    this.productService.getProduct(productId).subscribe({
      next: (product) => {
        this.product = product;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading product:', error);
        this.error = 'Failed to load product details. Please try again later.';
        this.isLoading = false;
      }
    });
  }
  
  incrementQuantity(): void {
    if (this.product && this.quantity < this.product.inventory) {
      this.quantity++;
    }
  }
  
  decrementQuantity(): void {
    if (this.quantity > 1) {
      this.quantity--;
    }
  }
  
  addToCart(): void {
    if (!this.product) return;
    
    this.isAddingToCart = true;
    
    this.cartService.addToCart(this.product, this.quantity).subscribe({
      next: () => {
        this.isAddingToCart = false;
        // You could show a success message or navigate to cart
      },
      error: (error) => {
        console.error('Error adding product to cart:', error);
        this.isAddingToCart = false;
      }
    });
  }
  
  goBack(): void {
    this.router.navigate(['/products']);
  }
}
















































































































































































