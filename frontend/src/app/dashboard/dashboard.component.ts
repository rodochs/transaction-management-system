import { CommonModule, CurrencyPipe, DatePipe, NgForOf, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { catchError, forkJoin, of } from 'rxjs';
import { BeneficioService } from '../core/beneficio.service';
import { ContaBeneficioService } from '../core/conta-beneficio.service';
import { TransacaoBeneficioService } from '../core/transacao-beneficio.service';
import { ClienteService } from '../core/cliente.service';
import { Beneficio } from '../shared/models/beneficio.model';
import { ContaBeneficio } from '../shared/models/conta-beneficio.model';
import { TransacaoBeneficio } from '../shared/models/transacao-beneficio.model';
import { Cliente } from '../shared/models/cliente.model';
import { TransferModalComponent } from './transfer-modal/transfer-modal.component';
import { BeneficioModalComponent } from './beneficio-modal/beneficio-modal.component';
import { ClienteModalComponent } from './cliente-modal/cliente-modal.component';
import { ContaModalComponent } from './conta-modal/conta-modal.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule, NgIf, NgForOf, CurrencyPipe, DatePipe,
    TransferModalComponent, BeneficioModalComponent, ClienteModalComponent, ContaModalComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardComponent implements OnInit {
  beneficios = signal<Beneficio[] | null>(null);
  contas = signal<ContaBeneficio[] | null>(null);
  transacoes = signal<TransacaoBeneficio[] | null>(null);
  clientes = signal<Cliente[] | null>(null);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);
  
  showTransferModal = signal<boolean>(false);
  showBeneficioModal = signal<boolean>(false);
  showClienteModal = signal<boolean>(false);
  showContaModal = signal<boolean>(false);
  
  editingBeneficio = signal<Beneficio | null>(null);
  editingCliente = signal<Cliente | null>(null);

  constructor(
    private readonly beneficioService: BeneficioService,
    private readonly contaBeneficioService: ContaBeneficioService,
    private readonly transacaoBeneficioService: TransacaoBeneficioService,
    private readonly clienteService: ClienteService
  ) {}

  ngOnInit(): void {
    this.loadAllData();
  }

  loadAllData(): void {
    this.loading.set(true);
    this.error.set(null);

    forkJoin({
      beneficios: this.beneficioService.getBeneficios().pipe(catchError(() => of([] as Beneficio[]))),
      contas: this.contaBeneficioService.getContas().pipe(catchError(() => of([] as ContaBeneficio[]))),
      transacoes: this.transacaoBeneficioService.listarTransacoes().pipe(catchError(() => of([] as TransacaoBeneficio[]))),
      clientes: this.clienteService.getClientes().pipe(catchError(() => of([] as Cliente[])))
    }).subscribe({
      next: (data) => {
        this.beneficios.set(data.beneficios);
        this.contas.set(data.contas);
        this.transacoes.set(data.transacoes);
        this.clientes.set(data.clientes);
      },
      error: () => {
        this.error.set('Não foi possível carregar os dados. Tente novamente.');
      },
      complete: () => {
        this.loading.set(false);
      }
    });
  }

  openTransferModal(): void {
    this.showTransferModal.set(true);
  }

  openBeneficioModal(beneficio: Beneficio | null = null): void {
    this.editingBeneficio.set(beneficio);
    this.showBeneficioModal.set(true);
  }

  openClienteModal(cliente: Cliente | null = null): void {
    this.editingCliente.set(cliente);
    this.showClienteModal.set(true);
  }

  openContaModal(): void {
    this.showContaModal.set(true);
  }

  handleTransferCompleted(): void {
    this.showTransferModal.set(false);
    this.loadAllData();
  }

  handleBeneficioSaved(): void {
    this.showBeneficioModal.set(false);
    this.editingBeneficio.set(null);
    this.loadAllData();
  }

  handleClienteSaved(): void {
    this.showClienteModal.set(false);
    this.editingCliente.set(null);
    this.loadAllData();
  }

  handleContaSaved(): void {
    this.showContaModal.set(false);
    this.loadAllData();
  }

  deleteBeneficio(beneficio: Beneficio): void {
    if (confirm(`Deseja excluir o benefício "${beneficio.nome}"?`)) {
      this.beneficioService.deleteBeneficio(beneficio.id).subscribe({
        next: () => this.loadAllData(),
        error: () => alert('Erro ao excluir benefício.')
      });
    }
  }

  deleteCliente(cliente: Cliente): void {
    if (confirm(`Deseja excluir o colaborador "${cliente.nome}"?`)) {
      this.clienteService.deleteCliente(cliente.id).subscribe({
        next: () => this.loadAllData(),
        error: () => alert('Erro ao excluir colaborador.')
      });
    }
  }

  getTotalSaldo(): number {
    const contas = this.contas();
    if (!contas) return 0;
    return contas.reduce((sum, c) => sum + c.saldo, 0);
  }

  trackById(index: number, item: Beneficio): number {
    return item.id;
  }
}
