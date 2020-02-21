import { Component, OnInit } from '@angular/core';
import {IInvoice} from "../../models/iinvoice";

@Component({
  selector: 'app-invoice-list',
  templateUrl: './invoice-list.component.html',
  styles: []
})
export class InvoiceListComponent implements OnInit {

  toggleEvent() {
    console.log("triggered")
  }

  invoices: IInvoice[] = [
    {
      "number": "2020/12/10",
      "seller":
          {
            "id":"id",
            "name": "name1"
          },

      "buyer":
          {
            "id":"id",
            "name": "name1"
          },
      "dueDate": new Date("2019-08-21"),
      "issuedDate": new Date("2019-08-21"),
      "entries": [
        {
          "id": "1",
          "description": "description1",
          "vatRate": new Vat(Vat.VAT_23),
          "price": 20.50,
          "netValue": 20.70,
          "grossValue": 30.80,
          "quantity":5
        }
          ],

      "number": "2020/12/10",
      "seller": "seller2",
      "buyer": "buyer2",
      "dueDate": new Date("2019-08-21"),
      "issuedDate": new Date("2019-08-21"),
      "total": 30.00
    }
]

  filteredInvoices: IInvoice[] = [];
  listFilter: string = "filter";

  constructor() { }

  ngOnInit() {
  }

}
