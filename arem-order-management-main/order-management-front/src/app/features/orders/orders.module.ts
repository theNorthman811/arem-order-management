import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../../shared/shared.module';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { OrdersRoutingModule } from './orders-routing.module';
import { OrderListComponent } from './order-list/order-list.component';
import { OrderDetailComponent } from './order-detail/order-detail.component';
import { OrderCreateComponent } from './order-create/order-create.component';
import { OrderItemDialogComponent } from './components/order-item-dialog/order-item-dialog.component';

@NgModule({
  declarations: [
    OrderListComponent,
    OrderDetailComponent,
    OrderCreateComponent,
    OrderItemDialogComponent
  ],
  imports: [
    CommonModule,
    SharedModule,
    OrdersRoutingModule,
    ReactiveFormsModule,
    FormsModule
  ]
})
export class OrdersModule { } 