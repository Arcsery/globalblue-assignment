export interface PurchaseResponse {
  userEmail: string;
  productName: string;
  category: string;
  netAmount: number;
  vatRate: number;
  vatAmount: number;
  refund: number;
  purchaseDate: string;
}
