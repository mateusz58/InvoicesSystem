import { Component, OnInit } from '@angular/core';
import {IInvoice} from "../../models/iinvoice";
import {InvoiceService} from "../../services/invoice-service.service";
import {Observable} from "rxjs";


@Component({
  selector: 'app-invoice-list',
  templateUrl: './invoice-list.component.html',
  styleUrls: ['./styles-invoice.css'],
  providers: [InvoiceService]
})
export class InvoiceListComponent implements OnInit {

  private  errorMessage = '';

  toggleEvent() {
    console.log("triggered")
  }

  _listFilter: string;

  filteredInvoices: IInvoice[] = [];

  invoices: IInvoice[] = [
    // {
    //   "number": "2020/12/10",
    //   "seller": "seller1",
    //   "buyer": "buyer1",
    //   "dueDate": new Date("2019-08-21"),
    //   "issuedDate": new Date("2019-08-21")
    // },
    // {
    //   "number": "2020/12/11",
    //   "seller": "seller2",
    //   "buyer": "buyer2",
    //   "dueDate": new Date("2019-08-21"),
    //   "issuedDate": new Date("2019-08-21"),
    // }
  ]

  constructor(private invoiceService : InvoiceService) {
    this.listFilter =  '';
  }

  set listFilter(value: string) {
    this._listFilter = value;
    this.filteredInvoices = this.listFilter ? this.performFilter(this.listFilter) : this.invoices;
  }
  get listFilter(): string {
    return this._listFilter;
  }

  performFilter (filterBy : string): IInvoice[] {
    filterBy = filterBy.toLocaleLowerCase();

    return this.invoices.filter((invoice : IInvoice) =>
    invoice.number.toLowerCase().indexOf(filterBy)!=-1);
  }

  // key and value pair here
  ngOnInit(): void {
    this.invoiceService.getInvoices().subscribe({
      next: invoices => this.invoices = invoices,
      error: err => this.errorMessage = err
    });
    this.filteredInvoices = this.invoices;
  }

//   invoices: IInvoice[] = [
//     {
//       "id": "0",
//       "buyer": {
//         "id": "0",
//         "accountNumber": "27 1030 0019 0109 8503 0014 2668",
//         "address": "ul. Bukowińska 24 d/7, 02-703 Warszawa",
//         "email": "example@post.com.pl",
//         "name": "CodersTrust",
//         "phoneNumber": "22 788-83-22",
//         "taxId": "7010416384"
//       },
//       "dueDate": new Date("2019-11-21"),
//       "entries": [
//         {
//           "id": "0",
//           "description": "Siatka ogrodzeniowa",
//           "grossValue": 615,
//           "netValue": 500,
//           "price": 123,
//           "quantity": 5,
//           "vatRate": Vat.VAT_23
//         }
//       ],
//       "issuedDate": new Date("2019-11-21"),
//       "number": "FV/1/05/2019",
//       "seller": {
//         "id": "0",
//         "accountNumber": "27 1030 0019 0109 8503 0014 2668",
//         "address": "ul. Bukowińska 24 d/7, 02-703 Warszawa",
//         "email": "example@post.com.pl",
//         "name": "CodersTrust",
//         "phoneNumber": "22 788-83-22",
//         "taxId": "7010416384"
//       }
//     }
// ]

}
