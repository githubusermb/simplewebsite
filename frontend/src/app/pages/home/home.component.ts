











































































































































import { Component, OnInit } from '@angular/core';
import { Product } from '../../models/product.model';
import { Category } from '../../models/category.model';
import { ProductService } from '../../services/product.service';
import { CategoryService } from '../../services/category.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  featuredProducts: Product[] = [];
  categories: Category[] = [];
  isLoading = true;
  error: string | null = null;
  
  constructor(
    private productService: ProductService,
    private categoryService: CategoryService
  ) { }

  ngOnInit(): void {
    this.loadFeaturedProducts();
    this.loadCategories();
  }
  
  loadFeaturedProducts(): void {
    this.productService.getProducts().subscribe({
      next: (products) => {
        // In a real app, you might have a featured flag or use a specific endpoint
        this.featuredProducts = products.slice(0, 8); // Just take first 8 for demo
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading featured products:', error);
        this.error = 'Failed to load featured products. Please try again later.';
        this.isLoading = false;
      }
    });
  }
  
  loadCategories(): void {
    this.categoryService.getCategories().subscribe({
      next: (categories) => {
        this.categories = categories;
      },
      error: (error) => {
        console.error('Error loading categories:', error);
      }
    });
  }
  
  onProductAddedToCart(product: Product): void {
    // You could show a notification or update UI here
    console.log(`Product added to cart: ${product.name}`);
  }
}











































































































































