import { Component, inject } from '@angular/core';
import { MatToolbar } from '@angular/material/toolbar';
import { MatIcon } from '@angular/material/icon';
import { MatIconButton } from '@angular/material/button';
import { MatDialog } from '@angular/material/dialog';
import { PurchaseRegistration } from '../../pages/purchase-registration/purchase-registration';

@Component({
  selector: 'app-header',
  imports: [MatToolbar, MatIcon, MatIconButton],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class Header {

  readonly dialog = inject(MatDialog);

  openDialog(): void {
    this.dialog.open(PurchaseRegistration)
  }
}
