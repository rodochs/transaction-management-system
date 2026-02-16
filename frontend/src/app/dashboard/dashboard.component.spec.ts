import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of, throwError } from 'rxjs';
import { DashboardComponent } from './dashboard.component';
import { BeneficioService } from '../core/beneficio.service';
import { ContaBeneficioService } from '../core/conta-beneficio.service';
import { TransacaoBeneficioService } from '../core/transacao-beneficio.service';
import { Beneficio } from '../shared/models/beneficio.model';
import { ContaBeneficio } from '../shared/models/conta-beneficio.model';
import { TransacaoBeneficio } from '../shared/models/transacao-beneficio.model';
import { By } from '@angular/platform-browser';

class BeneficioServiceMock {
  getBeneficios: () => Observable<Beneficio[]> = () => of([]);
}

class ContaBeneficioServiceMock {
  getContas: () => Observable<ContaBeneficio[]> = () => of([]);
}

class TransacaoBeneficioServiceMock {
  listarTransacoes: () => Observable<TransacaoBeneficio[]> = () => of([]);
}

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let beneficioService: BeneficioServiceMock;
  let contaBeneficioService: ContaBeneficioServiceMock;
  let transacaoBeneficioService: TransacaoBeneficioServiceMock;

  beforeEach(async () => {
    beneficioService = new BeneficioServiceMock();
    contaBeneficioService = new ContaBeneficioServiceMock();
    transacaoBeneficioService = new TransacaoBeneficioServiceMock();

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [
        { provide: BeneficioService, useValue: beneficioService },
        { provide: ContaBeneficioService, useValue: contaBeneficioService },
        { provide: TransacaoBeneficioService, useValue: transacaoBeneficioService },
      ],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
  }

  it('should show loading state and then display accounts', () => {
    const beneficiosMock: Beneficio[] = [
      { id: 1, nome: 'Vale Alimentação', descricao: 'Cartão alimentação', valor: 500, ativo: true },
    ];
    const contasMock: ContaBeneficio[] = [
      { id: 1, clienteId: 1, clienteNome: 'Ana Silva', beneficioId: 1, saldo: 1000 },
      { id: 2, clienteId: 2, clienteNome: 'Bruno Santos', beneficioId: 1, saldo: 500 },
    ];

    beneficioService.getBeneficios = () => of(beneficiosMock);
    contaBeneficioService.getContas = () => of(contasMock);
    transacaoBeneficioService.listarTransacoes = () => of([]);

    createComponent();
    fixture.detectChanges();

    expect(component.loading()).toBe(false);
    expect(component.contas()?.length).toBe(2);
    expect(component.getTotalSaldo()).toBe(1500);
  });

  it('should show empty state when there are no accounts', () => {
    beneficioService.getBeneficios = () => of([]);
    contaBeneficioService.getContas = () => of([]);
    transacaoBeneficioService.listarTransacoes = () => of([]);

    createComponent();
    fixture.detectChanges();

    const emptyState = fixture.debugElement.query(By.css('.empty-state'));
    expect(emptyState).toBeTruthy();
  });

  it('should calculate total balance correctly', () => {
    const contasMock: ContaBeneficio[] = [
      { id: 1, clienteId: 1, clienteNome: 'Ana', beneficioId: 1, saldo: 100 },
      { id: 2, clienteId: 2, clienteNome: 'Bruno', beneficioId: 1, saldo: 200 },
      { id: 3, clienteId: 3, clienteNome: 'Carla', beneficioId: 1, saldo: 300 },
    ];

    beneficioService.getBeneficios = () => of([]);
    contaBeneficioService.getContas = () => of(contasMock);
    transacaoBeneficioService.listarTransacoes = () => of([]);

    createComponent();
    fixture.detectChanges();

    expect(component.getTotalSaldo()).toBe(600);
  });
});
