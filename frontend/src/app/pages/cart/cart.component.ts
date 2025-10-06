












































































































































































































import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { Cart, CartItem } from '../../models/cart.model';
import { CartService } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.scss']
})
export class CartComponent implements OnInit {
  cart$: Observable<Cart | null>;
  isLoading = true;
  error: string | null = null;
  isAuthenticated = false;
  
  constructor(
    private cartService: CartService,
    private authService: AuthService,
    private router: Router
  ) {
    this.cart$ = this.cartService.cart$;
  }

  ngOnInit(): void {
    this.isAuthenticated = this.authService.isAuthenticated();
    
    // If user is authenticated, load their cart
    if (this.isAuthenticated) {
      this.loadCart();
    } else {
      this.isLoading = false;
    }
  }
  
  loadCart(): void {
    this.isLoading = true;
    
    this.cartService.loadCart();
    
    // Subscribe to cart changes to know when loading is complete
    const subscription = this.cart$.subscribe({
      next: (cart) => {
        this.isLoading = false;
        subscription.unsubscribe();
      },
      error: (error) => {
        console.error('Error loading cart:', error);
        this.error = 'Failed to load your cart. Please try again later.';
        this.isLoading = false;
        subscription.unsubscribe();
      }
    });
  }
  
  onQuantityChanged(item: CartItem): void {
    // The cart service will update the cart$ observable
    console.log(`Quantity changed for ${item.name}`);
  }
  
  onItemRemoved(item: CartItem): void {
    // The cart service will update the cart$ observable
    console.log(`Removed ${item.name} from cart`);
  }
  
  clearCart(): void {
    if (confirm('Are you sure you want to clear your cart?')) {
      this.cartService.clearCart().subscribe({
        error: (error) => {
          console.error('Error clearing cart:', error);
        }
      });
    }
  }
  
  proceedToCheckout(): void {
    if (this.isAuthenticated) {
      this.router.navigate(['/checkout']);
    } else {
      // Redirect to login with return URL
      this.router.navigate(['/login'], { 
        queryParams: { returnUrl: '/checkout' }
      });
    }
  }
  
  continueShopping(): void {
    this.router.navigate(['/products']);
  }
}












































































































































































































