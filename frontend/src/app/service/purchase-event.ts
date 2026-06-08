import { Injectable, signal } from '@angular/core';

export interface PurchaseCreatedEvent {
  userEmail: string;
  createdAt: number;
}

@Injectable({
  providedIn: 'root',
})
export class PurchaseEventsService {
  readonly purchaseCreated = signal<PurchaseCreatedEvent | null>(null);

  notifyPurchaseCreated(userEmail: string): void {
    this.purchaseCreated.set({
      userEmail: userEmail.trim().toLowerCase(),
      createdAt: Date.now(),
    });
  }
}
