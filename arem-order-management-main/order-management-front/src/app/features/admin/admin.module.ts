import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { RouterModule, Routes } from '@angular/router';
import { SharedModule } from '../../shared/shared.module';

import { AdminOrdersComponent } from './admin-orders/admin-orders.component';
import { AdminProductsComponent } from './admin-products/admin-products.component';
import { AdminSuppliersComponent } from './admin-suppliers/admin-suppliers.component';
import { AdminSupplierOrdersComponent } from './admin-supplier-orders/admin-supplier-orders.component';
import { AdminProductDialogComponent } from './admin-product-dialog/admin-product-dialog.component';
import { AdminSharedModule } from './admin-shared.module';
// Retirer ProductsModule qui cause des conflits de routing

const routes: Routes = [
  { path: 'orders', component: AdminOrdersComponent },
  { path: 'products', component: AdminProductsComponent },
  { path: 'products/stock', component: AdminProductsComponent },
  { path: 'products/prices', component: AdminProductsComponent },
  { path: 'providers', component: AdminSuppliersComponent },
  { path: 'supplier-orders', component: AdminSupplierOrdersComponent },
  { path: '', redirectTo: 'orders', pathMatch: 'full' }
];

@NgModule({
  declarations: [
    AdminOrdersComponent,
    AdminProductsComponent,
    AdminSuppliersComponent,
    AdminSupplierOrdersComponent,
    AdminProductDialogComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,
    RouterModule.forChild(routes),
    AdminSharedModule,
    SharedModule  // ← AJOUT du SharedModule
  ]
})
export class AdminModule { } 