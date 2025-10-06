
























































































































































import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Product } from '../../models/product.model';
import { Category } from '../../models/category.model';
import { ProductService } from '../../services/product.service';
import { CategoryService } from '../../services/category.service';

@Component({
  selector: 'app-product-list',
  templateUrl: './product-list.component.html',
  styleUrls: ['./product-list.component.scss']
})
export class ProductListComponent implements OnInit {
  products: Product[] = [];
  categories: Category[] = [];
  filteredProducts: Product[] = [];
  selectedCategory: string | null = null;
  searchQuery: string = '';
  sortOption: string = 'name_asc';
  isLoading = true;
  error: string | null = null;
  
  constructor(
    private productService: ProductService,
    private categoryService: CategoryService,
    private route: ActivatedRoute,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.loadCategories();
    
    // Subscribe to query params to get filters
    this.route.queryParams.subscribe(params => {
      this.selectedCategory = params['category'] || null;
      this.searchQuery = params['search'] || '';
      this.sortOption = params['sort'] || 'name_asc';
      
      this.loadProducts();
    });
  }
  
  loadProducts(): void {
    this.isLoading = true;
    
    // If category filter is applied
    if (this.selectedCategory) {
      this.productService.getProductsByCategory(this.selectedCategory).subscribe({
        next: (products) => {
          this.products = products;
          this.applyFilters();
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading products by category:', error);
          this.error = 'Failed to load products. Please try again later.';
          this.isLoading = false;
        }
      });
    } 
    // If search query is applied
    else if (this.searchQuery) {
      this.productService.searchProducts(this.searchQuery).subscribe({
        next: (products) => {
          this.products = products;
          this.applyFilters();
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error searching products:', error);
          this.error = 'Failed to search products. Please try again later.';
          this.isLoading = false;
        }
      });
    } 
    // Get all products
    else {
      this.productService.getProducts().subscribe({
        next: (products) => {
          this.products = products;
          this.applyFilters();
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading products:', error);
          this.error = 'Failed to load products. Please try again later.';
          this.isLoading = false;
        }
      });
    }
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
  
  applyFilters(): void {
    // First apply any text search filter
    let filtered = [...this.products];
    
    if (this.searchQuery) {
      const query = this.searchQuery.toLowerCase();
      filtered = filtered.filter(product => 
        product.name.toLowerCase().includes(query) || 
        product.description.toLowerCase().includes(query)
      );
    }
    
    // Then sort the results
    switch (this.sortOption) {
      case 'name_asc':
        filtered.sort((a, b) => a.name.localeCompare(b.name));
        break;
      case 'name_desc':
        filtered.sort((a, b) => b.name.localeCompare(a.name));
        break;
      case 'price_asc':
        filtered.sort((a, b) => a.price - b.price);
        break;
      case 'price_desc':
        filtered.sort((a, b) => b.price - a.price);
        break;
    }
    
    this.filteredProducts = filtered;
  }
  
  onCategoryChange(categoryId: string | null): void {
    this.updateQueryParams({ category: categoryId });
  }
  
  onSortChange(sortOption: string): void {
    this.updateQueryParams({ sort: sortOption });
  }
  
  onSearch(query: string): void {
    this.updateQueryParams({ search: query });
  }
  
  updateQueryParams(params: any): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { ...params },
      queryParamsHandling: 'merge'
    });
  }
  
  onProductAddedToCart(product: Product): void {
    // You could show a notification or update UI here
    console.log(`Product added to cart: ${product.name}`);
  }
  
  clearFilters(): void {
    this.router.navigate(['/products']);
  }
}
























































































































































