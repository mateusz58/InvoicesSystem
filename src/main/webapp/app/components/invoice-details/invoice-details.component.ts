import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {IInvoice} from "../../models/iinvoice";
import {InvoiceService} from "../../services/invoice-service.service";

@Component({
  selector: 'app-invoice-details',
  templateUrl: './invoice-details.component.html',
  styleUrls: ['./styles-invoice-details.css'],
})
export class InvoiceDetailsComponent implements OnInit {

  pageTitle: string = 'Invoice detail';
  invoice: IInvoice | undefined;
  errorMessage = '';

  constructor(private route: ActivatedRoute,
              private router: Router,
              private invoiceService:InvoiceService) {
    console.log(this.route.snapshot.paramMap.get('id'));
  }

  ngOnInit() {
    const param = this.route.snapshot.paramMap.get('id');
    if (param) {
      const id = +param;
      this.invoiceService.getInvoice(id);
    }
  }

  getInvoice(id: number) {
    this.invoiceService.getInvoice(id).subscribe({
      next: invoice => this.invoice = invoice,
      error: err => this.errorMessage = err
    });
  }

    onBack() : void {
      this.router.navigate(['/invoices']);
    }
  }

