
export interface InvoiceEntry {
    id: string;
    name: string;
    vatRate: Vat;
    price: number,
    quantity: number;
    invoiceId: String;
}
