import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { OrderListComponent } from './order-list/order-list.component';
import { OrderCreateComponent } from './order-create/order-create.component';
import { OrderDetailComponent } from './order-detail/order-detail.component';

const routes: Routes = [
  { path: '', component: OrderListComponent },
  { path: 'create', component: OrderCreateComponent },
  { path: 'edit/:id', component: OrderCreateComponent },
  { path: ':id', component: OrderDetailComponent }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class OrdersRoutingModule { } 