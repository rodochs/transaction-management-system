import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { BeneficioService, BeneficioRequest } from '../../core/beneficio.service';
import { Beneficio } from '../../shared/models/beneficio.model';

@Component({
  selector: 'app-beneficio-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './beneficio-modal.component.html',
  styleUrl: './beneficio-modal.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BeneficioModalComponent implements OnInit {
  @Input() beneficio: Beneficio | null = null;
  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<Beneficio>();

  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  form = new FormBuilder().nonNullable.group({
    nome: ['', [Validators.required, Validators.minLength(3)]],
    descricao: [''],
    valor: [0, [Validators.required, Validators.min(0)]],
    ativo: [true],
  });

  constructor(private readonly beneficioService: BeneficioService) {}

  ngOnInit(): void {
    if (this.beneficio) {
      this.form.patchValue({
        nome: this.beneficio.nome,
        descricao: this.beneficio.descricao || '',
        valor: this.beneficio.valor,
        ativo: this.beneficio.ativo,
      });
    }
  }

  get isEditing(): boolean {
    return this.beneficio !== null;
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    const request: BeneficioRequest = this.form.getRawValue();

    const operation = this.isEditing
      ? this.beneficioService.updateBeneficio(this.beneficio!.id, request)
      : this.beneficioService.createBeneficio(request);

    operation.subscribe({
      next: (result) => {
        this.saved.emit(result);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Erro ao salvar benefício.');
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
