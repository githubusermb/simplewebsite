





































































































































































































































import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { Cart } from '../../models/cart.model';
import { Customer } from '../../models/customer.model';
import { Order, PaymentMethod } from '../../models/order.model';
import { CartService } from '../../services/cart.service';
import { OrderService } from '../../services/order.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-checkout',
  templateUrl: './checkout.component.html',
  styleUrls: ['./checkout.component.scss']
})
export class CheckoutComponent implements OnInit {
  cart$: Observable<Cart | null>;
  currentUser: Customer | null = null;
  
  shippingForm: FormGroup;
  paymentForm: FormGroup;
  
  currentStep = 1;
  isSubmitting = false;
  error: string | null = null;
  
  paymentMethods = [
    { id: PaymentMethod.CREDIT_CARD, name: 'Credit Card' },
    { id: PaymentMethod.PAYPAL, name: 'PayPal' }
  ];
  
  constructor(
    private fb: FormBuilder,
    private cartService: CartService,
    private orderService: OrderService,
    private authService: AuthService,
    private router: Router
  ) {
    this.cart$ = this.cartService.cart$;
    
    // Initialize forms
    this.shippingForm = this.fb.group({
      fullName: ['', [Validators.required]],
      addressLine1: ['', [Validators.required]],
      addressLine2: [''],
      city: ['', [Validators.required]],
      state: ['', [Validators.required]],
      postalCode: ['', [Validators.required]],
      country: ['', [Validators.required]]
    });
    
    this.paymentForm = this.fb.group({
      paymentMethod: [PaymentMethod.CREDIT_CARD, [Validators.required]],
      cardNumber: ['', [Validators.required, Validators.pattern(/^\d{16}$/)]],
      cardName: ['', [Validators.required]],
      expiryDate: ['', [Validators.required, Validators.pattern(/^(0[1-9]|1[0-2])\/\d{2}$/)]],
      cvv: ['', [Validators.required, Validators.pattern(/^\d{3,4}$/)]]
    });
  }

  ngOnInit(): void {
    // Get current user
    this.authService.currentUser$.subscribe(user => {
      this.currentUser = user;
      
      // Pre-fill shipping form if user has address
      if (user && user.address) {
        this.shippingForm.patchValue({
          fullName: `${user.firstName} ${user.lastName}`,
          addressLine1: user.address.addressLine1,
          addressLine2: user.address.addressLine2 || '',
          city: user.address.city,
          state: user.address.state,
          postalCode: user.address.postalCode,
          country: user.address.country
        });
      }
    });
  }
  
  nextStep(): void {
    if (this.currentStep === 1 && this.shippingForm.valid) {
      this.currentStep = 2;
      window.scrollTo(0, 0);
    }
  }
  
  prevStep(): void {
    if (this.currentStep === 2) {
      this.currentStep = 1;
      window.scrollTo(0, 0);
    }
  }
  
  placeOrder(): void {
    if (this.shippingForm.invalid || this.paymentForm.invalid) {
      return;
    }
    
    const cart = this.cartService.currentCartValue;
    if (!cart || !this.currentUser) {
      this.error = 'Unable to place order. Please try again later.';
      return;
    }
    
    this.isSubmitting = true;
    
    // Create order object
    const order = {
      customerId: this.currentUser.customerId,
      items: cart.items.map(item => ({
        productId: item.productId,
        name: item.name,
        price: item.price,
        quantity: item.quantity
      })),
      totalAmount: cart.totalPrice,
      shippingAddress: {
        fullName: this.shippingForm.value.fullName,
        addressLine1: this.shippingForm.value.addressLine1,
        addressLine2: this.shippingForm.value.addressLine2,
        city: this.shippingForm.value.city,
        state: this.shippingForm.value.state,
        postalCode: this.shippingForm.value.postalCode,
        country: this.shippingForm.value.country
      },
      paymentMethod: this.paymentForm.value.paymentMethod
    };
    
    this.orderService.createOrder(order).subscribe({
      next: (createdOrder: Order) => {
        this.isSubmitting = false;
        // Navigate to order confirmation page
        this.router.navigate(['/order-confirmation', createdOrder.orderId]);
      },
      error: (error) => {
        console.error('Error creating order:', error);
        this.error = 'Failed to place your order. Please try again later.';
        this.isSubmitting = false;
      }
    });
  }
  
  // Form getters for validation
  get sf() { return this.shippingForm.controls; }
  get pf() { return this.paymentForm.controls; }
}





































































































































































































































