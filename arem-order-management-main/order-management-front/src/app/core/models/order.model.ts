export enum Measure {
  None = 'None',
  Unit = 'Unit',
  Kilogramme = 'Kilogramme',
  Liter = 'Liter',
  Gramme = 'Gramme',
  Metre = 'Metre'
}

export interface Product {
  id: number;
  name: string;
  reference?: string;
  marque?: string;
}

export interface OrderItem {
  id?: number;
  productId: number;
  product?: Product;
  quantity: number;
  unitPrice?: number;
  measure: Measure;
}

export interface Order {
  id: number;
  customerId: number;
  status: string;
  items: OrderItem[];
  orderDate: string;
  lastModifiedDate: string;
  totalAmount: number;
} 