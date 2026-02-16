import { CommonModule, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TransferService } from '../../core/transfer.service';
import { TransferRequest } from '../../shared/models/transfer-request.model';
import { TransferResult } from '../../shared/models/transfer-result.model';

@Component({
  selector: 'app-transfer-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgIf],
  templateUrl: './transfer-modal.component.html',
  styleUrl: './transfer-modal.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransferModalComponent {
  @Output() closed = new EventEmitter<void>();
  @Output() completed = new EventEmitter<TransferResult>();

  readonly loading = signal(false);
  readonly error = signal<string | null>(null);
  readonly success = signal<string | null>(null);

  form = new FormBuilder().nonNullable.group({
    fromAccountId: [0, [Validators.required, Validators.min(1)]],
    toAccountId: [0, [Validators.required, Validators.min(1)]],
    amount: [0, [Validators.required, Validators.min(0.01)]],
  });

  constructor(private readonly transferService: TransferService) {}

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.error.set(null);
    this.success.set(null);

    const payload: TransferRequest = this.form.getRawValue();

    this.transferService.transfer(payload).subscribe({
      next: (result) => {
        this.success.set('Transferência realizada com sucesso.');
        this.completed.emit(result);
      },
      error: () => {
        this.error.set('Não foi possível realizar a transferência. Verifique os dados e tente novamente.');
      },
      complete: () => {
        this.loading.set(false);
      },
    });
  }

  close(): void {
    this.closed.emit();
  }
}
