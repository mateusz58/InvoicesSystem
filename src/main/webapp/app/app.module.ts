import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule, APP_INITIALIZER } from '@angular/core';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { DatePipe } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { CollapseModule, TooltipModule, ModalModule, BsDropdownModule } from 'ngx-bootstrap';

import { KeycloakService, KeycloakAngularModule } from 'keycloak-angular';
import { environment } from 'src/main/webapp/environments/environment';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { TwoDigitDecimalNumberDirective } from './directives/two-digit-decimal-number.directive';
import { HttpErrorInterceptor} from './interceptor/http-error.interceptor';

import { CustomerStore } from './stores/customer.store';
import { AlertComponent } from './components/alert/alert.component';
import { InvoiceComponent } from './components/invoice/invoice.component';
import { InvoiceEntryComponent } from './components/invoice-entry/invoice-entry.component';
import { InvoiceListComponent } from './components/invoice-list/invoice-list.component';
import {ConvertToSpacesPipe} from "./shared/convert-to-spaces.pipe";
import { StarsComponent } from './components/shared/stars/stars.component';
import { InvoiceDetailsComponent } from './components/invoice-details/invoice-details.component';
import { HomeComponent } from './components/home/home.component';


export function kcInitializer(keycloak: KeycloakService): () => Promise<any> {
  return (): Promise<any> => {
    return new Promise(async (resolve, reject) => {
      try {
        await keycloak.init(environment.keycloakOptions);
        console.log('Keycloak is initialized');
        resolve();
      } catch (error) {
        console.log('Error thrown in init ' + error);
        reject(error);
      }
    });
  };
}

@NgModule({
  declarations: [
    AppComponent,
    TwoDigitDecimalNumberDirective,
    AlertComponent,
    InvoiceComponent,
    InvoiceEntryComponent,
    InvoiceListComponent,
      ConvertToSpacesPipe,
      StarsComponent,
      InvoiceDetailsComponent,
      HomeComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    FormsModule,
    ReactiveFormsModule,
    KeycloakAngularModule,
    CollapseModule.forRoot(),
    TooltipModule.forRoot(),
    ModalModule.forRoot(),
    BsDropdownModule.forRoot()
  ],
  providers: [
    DatePipe,
    CustomerStore,
    { provide: APP_INITIALIZER, useFactory: kcInitializer, multi: true, deps: [KeycloakService] },
    { provide: HTTP_INTERCEPTORS, useClass: HttpErrorInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})

export class AppModule { }
