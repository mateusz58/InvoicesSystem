import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {IInvoice} from "../models/iinvoice";
import {Observable, throwError} from "rxjs";
import {catchError, map, tap} from "rxjs/operators";

const apiUrl = '/api/invoices';
const headers = new HttpHeaders().set('Content-Type', 'application/json');

@Injectable({
  providedIn: 'root'
})
export class InvoiceService {

  private invoiceUrl = './api/invoices/invoices.json';

  constructor(private http: HttpClient) {}

  getInvoices(): Observable<IInvoice[]> {
    return this .http.get<IInvoice[]>(this.invoiceUrl).pipe(
        tap(data => console.log('All:' + JSON.stringify(data))),
            catchError(this.handleError)
    );
  }


  private handleError(err: HttpErrorResponse) {
    let errorMessage = '';
    if (err.error instanceof ErrorEvent) {
      errorMessage = `An error occurred: ${err.error.message}`;
    } else {
      errorMessage = `Server returned code: ${err.status}, error message is: ${err.message}`;
    }
    console.error(errorMessage);
    return throwError(errorMessage);
  }
}
