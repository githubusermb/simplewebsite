












export interface Customer {
  customerId: string;
  email: string;
  firstName: string;
  lastName: string;
  address?: Address;
  createdAt: string;
  updatedAt: string;
}

export interface Address {
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state: string;
  postalCode: string;
  country: string;
}












