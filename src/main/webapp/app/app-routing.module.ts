import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CustomersComponent } from './components/customers/customers.component';
import { OrdersComponent } from './components/orders/orders.component';
import { CustomerOrdersComponent } from './components/customer-orders/customer-orders.component';
import { AppAuthGuard } from './app-auth.guard';
import {InvoiceListComponent} from "./components/invoice-list/invoice-list.component";

const routes: Routes = [
  { path: 'invoices', component: InvoiceListComponent, canActivate: [AppAuthGuard]  },
  { path: 'customers', component: CustomersComponent, canActivate: [AppAuthGuard], data: { roles: ['admin'] } },
  { path: 'orders', component: OrdersComponent, canActivate: [AppAuthGuard], data: { roles: ['admin'] } },
  { path: 'customerorders/:username', component: CustomerOrdersComponent, canActivate: [AppAuthGuard], data: { roles: ['admin'] } },
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: '**', redirectTo: '/' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
