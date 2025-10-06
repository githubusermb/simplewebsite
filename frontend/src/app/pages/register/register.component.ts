





























































































































































































import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
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
    this.registerForm = this.fb.group({
      firstName: ['', [Validators.required]],
      lastName: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]]
    }, {
      validators: this.passwordMatchValidator
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
  
  // Custom validator to check if passwords match
  passwordMatchValidator(formGroup: FormGroup) {
    const password = formGroup.get('password')?.value;
    const confirmPassword = formGroup.get('confirmPassword')?.value;
    
    if (password !== confirmPassword) {
      formGroup.get('confirmPassword')?.setErrors({ passwordMismatch: true });
      return { passwordMismatch: true };
    } else {
      formGroup.get('confirmPassword')?.setErrors(null);
      return null;
    }
  }
  
  onSubmit(): void {
    if (this.registerForm.invalid) {
      return;
    }
    
    this.isSubmitting = true;
    this.error = null;
    
    const { firstName, lastName, email, password } = this.registerForm.value;
    
    const userData = {
      firstName,
      lastName,
      email,
      password
    };
    
    this.authService.register(userData).subscribe({
      next: () => {
        this.isSubmitting = false;
        
        // Auto login after registration
        this.authService.login(email, password).subscribe({
          next: () => {
            // Initialize empty cart for new user
            this.cartService.loadCart();
            
            // Navigate to return url
            this.router.navigateByUrl(this.returnUrl);
          }
        });
      },
      error: (error) => {
        this.isSubmitting = false;
        this.error = error.message || 'Registration failed. Please try again.';
      }
    });
  }
  
  // Form getters for validation
  get f() { return this.registerForm.controls; }
}





























































































































































































