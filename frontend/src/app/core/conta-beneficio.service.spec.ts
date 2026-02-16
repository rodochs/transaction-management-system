import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ContaBeneficioService } from './conta-beneficio.service';
import { ContaBeneficio } from '../shared/models/conta-beneficio.model';

describe('ContaBeneficioService', () => {
  let service: ContaBeneficioService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ContaBeneficioService],
    });
    service = TestBed.inject(ContaBeneficioService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all contas', () => {
    const mockContas: ContaBeneficio[] = [
      { id: 1, clienteId: 1, clienteNome: 'Cliente 1', beneficioId: 1, beneficioNome: 'Vale Alimentação', saldo: 1000 },
      { id: 2, clienteId: 1, clienteNome: 'Cliente 1', beneficioId: 2, beneficioNome: 'Vale Refeição', saldo: 500 },
    ];

    service.getContas().subscribe((contas) => {
      expect(contas.length).toBe(2);
      expect(contas).toEqual(mockContas);
    });

    const req = httpMock.expectOne((r) => r.url.includes('/api/v1/contas-beneficio'));
    expect(req.request.method).toBe('GET');
    req.flush(mockContas);
  });

  it('should fetch conta by id', () => {
    const mockConta: ContaBeneficio = { id: 1, clienteId: 1, clienteNome: 'Cliente 1', beneficioId: 1, beneficioNome: 'Vale Alimentação', saldo: 1000 };

    service.getContaById(1).subscribe((conta) => {
      expect(conta).toEqual(mockConta);
    });

    const req = httpMock.expectOne((r) => r.url.includes('/api/v1/contas-beneficio/1'));
    expect(req.request.method).toBe('GET');
    req.flush(mockConta);
  });
});
