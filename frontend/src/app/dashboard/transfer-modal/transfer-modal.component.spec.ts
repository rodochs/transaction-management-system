import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TransferModalComponent } from './transfer-modal.component';
import { TransferService } from '../../core/transfer.service';
import { of, throwError } from 'rxjs';

describe('TransferModalComponent', () => {
  let component: TransferModalComponent;
  let fixture: ComponentFixture<TransferModalComponent>;
  let transferService: TransferService;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TransferModalComponent],
      providers: [
        {
          provide: TransferService,
          useValue: {
            transfer: () => of(),
          } as Partial<TransferService>,
        },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(TransferModalComponent);
    component = fixture.componentInstance;
    transferService = TestBed.inject(TransferService);
    fixture.detectChanges();
  });

  it('should not submit when form is invalid', () => {
    component.form.patchValue({ fromAccountId: 0, toAccountId: 0, amount: 0 });

    // Se o método transfer for chamado, o teste falha explicitamente
    (transferService as any).transfer = () => {
      throw new Error('transfer should not be called for invalid form');
    };

    component.submit();

    // Nenhuma exceção significa que o método não foi chamado
  });

  it('should call transfer service on valid submit and emit completed', () => {
    const payload = { fromAccountId: 5, toAccountId: 6, amount: 150 };
    const result = { ...payload };
    let emitted: any = null;
    component.completed.subscribe((value) => (emitted = value));

    (transferService as any).transfer = () => of(result);

    component.form.patchValue(payload);

    component.submit();

    expect(emitted).toEqual(result);
    expect(component.success()).toBeTruthy();
  });

  it('should set error message when transfer fails', () => {
    const payload = { fromAccountId: 5, toAccountId: 6, amount: 150 };

    (transferService as any).transfer = () => throwError(() => new Error('failure'));

    component.form.patchValue(payload);

    component.submit();

    expect(component.error()).toBeTruthy();
  });
});
