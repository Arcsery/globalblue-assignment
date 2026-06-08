export interface PurchaseCreateRequest {
  userEmail: string;
  productName: string;
  category: string;
  netAmount: number;
  vatRate: number;
  purchaseDate: string;
}
