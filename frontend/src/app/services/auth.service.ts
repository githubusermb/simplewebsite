

































import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from '../../environments/environment';
import { Customer } from '../models/customer.model';

interface AuthResponse {
  token: string;
  customer: Customer;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = `${environment.apiBaseUrl}/customers`;
  private tokenKey = 'auth_token';
  private userKey = 'current_user';
  
  private currentUserSubject: BehaviorSubject<Customer | null>;
  public currentUser$: Observable<Customer | null>;
  
  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.currentUserSubject = new BehaviorSubject<Customer | null>(this.getUserFromStorage());
    this.currentUser$ = this.currentUserSubject.asObservable();
  }
  
  // Get current user value
  public get currentUserValue(): Customer | null {
    return this.currentUserSubject.value;
  }
  
  // Register new user
  register(userData: Partial<Customer>): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}`, userData).pipe(
      tap(response => {
        this.setSession(response);
        this.currentUserSubject.next(response.customer);
      }),
      catchError(error => {
        return throwError(() => new Error(error.error?.message || 'Registration failed'));
      })
    );
  }
  
  // Login user
  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, { email, password }).pipe(
      tap(response => {
        this.setSession(response);
        this.currentUserSubject.next(response.customer);
      }),
      catchError(error => {
        return throwError(() => new Error(error.error?.message || 'Login failed'));
      })
    );
  }
  
  // Logout user
  logout(): void {
    localStorage.removeItem(this.tokenKey);
    localStorage.removeItem(this.userKey);
    this.currentUserSubject.next(null);
    this.router.navigate(['/login']);
  }
  
  // Check if user is authenticated
  isAuthenticated(): boolean {
    const token = this.getToken();
    // Check if token exists and is not expired
    return !!token; // In a real app, you would also check if the token is expired
  }
  
  // Get authentication token
  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }
  
  // Store authentication data
  private setSession(authResult: AuthResponse): void {
    localStorage.setItem(this.tokenKey, authResult.token);
    localStorage.setItem(this.userKey, JSON.stringify(authResult.customer));
  }
  
  // Get user from storage
  private getUserFromStorage(): Customer | null {
    const user = localStorage.getItem(this.userKey);
    return user ? JSON.parse(user) : null;
  }
}

































