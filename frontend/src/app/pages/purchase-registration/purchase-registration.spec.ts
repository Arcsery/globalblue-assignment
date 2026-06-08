import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PurchaseRegistration } from './purchase-registration';

describe('PurchaseRegistration', () => {
  let component: PurchaseRegistration;
  let fixture: ComponentFixture<PurchaseRegistration>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PurchaseRegistration],
    }).compileComponents();

    fixture = TestBed.createComponent(PurchaseRegistration);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
