import {IinvoiceEntry} from "./iinvoice-entry";

export interface IInvoice {
    id: string;
    number: string;
    seller: ICompany;
    buyer: ICompany;
    issuedDate: Date;
    dueDate: Date;
    entries: IinvoiceEntry[];
}
