import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Product } from '../../../core/models/product.model';

export interface ProductDialogData {
  product?: Product;
  isEdit: boolean;
}

@Component({
  selector: 'app-product-dialog',
  templateUrl: './product-dialog.component.html',
  styleUrls: ['./product-dialog.component.scss']
})
export class ProductDialogComponent implements OnInit {
  productForm: FormGroup;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<ProductDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ProductDialogData
  ) {
    this.productForm = this.fb.group({
      name: ['', [Validators.required, Validators.maxLength(100)]],
      reference: ['', [Validators.required, Validators.maxLength(50)]],
      description: ['', [Validators.maxLength(500)]],
      marque: ['', [Validators.maxLength(50)]],
      comment: ['', [Validators.maxLength(200)]],
      price: [0, [Validators.required, Validators.min(0)]],
      quantity: [0, [Validators.required, Validators.min(0)]],
      measure: [1, [Validators.required]]
    });
  }

  ngOnInit(): void {
    if (this.data.isEdit && this.data.product) {
      this.productForm.patchValue({
        name: this.data.product.name,
        reference: this.data.product.reference,
        description: this.data.product.description || '',
        marque: this.data.product.marque || '',
        comment: this.data.product.comment || '',
        price: this.data.product.price,
        quantity: this.data.product.quantity,
        measure: this.data.product.measure
      });
    }
  }

  onSubmit(): void {
    if (this.productForm.valid) {
      this.loading = true;
      const productData = this.productForm.value;
      
      if (this.data.isEdit && this.data.product) {
        productData.id = this.data.product.id;
      }

      this.dialogRef.close(productData);
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  getErrorMessage(fieldName: string): string {
    const field = this.productForm.get(fieldName);
    if (field?.hasError('required')) {
      return 'Ce champ est obligatoire';
    }
    if (field?.hasError('maxlength')) {
      return `Maximum ${field.errors?.['maxlength'].requiredLength} caractères`;
    }
    if (field?.hasError('min')) {
      return 'La valeur doit être positive';
    }
    return '';
  }
} 