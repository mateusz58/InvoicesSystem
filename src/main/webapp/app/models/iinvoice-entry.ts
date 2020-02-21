
export interface IinvoiceEntry {
    id: string;
    description: string;
    vatRate: Vat;
    price: number;
    netValue: number;
    grossValue: number;
    quantity: number;
    invoiceId?: string;
}
