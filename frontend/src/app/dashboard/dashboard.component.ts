import { CommonModule, CurrencyPipe, DatePipe, NgForOf, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { catchError, forkJoin, of } from 'rxjs';
import { BeneficioService } from '../core/beneficio.service';
import { ContaBeneficioService } from '../core/conta-beneficio.service';
import { TransacaoBeneficioService } from '../core/transacao-beneficio.service';
import { Beneficio } from '../shared/models/beneficio.model';
import { ContaBeneficio } from '../shared/models/conta-beneficio.model';
import { TransacaoBeneficio } from '../shared/models/transacao-beneficio.model';
import { TransferModalComponent } from './transfer-modal/transfer-modal.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, NgIf, NgForOf, CurrencyPipe, DatePipe, TransferModalComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardComponent implements OnInit {
  beneficios = signal<Beneficio[] | null>(null);
  contas = signal<ContaBeneficio[] | null>(null);
  transacoes = signal<TransacaoBeneficio[] | null>(null);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);
  showTransferModal = signal<boolean>(false);

  constructor(
    private readonly beneficioService: BeneficioService,
    private readonly contaBeneficioService: ContaBeneficioService,
    private readonly transacaoBeneficioService: TransacaoBeneficioService
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
      transacoes: this.transacaoBeneficioService.listarTransacoes().pipe(catchError(() => of([] as TransacaoBeneficio[])))
    }).subscribe({
      next: (data) => {
        this.beneficios.set(data.beneficios);
        this.contas.set(data.contas);
        this.transacoes.set(data.transacoes);
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

  handleTransferCompleted(): void {
    this.showTransferModal.set(false);
    this.loadAllData();
  }

  getTotalSaldo(): number {
    const contas = this.contas();
    if (!contas) return 0;
    return contas.reduce((sum, c) => sum + c.saldo, 0);
  }

  getBeneficioNome(beneficioId: number): string {
    const beneficios = this.beneficios();
    if (!beneficios) return '';
    const b = beneficios.find(x => x.id === beneficioId);
    return b ? b.nome : '';
  }

  trackById(index: number, item: Beneficio): number {
    return item.id;
  }
}
