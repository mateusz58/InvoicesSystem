import {IinvoiceEntry} from "./iinvoice-entry";

export interface IInvoice {
    id?: number;
    number: string;
    seller: string;
    buyer: string;
    issuedDate: string;
    dueDate: string;
}
