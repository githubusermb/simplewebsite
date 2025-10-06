








































import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Category } from '../models/category.model';

@Injectable({
  providedIn: 'root'
})
export class CategoryService {
  private apiUrl = `${environment.apiBaseUrl}/categories`;
  
  constructor(private http: HttpClient) { }
  
  // Get all categories
  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.apiUrl);
  }
  
  // Get category by ID
  getCategory(categoryId: string): Observable<Category> {
    return this.http.get<Category>(`${this.apiUrl}/${categoryId}`);
  }
  
  // Create category (admin only)
  createCategory(category: Partial<Category>): Observable<Category> {
    return this.http.post<Category>(this.apiUrl, category);
  }
  
  // Update category (admin only)
  updateCategory(categoryId: string, category: Partial<Category>): Observable<Category> {
    return this.http.put<Category>(`${this.apiUrl}/${categoryId}`, category);
  }
  
  // Delete category (admin only)
  deleteCategory(categoryId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${categoryId}`);
  }
}








































