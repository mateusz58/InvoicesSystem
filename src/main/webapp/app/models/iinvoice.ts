import {IinvoiceEntry} from "./iinvoice-entry";

export interface IInvoice {
    id?: string;
    number: string;
    seller: string;
    buyer: string;
    issuedDate: Date;
    dueDate: Date;
    // entries?: IinvoiceEntry[];
}
