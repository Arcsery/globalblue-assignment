import { PurchaseResponse } from './purchase-response';

export interface PurchaseSummaryResponse {
  userEmail: string;
  totalNetAmount: number;
  totalVat: number;
  totalRefundableVat: number;
  currency: string;
  purchases: PurchaseResponse[];
}
