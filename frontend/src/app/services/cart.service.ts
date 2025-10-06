












































import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { Cart, CartItem } from '../models/cart.model';
import { Product } from '../models/product.model';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = `${environment.apiBaseUrl}/carts`;
  private cartSubject = new BehaviorSubject<Cart | null>(null);
  public cart$ = this.cartSubject.asObservable();
  
  constructor(
    private http: HttpClient,
    private authService: AuthService
  ) {
    // Load cart when user logs in
    this.authService.currentUser$.subscribe(user => {
      if (user) {
        this.loadCart();
      } else {
        this.cartSubject.next(null);
      }
    });
  }
  
  // Get current cart value
  public get currentCartValue(): Cart | null {
    return this.cartSubject.value;
  }
  
  // Load user's active cart
  loadCart(): void {
    const user = this.authService.currentUserValue;
    if (user) {
      this.http.get<Cart>(`${this.apiUrl}/customer/${user.customerId}/active`)
        .subscribe({
          next: (cart) => {
            this.cartSubject.next(cart);
          },
          error: (error) => {
            console.error('Error loading cart:', error);
            // If no cart exists, create a new one
            if (error.status === 404) {
              this.createCart();
            }
          }
        });
    }
  }
  
  // Create a new cart
  createCart(): Observable<Cart> {
    const user = this.authService.currentUserValue;
    if (!user) {
      throw new Error('User must be logged in to create a cart');
    }
    
    return this.http.post<Cart>(this.apiUrl, { customerId: user.customerId }).pipe(
      tap(cart => {
        this.cartSubject.next(cart);
      })
    );
  }
  
  // Get cart by ID
  getCart(cartId: string): Observable<Cart> {
    return this.http.get<Cart>(`${this.apiUrl}/${cartId}`);
  }
  
  // Add item to cart
  addToCart(product: Product, quantity: number = 1): Observable<Cart> {
    const cart = this.currentCartValue;
    if (!cart) {
      throw new Error('No active cart found');
    }
    
    const item = {
      productId: product.productId,
      quantity: quantity
    };
    
    return this.http.post<Cart>(`${this.apiUrl}/${cart.cartId}/items`, item).pipe(
      tap(updatedCart => {
        this.cartSubject.next(updatedCart);
      })
    );
  }
  
  // Update cart item quantity
  updateCartItem(productId: string, quantity: number): Observable<Cart> {
    const cart = this.currentCartValue;
    if (!cart) {
      throw new Error('No active cart found');
    }
    
    return this.http.put<Cart>(`${this.apiUrl}/${cart.cartId}/items/${productId}`, { quantity }).pipe(
      tap(updatedCart => {
        this.cartSubject.next(updatedCart);
      })
    );
  }
  
  // Remove item from cart
  removeFromCart(productId: string): Observable<Cart> {
    const cart = this.currentCartValue;
    if (!cart) {
      throw new Error('No active cart found');
    }
    
    return this.http.delete<Cart>(`${this.apiUrl}/${cart.cartId}/items/${productId}`).pipe(
      tap(updatedCart => {
        this.cartSubject.next(updatedCart);
      })
    );
  }
  
  // Clear cart
  clearCart(): Observable<Cart> {
    const cart = this.currentCartValue;
    if (!cart) {
      throw new Error('No active cart found');
    }
    
    return this.http.delete<Cart>(`${this.apiUrl}/${cart.cartId}/items`).pipe(
      tap(emptyCart => {
        this.cartSubject.next(emptyCart);
      })
    );
  }
}












































