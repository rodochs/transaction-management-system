import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, OnInit, Output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ContaBeneficioService, ContaBeneficioRequest } from '../../core/conta-beneficio.service';
import { ClienteService } from '../../core/cliente.service';
import { BeneficioService } from '../../core/beneficio.service';
import { Cliente } from '../../shared/models/cliente.model';
import { Beneficio } from '../../shared/models/beneficio.model';
import { ContaBeneficio } from '../../shared/models/conta-beneficio.model';

@Component({
  selector: 'app-conta-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './conta-modal.component.html',
  styleUrl: './conta-modal.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ContaModalComponent implements OnInit {
  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<ContaBeneficio>();

  readonly loading = signal(false);
  readonly loadingData = signal(false);
  readonly error = signal<string | null>(null);
  readonly clientes = signal<Cliente[]>([]);
  readonly beneficios = signal<Beneficio[]>([]);

  form = new FormBuilder().nonNullable.group({
    clienteId: [0, [Validators.required, Validators.min(1)]],
    beneficioId: [0, [Validators.required, Validators.min(1)]],
    saldoInicial: [0, [Validators.required, Validators.min(0)]],
  });

  constructor(
    private readonly contaService: ContaBeneficioService,
    private readonly clienteService: ClienteService,
    private readonly beneficioService: BeneficioService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  private loadData(): void {
    this.loadingData.set(true);
    
    this.clienteService.getClientes().subscribe({
      next: (data) => this.clientes.set(data),
      error: () => this.error.set('Erro ao carregar clientes.'),
    });

    this.beneficioService.getBeneficios().subscribe({
      next: (data) => this.beneficios.set(data),
      error: () => this.error.set('Erro ao carregar benefícios.'),
      complete: () => this.loadingData.set(false),
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    const request: ContaBeneficioRequest = this.form.getRawValue();

    this.contaService.createConta(request).subscribe({
      next: (result) => {
        this.saved.emit(result);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Erro ao criar conta de benefício.');
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
