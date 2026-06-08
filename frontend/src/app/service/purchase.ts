import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { PurchaseCreateRequest } from '../model/purchase-create-request';
import { PurchaseResponse } from '../model/purchase-response';
import { PurchaseSummaryResponse } from '../model/purchase-summary-response';

@Injectable({
  providedIn: 'root',
})
export class PurchaseService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080/api/purchases';

  createPurchase(request: PurchaseCreateRequest) {
    return this.http.post<PurchaseResponse>(this.apiUrl, request);
  }

  getPurchasesByUserEmail(userEmail: string) {
    const encodedEmail = encodeURIComponent(userEmail.trim());

    return this.http.get<PurchaseSummaryResponse>(`${this.apiUrl}/${encodedEmail}`);
  }
}
