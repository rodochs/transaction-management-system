import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { BeneficioCardComponent } from './beneficio-card.component';
import { Beneficio } from '../models/beneficio.model';

describe('BeneficioCardComponent', () => {
  let component: BeneficioCardComponent;
  let fixture: ComponentFixture<BeneficioCardComponent>;

  const beneficioMock: Beneficio = {
    id: 1,
    nome: 'Vale Alimentação',
    descricao: 'Cartão alimentação',
    valor: 500,
    ativo: true,
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BeneficioCardComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(BeneficioCardComponent);
    component = fixture.componentInstance;
    component.beneficio = beneficioMock;
    fixture.detectChanges();
  });

  it('should render benefit name, description and value', () => {
    const titleEl = fixture.debugElement.query(By.css('.card-title')).nativeElement;
    const descriptionEl = fixture.debugElement.query(By.css('.card-description')).nativeElement;
    const amountEl = fixture.debugElement.query(By.css('.card-amount')).nativeElement;

    expect(titleEl.textContent).toContain(beneficioMock.nome);
    expect(descriptionEl.textContent).toContain(beneficioMock.descricao);
    expect(amountEl.textContent).toContain('500');
  });

  it('should show active tag when benefit is active', () => {
    const tagEl = fixture.debugElement.query(By.css('.card-tag')).nativeElement;
    expect(tagEl.textContent).toContain('Ativo');
  });
});
