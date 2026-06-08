export interface PurchaseResponse {
  id: number;
  userEmail: string;
  productName: string;
  category: string;
  netAmount: number;
  vatRate: number;
  vatAmount: number;
  refund: number;
  purchaseDate: string;
}
