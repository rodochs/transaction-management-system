import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Observable, of, throwError } from 'rxjs';
import { DashboardComponent } from './dashboard.component';
import { BeneficioService } from '../core/beneficio.service';
import { Beneficio } from '../shared/models/beneficio.model';
import { By } from '@angular/platform-browser';

class BeneficioServiceMock {
  getBeneficios: () => Observable<Beneficio[]> = () => of([]);
}

describe('DashboardComponent', () => {
  let component: DashboardComponent;
  let fixture: ComponentFixture<DashboardComponent>;
  let beneficioService: BeneficioServiceMock;

  beforeEach(async () => {
    beneficioService = new BeneficioServiceMock();

    await TestBed.configureTestingModule({
      imports: [DashboardComponent],
      providers: [{ provide: BeneficioService, useValue: beneficioService }],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(DashboardComponent);
    component = fixture.componentInstance;
  }

  it('should show loading state and then list of benefits', () => {
    const beneficiosMock: Beneficio[] = [
      { id: 1, nome: 'Vale Alimentação', descricao: 'Cartão alimentação', valor: 500, ativo: true },
      { id: 2, nome: 'Vale Refeição', descricao: 'Cartão refeição', valor: 600, ativo: true },
    ];

    beneficioService.getBeneficios = () => of(beneficiosMock);

    createComponent();
    fixture.detectChanges();

    // Após detecção inicial, a chamada ao serviço já foi resolvida (of)
    expect(component.loading()).toBe(false);
    const cards = fixture.debugElement.queryAll(By.css('app-beneficio-card'));
    expect(cards.length).toBe(2);
  });

  it('should show empty state when there are no benefits', () => {
    beneficioService.getBeneficios = () => of([]);

    createComponent();
    fixture.detectChanges();

    const emptyMessage = fixture.debugElement.query(By.css('.state-message'));
    expect(emptyMessage.nativeElement.textContent).toContain('Nenhum benefício cadastrado');
  });

  it('should show error state when service fails', () => {
    beneficioService.getBeneficios = () => throwError(() => new Error('Erro de API'));

    createComponent();
    fixture.detectChanges();

    const errorMessage = fixture.debugElement.query(By.css('.state-message--error'));
    expect(errorMessage.nativeElement.textContent).toContain('Não foi possível carregar os benefícios');
  });
});
