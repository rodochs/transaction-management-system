import { CommonModule } from '@angular/common';
import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ClienteService, ClienteRequest } from '../../core/cliente.service';
import { Cliente } from '../../shared/models/cliente.model';

@Component({
  selector: 'app-cliente-modal',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cliente-modal.component.html',
  styleUrl: './cliente-modal.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ClienteModalComponent implements OnInit {
  @Input() cliente: Cliente | null = null;
  @Output() closed = new EventEmitter<void>();
  @Output() saved = new EventEmitter<Cliente>();

  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  form = new FormBuilder().nonNullable.group({
    nome: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
  });

  constructor(private readonly clienteService: ClienteService) {}

  ngOnInit(): void {
    if (this.cliente) {
      this.form.patchValue({
        nome: this.cliente.nome,
        email: this.cliente.email,
      });
    }
  }

  get isEditing(): boolean {
    return this.cliente !== null;
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.error.set(null);

    const request: ClienteRequest = this.form.getRawValue();

    const operation = this.isEditing
      ? this.clienteService.updateCliente(this.cliente!.id, request)
      : this.clienteService.createCliente(request);

    operation.subscribe({
      next: (result) => {
        this.saved.emit(result);
      },
      error: (err) => {
        this.error.set(err?.error?.message || 'Erro ao salvar cliente.');
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
