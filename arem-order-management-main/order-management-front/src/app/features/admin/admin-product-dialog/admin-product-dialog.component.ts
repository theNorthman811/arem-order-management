import { Component, OnInit, Input, Output, EventEmitter } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Product } from '../../../core/models/product.model';
import { Measure } from '../../../core/models/order.model';

export interface ProductDialogData {
  product?: Product;
  isEdit: boolean;
}

@Component({
  selector: 'app-admin-product-dialog',
  template: `
    <div class="product-dialog-overlay" (click)="onCancel()">
      <div class="product-dialog" (click)="$event.stopPropagation()">
        <!-- Header -->
        <div class="dialog-header">
          <h2>
            <i class="fas fa-cube"></i>
            {{ isEdit ? 'Modifier le produit' : 'Nouveau produit' }}
          </h2>
          <button class="close-btn" (click)="onCancel()">
            <i class="fas fa-times"></i>
          </button>
        </div>

        <!-- Form -->
        <form [formGroup]="productForm" (ngSubmit)="onSubmit()" class="dialog-form">
          <!-- Informations générales -->
          <div class="form-section">
            <h3>
              <i class="fas fa-info-circle"></i>
              Informations générales
            </h3>
            
            <div class="form-row">
              <div class="form-group">
                <label>Nom du produit *</label>
                <input type="text" 
                       formControlName="name" 
                       placeholder="Ex: Laptop HP Pavilion"
                       [class.error]="productForm.get('name')?.invalid && productForm.get('name')?.touched">
                <div class="error-message" *ngIf="productForm.get('name')?.invalid && productForm.get('name')?.touched">
                  {{ getErrorMessage('name') }}
                </div>
              </div>
              
              <div class="form-group">
                <label>Référence *</label>
                <input type="text" 
                       formControlName="reference" 
                       placeholder="Ex: PROD001"
                       [class.error]="productForm.get('reference')?.invalid && productForm.get('reference')?.touched">
                <div class="error-message" *ngIf="productForm.get('reference')?.invalid && productForm.get('reference')?.touched">
                  {{ getErrorMessage('reference') }}
                </div>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>Marque</label>
                <input type="text" 
                       formControlName="marque" 
                       placeholder="Ex: HP, Samsung...">
              </div>
              
              <div class="form-group">
                <label>Unité de mesure *</label>
                <select formControlName="measure" 
                        [class.error]="productForm.get('measure')?.invalid && productForm.get('measure')?.touched">
                  <option [value]="Measure.Unit">Unités</option>
                  <option [value]="Measure.Kilogramme">Kilogrammes</option>
                  <option [value]="Measure.Liter">Litres</option>
                </select>
              </div>
            </div>

            <div class="form-group full-width">
              <label>Description</label>
              <textarea formControlName="description" 
                        placeholder="Description détaillée du produit..."
                        rows="3"></textarea>
            </div>
          </div>

          <!-- Prix et stock -->
          <div class="form-section">
            <h3>
              <i class="fas fa-euro-sign"></i>
              Prix et stock
            </h3>
            
            <div class="form-row">
              <div class="form-group">
                <label>Prix de vente * (€)</label>
                <div class="input-with-icon">
                  <i class="fas fa-euro-sign"></i>
                  <input type="number" 
                         formControlName="sellPrice" 
                         step="0.01"
                         min="0"
                         placeholder="0.00"
                         [class.error]="productForm.get('sellPrice')?.invalid && productForm.get('sellPrice')?.touched">
                </div>
                <div class="error-message" *ngIf="productForm.get('sellPrice')?.invalid && productForm.get('sellPrice')?.touched">
                  {{ getErrorMessage('sellPrice') }}
                </div>
              </div>
              
              <div class="form-group">
                <label>Prix d'achat (€)</label>
                <div class="input-with-icon">
                  <i class="fas fa-shopping-cart"></i>
                  <input type="number" 
                         formControlName="buyPrice" 
                         step="0.01"
                         min="0"
                         placeholder="0.00">
                </div>
                <small class="form-hint">Prix d'achat auprès du fournisseur</small>
              </div>
            </div>

            <div class="form-row">
              <div class="form-group">
                <label>Stock actuel *</label>
                <div class="input-with-icon">
                  <i class="fas fa-boxes"></i>
                  <input type="number" 
                         formControlName="quantity" 
                         min="0"
                         placeholder="0"
                         [class.error]="productForm.get('quantity')?.invalid && productForm.get('quantity')?.touched">
                </div>
                <div class="error-message" *ngIf="productForm.get('quantity')?.invalid && productForm.get('quantity')?.touched">
                  {{ getErrorMessage('quantity') }}
                </div>
              </div>
              
              <div class="form-group">
                <label>Seuil d'alerte</label>
                <div class="input-with-icon">
                  <i class="fas fa-exclamation-triangle"></i>
                  <input type="number" 
                         formControlName="alertThreshold" 
                         min="0"
                         placeholder="10">
                </div>
                <small class="form-hint">Alerte quand le stock descend sous ce seuil</small>
              </div>
            </div>

            <!-- Indicateur de marge -->
            <div class="margin-indicator" *ngIf="productForm.get('sellPrice')?.value && productForm.get('buyPrice')?.value">
              <div class="margin-info">
                <span class="margin-label">Marge bénéficiaire:</span>
                <span class="margin-value" [ngClass]="getMarginClass()">
                  {{ getMarginText() }}
                </span>
              </div>
              <div class="margin-bar">
                <div class="margin-fill" [style.width.%]="getMarginPercentage()"></div>
              </div>
            </div>
          </div>

          <!-- Actions -->
          <div class="dialog-actions">
            <button type="button" class="btn btn-cancel" (click)="onCancel()">
              <i class="fas fa-times"></i>
              Annuler
            </button>
            <button type="submit" 
                    class="btn btn-save" 
                    [disabled]="productForm.invalid || loading"
                    [class.loading]="loading">
              <i class="fas fa-spinner fa-spin" *ngIf="loading"></i>
              <i class="fas fa-save" *ngIf="!loading"></i>
              {{ loading ? 'Enregistrement...' : (isEdit ? 'Modifier' : 'Créer') }}
            </button>
          </div>
        </form>
      </div>
    </div>
  `,
  styles: [`
    .product-dialog-overlay {
      position: fixed;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: rgba(0, 0, 0, 0.6);
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 1000;
      backdrop-filter: blur(4px);
    }

    .product-dialog {
      background: white;
      border-radius: 20px;
      width: 90%;
      max-width: 700px;
      max-height: 90vh;
      overflow-y: auto;
      box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
      animation: dialogAppear 0.3s ease;
    }

    @keyframes dialogAppear {
      from {
        opacity: 0;
        transform: scale(0.9) translateY(-20px);
      }
      to {
        opacity: 1;
        transform: scale(1) translateY(0);
      }
    }

    .dialog-header {
      background: linear-gradient(135deg, #667eea, #764ba2);
      color: white;
      padding: 24px 32px;
      border-radius: 20px 20px 0 0;
      display: flex;
      justify-content: space-between;
      align-items: center;

      h2 {
        margin: 0;
        font-size: 1.5rem;
        font-weight: 600;
        display: flex;
        align-items: center;
        gap: 12px;
      }

      .close-btn {
        background: rgba(255, 255, 255, 0.2);
        border: none;
        width: 40px;
        height: 40px;
        border-radius: 50%;
        color: white;
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          background: rgba(255, 255, 255, 0.3);
          transform: scale(1.1);
        }
      }
    }

    .dialog-form {
      padding: 32px;
    }

    .form-section {
      margin-bottom: 32px;

      h3 {
        color: #1e293b;
        font-size: 1.2rem;
        font-weight: 600;
        margin-bottom: 20px;
        display: flex;
        align-items: center;
        gap: 10px;
        padding-bottom: 10px;
        border-bottom: 2px solid #e2e8f0;

        i {
          color: #667eea;
        }
      }
    }

    .form-row {
      display: grid;
      grid-template-columns: 1fr 1fr;
      gap: 20px;
      margin-bottom: 20px;

      @media (max-width: 600px) {
        grid-template-columns: 1fr;
      }
    }

    .form-group {
      &.full-width {
        grid-column: 1 / -1;
      }

      label {
        display: block;
        font-weight: 600;
        color: #374151;
        margin-bottom: 8px;
        font-size: 0.9rem;
      }

      input, select, textarea {
        width: 100%;
        padding: 12px 16px;
        border: 2px solid #e2e8f0;
        border-radius: 12px;
        font-size: 16px;
        transition: all 0.3s ease;
        background: white;

        &:focus {
          outline: none;
          border-color: #667eea;
          box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
        }

        &.error {
          border-color: #ef4444;
          box-shadow: 0 0 0 3px rgba(239, 68, 68, 0.1);
        }

        &::placeholder {
          color: #94a3b8;
        }
      }

      textarea {
        resize: vertical;
        min-height: 80px;
      }

      .input-with-icon {
        position: relative;

        i {
          position: absolute;
          left: 16px;
          top: 50%;
          transform: translateY(-50%);
          color: #64748b;
          font-size: 16px;
        }

        input {
          padding-left: 48px;
        }
      }

      .form-hint {
        display: block;
        color: #64748b;
        font-size: 0.8rem;
        margin-top: 4px;
      }

      .error-message {
        color: #ef4444;
        font-size: 0.8rem;
        margin-top: 4px;
        font-weight: 500;
      }
    }

    .margin-indicator {
      background: #f8fafc;
      border-radius: 12px;
      padding: 16px;
      margin-top: 16px;

      .margin-info {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 8px;

        .margin-label {
          font-weight: 600;
          color: #374151;
        }

        .margin-value {
          font-weight: 700;
          font-size: 1.1rem;

          &.positive {
            color: #059669;
          }

          &.negative {
            color: #dc2626;
          }

          &.neutral {
            color: #64748b;
          }
        }
      }

      .margin-bar {
        height: 6px;
        background: #e2e8f0;
        border-radius: 3px;
        overflow: hidden;

        .margin-fill {
          height: 100%;
          background: linear-gradient(90deg, #10b981, #059669);
          transition: width 0.3s ease;
        }
      }
    }

    .dialog-actions {
      display: flex;
      gap: 16px;
      justify-content: flex-end;
      padding-top: 24px;
      border-top: 2px solid #e2e8f0;

      .btn {
        padding: 12px 24px;
        border: none;
        border-radius: 12px;
        font-weight: 600;
        cursor: pointer;
        transition: all 0.3s ease;
        display: flex;
        align-items: center;
        gap: 8px;
        min-width: 120px;
        justify-content: center;

        &.btn-cancel {
          background: #f1f5f9;
          color: #64748b;

          &:hover {
            background: #e2e8f0;
            color: #475569;
          }
        }

        &.btn-save {
          background: linear-gradient(135deg, #4ade80, #22c55e);
          color: white;
          box-shadow: 0 4px 15px rgba(34, 197, 94, 0.3);

          &:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 8px 25px rgba(34, 197, 94, 0.4);
          }

          &:disabled {
            opacity: 0.6;
            cursor: not-allowed;
          }

          &.loading {
            pointer-events: none;
          }
        }
      }
    }
  `]
})
export class AdminProductDialogComponent implements OnInit {
  @Input() isEdit = false;
  @Input() product?: Product;
  @Output() close = new EventEmitter<void>();
  @Output() save = new EventEmitter<any>();

  productForm: FormGroup;
  loading = false;
  
  // Exposer Measure pour le template
  Measure = Measure;

  constructor(private fb: FormBuilder) {
    this.productForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(100)]],
      reference: ['', [Validators.required, Validators.maxLength(50)]],
      description: [''],
      marque: [''],
      sellPrice: [0, [Validators.required, Validators.min(0.01)]],
      buyPrice: [0, [Validators.min(0)]],
      quantity: [0, [Validators.required, Validators.min(0)]],
      measure: [Measure.Unit, [Validators.required]],
      alertThreshold: [10, [Validators.min(0)]]
    });
  }

  ngOnInit(): void {
    if (this.isEdit && this.product) {
      this.productForm.patchValue({
        name: this.product.name,
        reference: this.product.reference,
        description: this.product.description || '',
        marque: this.product.marque || '',
        sellPrice: this.product.price,
        buyPrice: (this.product as any).buyPrice || 0, // ← CORRECTION: récupérer le prix d'achat
        quantity: this.product.quantity,
        measure: this.product.measure,
        alertThreshold: 10
      });
    }
  }

  onSubmit(): void {
    if (this.productForm.valid) {
      this.loading = true;
      
      // Simuler l'enregistrement
      setTimeout(() => {
        this.save.emit(this.productForm.value);
        this.loading = false;
      }, 1000);
    }
  }

  onCancel(): void {
    this.close.emit();
  }

  getErrorMessage(fieldName: string): string {
    const field = this.productForm.get(fieldName);
    if (field?.hasError('required')) {
      return 'Ce champ est obligatoire';
    }
    if (field?.hasError('min')) {
      return 'La valeur doit être positive';
    }
    if (field?.hasError('maxlength')) {
      return `Maximum ${field.errors?.['maxlength'].requiredLength} caractères`;
    }
    return '';
  }

  getMarginText(): string {
    const sellPrice = this.productForm.get('sellPrice')?.value || 0;
    const buyPrice = this.productForm.get('buyPrice')?.value || 0;
    
    if (sellPrice <= 0 || buyPrice <= 0) return '---';
    
    const margin = sellPrice - buyPrice;
    const marginPercent = ((margin / sellPrice) * 100).toFixed(1);
    
    return `${margin.toFixed(2)}€ (${marginPercent}%)`;
  }

  getMarginClass(): string {
    const sellPrice = this.productForm.get('sellPrice')?.value || 0;
    const buyPrice = this.productForm.get('buyPrice')?.value || 0;
    
    if (sellPrice <= 0 || buyPrice <= 0) return 'neutral';
    
    const margin = sellPrice - buyPrice;
    
    if (margin > 0) return 'positive';
    if (margin < 0) return 'negative';
    return 'neutral';
  }

  getMarginPercentage(): number {
    const sellPrice = this.productForm.get('sellPrice')?.value || 0;
    const buyPrice = this.productForm.get('buyPrice')?.value || 0;
    
    if (sellPrice <= 0 || buyPrice <= 0) return 0;
    
    const marginPercent = ((sellPrice - buyPrice) / sellPrice) * 100;
    return Math.max(0, Math.min(100, marginPercent));
  }
} 