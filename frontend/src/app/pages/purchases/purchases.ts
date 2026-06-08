import { AfterViewInit, Component, computed, effect, inject, signal, ViewChild } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatError, MatFormField, MatInput, MatLabel } from '@angular/material/input';
import { MatButton } from '@angular/material/button';
import { MatProgressSpinner } from '@angular/material/progress-spinner';
import { MatCard, MatCardContent, MatCardHeader, MatCardTitle } from '@angular/material/card';
import {
  MatCell,
  MatCellDef,
  MatColumnDef,
  MatHeaderCell,
  MatHeaderCellDef,
  MatHeaderRow,
  MatHeaderRowDef,
  MatRow,
  MatRowDef,
  MatTable,
  MatTableDataSource
} from '@angular/material/table';
import { MatPaginator } from '@angular/material/paginator';
import { PurchaseService } from '../../service/purchase';
import { PurchaseSummaryResponse } from '../../model/purchase-summary-response';
import { PurchaseResponse } from '../../model/purchase-response';
import { PurchaseEventsService } from '../../service/purchase-event';
import { extractApiErrorMessage } from '../../shared/util/util';

@Component({
  selector: 'app-purchases',
  imports: [
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatInput,
    MatError,
    MatButton,
    MatProgressSpinner,
    MatCard,
    MatCardHeader,
    MatCardTitle,
    MatCardContent,
    MatTable,
    MatHeaderCell,
    MatHeaderCellDef,
    MatCellDef,
    MatCell,
    MatColumnDef,
    MatHeaderRow,
    MatRow,
    MatRowDef,
    MatHeaderRowDef,
    MatPaginator,
  ],
  templateUrl: './purchases.html',
  styleUrl: './purchases.scss',
})
export class Purchases implements AfterViewInit {
  readonly formBuilder = inject(FormBuilder);
  readonly purchaseService = inject(PurchaseService);
  readonly purchaseEventsService = inject(PurchaseEventsService);

  @ViewChild(MatPaginator) paginator!: MatPaginator;

  readonly summary = signal<PurchaseSummaryResponse | null>(null);
  readonly isLoading = signal(false);
  readonly errorMessage = signal('');

  readonly tableDataSource = new MatTableDataSource<PurchaseResponse>([]);

  readonly displayedColumns: string[] = [
    'productName',
    'category',
    'netAmount',
    'vatRate',
    'vatAmount',
    'refund',
    'purchaseDate',
  ];

  readonly searchForm = this.formBuilder.nonNullable.group({
    userEmail: ['', [Validators.required, Validators.email]],
  });

  readonly hasPurchases = computed(() => {
    return (this.summary()?.purchases.length ?? 0) > 0;
  });

  constructor() {
    effect(() => {
      this.tableDataSource.data = this.summary()?.purchases ?? [];
    });

    effect(() => {
      const event = this.purchaseEventsService.purchaseCreated();

      if (!event) {
        return;
      }

      const currentEmail = this.searchForm.controls.userEmail.value.trim().toLowerCase();

      if (currentEmail && currentEmail === event.userEmail) {
        this.loadPurchases();
      }
    });
  }

  ngAfterViewInit(): void {
    this.tableDataSource.paginator = this.paginator;
  }

  loadPurchases(): void {
    this.clearMessages();

    if (this.searchForm.invalid) {
      this.searchForm.markAllAsTouched();
      return;
    }

    const userEmail = this.searchForm.controls.userEmail.value;

    this.isLoading.set(true);

    this.purchaseService.getPurchasesByUserEmail(userEmail).subscribe({
      next: (response) => {
        this.summary.set(response);
        this.isLoading.set(false);
      },
      error: (error) => {
        this.summary.set(null);
        this.tableDataSource.data = [];
        this.errorMessage.set(extractApiErrorMessage(error));
        this.isLoading.set(false);
      },
    });
  }

  shouldShowSearchEmailError(): boolean {
    const control = this.searchForm.controls.userEmail;
    return control.invalid && control.touched;
  }

  getSearchEmailErrorMessage(): string {
    const control = this.searchForm.controls.userEmail;

    if (control.hasError('required')) {
      return 'User email is required';
    }

    if (control.hasError('email')) {
      return 'Not a valid email';
    }

    return '';
  }

  private clearMessages(): void {
    this.errorMessage.set('');
  }
}
