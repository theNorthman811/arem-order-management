import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Routes } from '@angular/router';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ClientListComponent } from './client-list/client-list.component';
import { AccountComponent } from './account/account.component';

const routes: Routes = [
  { path: '', component: ClientListComponent },
  { path: 'account', component: AccountComponent }
];

@NgModule({
  declarations: [
    ClientListComponent,
    AccountComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule,
    RouterModule.forChild(routes)
  ],
  exports: [AccountComponent]
})
export class ClientsModule { }