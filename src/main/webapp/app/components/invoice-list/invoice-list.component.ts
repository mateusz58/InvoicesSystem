import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-invoice-list',
  templateUrl: './invoice-list.component.html',
  styles: []
})
export class InvoiceListComponent implements OnInit {

  invoices: any[] = [
    {
      "seller": "seller1",
      "buyer": "buyer1"
    }
]

  constructor() { }

  ngOnInit() {
  }

}
