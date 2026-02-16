import { CommonModule, NgIf, NgForOf } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, OnInit, Output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { TransferService } from '../../core/transfer.service';
import { TransferRequest } from '../../shared/models/transfer-request.model';
import { TransferResult } from '../../shared/models/transfer-result.model';
import { ContaBeneficioService } from '../../core/conta-beneficio.service';
import { ContaBeneficio } from '../../shared/models/conta-beneficio.model';

@Component({
  selector: 'app-transfer-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, NgIf, NgForOf],
  templateUrl: './transfer-modal.component.html',
  styleUrl: './transfer-modal.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransferModalComponent implements OnInit {
  @Output() closed = new EventEmitter<void>();
  @Output() completed = new EventEmitter<TransferResult>();

  readonly loading = signal(false);
  readonly loadingContas = signal(false);
  readonly error = signal<string | null>(null);
  readonly success = signal<string | null>(null);
  readonly contas = signal<ContaBeneficio[]>([]);

  form = new FormBuilder().nonNullable.group({
    fromAccountId: [0, [Validators.required, Validators.min(1)]],
    toAccountId: [0, [Validators.required, Validators.min(1)]],
    amount: [0, [Validators.required, Validators.min(0.01)]],
  });

  constructor(
    private readonly transferService: TransferService,
    private readonly contaBeneficioService: ContaBeneficioService
  ) {}

  ngOnInit(): void {
    this.loadContas();
  }

  private loadContas(): void {
    this.loadingContas.set(true);
    this.contaBeneficioService.getContas().subscribe({
      next: (contas) => this.contas.set(contas),
      error: () => this.error.set('Erro ao carregar contas.'),
      complete: () => this.loadingContas.set(false),
    });
  }

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
      error: (err) => {
        const message = err?.error?.message || 'Não foi possível realizar a transferência. Verifique os dados e tente novamente.';
        this.error.set(message);
        this.loading.set(false);
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
