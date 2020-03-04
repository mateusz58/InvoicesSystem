import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {InvoiceListComponent} from "./components/invoice-list/invoice-list.component";
import {HomeComponent} from "./components/home/home.component";

const routes: Routes = [
  { path: 'invoices', component: InvoiceListComponent },
  { path: 'home', component: HomeComponent },
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: '**', redirectTo: '/' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
