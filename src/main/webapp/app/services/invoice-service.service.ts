import { Injectable } from '@angular/core';
import {IInvoice} from "../models/iinvoice";

@Injectable({
  providedIn: 'root'
})
export class InvoiceService {

  getInvoices(): IInvoice[] {
  return [
    {
      "number": "2020/12/10",
      "seller": "seller1",
      "buyer": "buyer1",
      "dueDate": new Date("2019-08-21"),
      "issuedDate": new Date("2019-08-21")
    },
    {
      "number": "2020/12/11",
      "seller": "seller2",
      "buyer": "buyer2",
      "dueDate": new Date("2019-08-21"),
      "issuedDate": new Date("2019-08-21"),
    }
  ]}
}
