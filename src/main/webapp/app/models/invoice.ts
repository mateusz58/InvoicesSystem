import {InvoiceEntry} from "./invoice-entry";

export interface Invoice {
    id: string;
    number: string;
    seller: string;
    buyer: string;
    issuedDate: Date;
    dueDate: Date;
    quantity: number;
    entries?: InvoiceEntry[];
}
