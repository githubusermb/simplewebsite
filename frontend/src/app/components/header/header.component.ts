

























































import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';
import { Customer } from '../../models/customer.model';
import { Cart } from '../../models/cart.model';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  currentUser$: Observable<Customer | null>;
  cart$: Observable<Cart | null>;
  isMenuOpen = false;
  
  constructor(
    private authService: AuthService,
    private cartService: CartService,
    private router: Router
  ) {
    this.currentUser$ = this.authService.currentUser$;
    this.cart$ = this.cartService.cart$;
  }

  ngOnInit(): void {
  }
  
  toggleMenu(): void {
    this.isMenuOpen = !this.isMenuOpen;
  }
  
  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}

























































