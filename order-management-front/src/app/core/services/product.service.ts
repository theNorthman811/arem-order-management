import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Product } from '../models/product.model';
import { environment } from '../../../environments/environment';

export interface ProductFormData {
  name: string;
  reference: string;
  description?: string;
  marque?: string;
  sellPrice: number;
  buyPrice: number;
  quantity: number;
  measure: number;
  alertThreshold?: number;
}

export interface ProductContract {
  id?: number;
  name: string;
  reference: string;
  description?: string;
  marque?: string;
  quantity: number;
  measure: number;
  prices: PriceContract[];
  creationDate?: string;
  modifDate?: string;
}

export interface PriceContract {
  id?: number;
  price: number;
  amount: number;
  side: 'Buy' | 'Sell';
  measure: number;
  startDate: string;
  endDate?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProductService {
  private apiUrl = `${environment.apiUrl}/v1`;

  constructor(private http: HttpClient) {}

  getProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/products`);
  }

  getPublicProducts(): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/products/public`);
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/product/${id}`);
  }

  // Nouvelle méthode pour créer un produit avec prix Buy/Sell
  createProduct(formData: ProductFormData): Observable<ProductContract> {
    const productContract: ProductContract = {
      name: formData.name,
      reference: formData.reference,
      description: formData.description || '',
      marque: formData.marque || '',
      quantity: formData.quantity,
      measure: formData.measure,
      prices: this.createPriceContracts(formData)
    };

    return this.http.post<ProductContract>(`${this.apiUrl}/product`, productContract);
  }

  // Nouvelle méthode pour modifier un produit avec prix Buy/Sell
  updateProduct(id: number, formData: ProductFormData): Observable<ProductContract> {
    const productContract: ProductContract = {
      id: id,
      name: formData.name,
      reference: formData.reference,
      description: formData.description || '',
      marque: formData.marque || '',
      quantity: formData.quantity,
      measure: formData.measure,
      prices: this.createPriceContracts(formData)
    };

    return this.http.put<ProductContract>(`${this.apiUrl}/product/${id}`, productContract);
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/product/${id}`);
  }

  // Méthode pour créer les contrats de prix Buy/Sell
  private createPriceContracts(formData: ProductFormData): PriceContract[] {
    const now = new Date().toISOString();
    const prices: PriceContract[] = [];

    // Prix de vente (Side.Sell)
    if (formData.sellPrice > 0) {
      prices.push({
        price: formData.sellPrice,
        amount: formData.sellPrice,
        side: 'Sell',
        measure: formData.measure,
        startDate: now
      });
    }

    // Prix d'achat (Side.Buy)
    if (formData.buyPrice > 0) {
      prices.push({
        price: formData.buyPrice,
        amount: formData.buyPrice,
        side: 'Buy',
        measure: formData.measure,
        startDate: now
      });
    }

    return prices;
  }

  // Méthode utilitaire pour extraire les prix d'un ProductContract
  extractPrices(productContract: ProductContract): { sellPrice: number; buyPrice: number } {
    let sellPrice = 0;
    let buyPrice = 0;

    if (productContract.prices) {
      for (const price of productContract.prices) {
        if (price.side === 'Sell') {
          sellPrice = price.amount;
        } else if (price.side === 'Buy') {
          buyPrice = price.amount;
        }
      }
    }

    return { sellPrice, buyPrice };
  }
} 