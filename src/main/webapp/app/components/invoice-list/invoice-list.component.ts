import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-invoice-list',
  templateUrl: './invoice-list.component.html',
  styles: []
})
export class InvoiceListComponent implements OnInit {

  toggleEvent() {
    console.log("triggered")
  }

  invoices: any[] = [
    {
      "number": "2020/12/10",
      "seller": "seller1",
      "buyer": "buyer1",
      "due_date": "2019-08-21",
      "issued_date": "2019-08-21",
      "total": 30.00,

    },
    {
      "number": "2020/12/10",
      "seller": "seller2",
      "buyer": "buyer2",
      "due_date": "2019-08-21",
      "issued_date": "2019-08-21",
      "total": 30.00
    }
]
  listFilter: string = "filter";

  constructor() { }

  ngOnInit() {
  }

}
