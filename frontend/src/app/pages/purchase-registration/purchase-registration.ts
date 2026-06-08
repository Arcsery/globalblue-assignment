import { Component, computed, inject, signal } from '@angular/core';
import { MatDialogActions, MatDialogContent, MatDialogRef, MatDialogTitle } from '@angular/material/dialog';
import { MatError, MatFormField, MatHint, MatInput, MatLabel } from '@angular/material/input';
import {
  FormBuilder,
  FormsModule,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { MatButton } from '@angular/material/button';

import { MatOption, MatSelect } from '@angular/material/select';
import { PurchaseCategory } from '../../model/purchase-category';
import { VAT_RATES } from '../../model/vat-rate';
import { MatDatepicker, MatDatepickerInput, MatDatepickerToggle } from '@angular/material/datepicker';
import { MAT_DATE_LOCALE, provideNativeDateAdapter } from '@angular/material/core';
import { PurchaseService } from '../../service/purchase';
import { PurchaseEventsService } from '../../service/purchase-event';
import { PurchaseCreateRequest } from '../../model/purchase-create-request';
import { extractApiErrorMessage, formatDateToLocalIsoDate } from '../../shared/util/util';
import { toSignal } from '@angular/core/rxjs-interop';
import { startWith } from 'rxjs';

@Component({
  selector: 'app-purchase-registration',
  providers: [{ provide: MAT_DATE_LOCALE, useValue: 'en-GB' }, provideNativeDateAdapter()],
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatFormField,
    MatLabel,
    MatInput,
    FormsModule,
    MatDialogActions,
    MatButton,
    ReactiveFormsModule,
    MatError,
    MatSelect,
    MatOption,
    MatDatepicker,
    MatDatepickerToggle,
    MatDatepickerInput,
    MatHint,
  ],
  templateUrl: './purchase-registration.html',
  styleUrl: './purchase-registration.scss',
})
export class PurchaseRegistration {
  readonly dialogRef = inject(MatDialogRef<PurchaseRegistration>);
  readonly formBuilder = inject(FormBuilder);
  readonly purchaseService = inject(PurchaseService);
  purchaseEventsService = inject(PurchaseEventsService);

  readonly maxPurchaseDate = new Date();

  readonly categories = signal(Object.values(PurchaseCategory));
  readonly vatRates = signal([...VAT_RATES]);

  readonly purchaseForm = this.formBuilder.nonNullable.group({
    userEmail: ['', [Validators.required, Validators.email]],
    productName: ['', [Validators.required]],
    category: [PurchaseCategory.Electronics, [Validators.required]],
    netAmount: [[Validators.required, Validators.min(0.01)]],
    vatRate: [0.27, [Validators.required]],
    purchaseDate: [new Date(), [Validators.required]],
  });

  readonly formSubmitted = signal(false);
  readonly isSubmitting = signal(false);
  readonly errorMessage = signal('');

  readonly formStatus = toSignal(
    this.purchaseForm.statusChanges.pipe(startWith(this.purchaseForm.status)),
    { initialValue: this.purchaseForm.status }
  );

  readonly isFormInvalid = computed(() => {
    return this.formStatus() !== 'VALID' || this.isSubmitting();
  });

  onCancel(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    this.formSubmitted.set(true);

    if (this.purchaseForm.invalid) {
      this.purchaseForm.markAllAsTouched();
      return;
    }

    const formValue = this.purchaseForm.getRawValue();

    const request: PurchaseCreateRequest = {
      userEmail: formValue.userEmail,
      productName: formValue.productName,
      category: formValue.category,
      netAmount: formValue.netAmount,
      vatRate: formValue.vatRate,
      purchaseDate: formatDateToLocalIsoDate(formValue.purchaseDate),
    };

    this.isSubmitting.set(true);

    this.purchaseService.createPurchase(request).subscribe({
      next: () => {
        this.purchaseEventsService.notifyPurchaseCreated(request.userEmail);
        this.isSubmitting.set(false);
        this.dialogRef.close(true);
      },
      error: (error) => {
        this.isSubmitting.set(false);
        this.errorMessage.set(extractApiErrorMessage(error));
      },
    });
  }

  getErrorMessage(controlName: keyof typeof this.purchaseForm.controls): string {
    const control = this.purchaseForm.controls[controlName];

    if (control.hasError('required')) {
      return 'This field is required';
    }

    if (control.hasError('email')) {
      return 'Not a valid email';
    }

    if (control.hasError('min')) {
      return 'Value must be greater than 0';
    }

    if (control.hasError('matDatepickerMax')) {
      return 'Purchase date cannot be in the future';
    }

    return '';
  }

  shouldShowError(controlName: keyof typeof this.purchaseForm.controls): boolean {
    const control = this.purchaseForm.controls[controlName];

    return control.invalid && (control.touched || this.formSubmitted());
  }
}
