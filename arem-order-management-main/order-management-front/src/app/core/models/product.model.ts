import { Measure } from './order.model';

export interface Product {
  id: number;
  reference: string;
  name: string;
  marque?: string;
  description?: string;
  comment?: string;
  quantity: number;
  price: number;
  measure: Measure;
  modifDate?: string;
  creationDate?: string;
} 