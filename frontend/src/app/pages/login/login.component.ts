
























































































































































































import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  isSubmitting = false;
  error: string | null = null;
  returnUrl: string = '/';
  
  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private cartService: CartService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    // Initialize form
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
    
    // Redirect to home if already logged in
    if (this.authService.isAuthenticated()) {
      this.router.navigate(['/']);
    }
  }

  ngOnInit(): void {
    // Get return url from route parameters or default to '/'
    this.returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';
  }
  
  onSubmit(): void {
    if (this.loginForm.invalid) {
      return;
    }
    
    this.isSubmitting = true;
    this.error = null;
    
    const { email, password } = this.loginForm.value;
    
    this.authService.login(email, password).subscribe({
      next: () => {
        this.isSubmitting = false;
        
        // Load user's cart
        this.cartService.loadCart();
        
        // Navigate to return url
        this.router.navigateByUrl(this.returnUrl);
      },
      error: (error) => {
        this.isSubmitting = false;
        this.error = error.message || 'Invalid email or password. Please try again.';
      }
    });
  }
  
  // Form getters for validation
  get f() { return this.loginForm.controls; }
}
























































































































































































