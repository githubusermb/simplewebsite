























export interface Cart {
  cartId: string;
  customerId: string;
  status: CartStatus;
  totalItems: number;
  totalPrice: number;
  items: CartItem[];
  createdAt: string;
  updatedAt: string;
}

export interface CartItem {
  productId: string;
  name: string;
  price: number;
  quantity: number;
  imageUrl: string;
}

export enum CartStatus {
  ACTIVE = 'active',
  CHECKOUT = 'checkout',
  ABANDONED = 'abandoned',
  COMPLETED = 'completed'
}























