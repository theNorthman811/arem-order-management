import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Product } from '../../../../core/models/product.model';
import { OrderItem, Measure } from '../../../../core/models/order.model';

export interface OrderItemDialogData {
  item?: OrderItem;
  products: Product[];
  mode: 'add' | 'edit';
}

@Component({
  selector: 'app-order-item-dialog',
  templateUrl: './order-item-dialog.component.html',
  styleUrls: ['./order-item-dialog.component.scss']
})
export class OrderItemDialogComponent implements OnInit {
  form: FormGroup;
  mode: 'add' | 'edit' = 'add';
  products: Product[] = [];
  selectedProduct?: Product;
  measures = Object.values(Measure);

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<OrderItemDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: OrderItemDialogData
  ) {
    this.form = this.fb.group({
      productId: ['', Validators.required],
      quantity: ['', [Validators.required, Validators.min(0.01)]]
    });
    
    this.mode = data.mode;
    this.products = data.products;

    if (data.item) {
      this.form.patchValue({
        productId: data.item.productId,
        quantity: data.item.quantity
      });
      this.selectedProduct = this.products.find(p => p.id === data.item?.productId);
    }
  }

  ngOnInit(): void {
    // Watch for product changes to update measure
    this.form.get('productId')?.valueChanges.subscribe(productId => {
      this.selectedProduct = this.products.find(p => p.id === productId);
    });
  }

  onSubmit(): void {
    if (this.form.valid && this.selectedProduct) {
      const formValue = this.form.value;
      const item: Partial<OrderItem> = {
        productId: formValue.productId,
        quantity: formValue.quantity,
        measure: this.selectedProduct.measure,
        product: this.selectedProduct,
        unitPrice: this.selectedProduct.price
      };
      this.dialogRef.close(item);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  getErrorMessage(controlName: string): string {
    const control = this.form.get(controlName);
    if (control?.hasError('required')) {
      return 'Ce champ est requis';
    }
    if (control?.hasError('min')) {
      return 'La quantité doit être supérieure à 0';
    }
    return '';
  }
} 