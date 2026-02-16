import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TransacaoBeneficioService } from './transacao-beneficio.service';
import { TransacaoBeneficio } from '../shared/models/transacao-beneficio.model';

describe('TransacaoBeneficioService', () => {
  let service: TransacaoBeneficioService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TransacaoBeneficioService],
    });
    service = TestBed.inject(TransacaoBeneficioService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all transacoes', () => {
    const mockTransacoes: TransacaoBeneficio[] = [
      { id: 1, contaOrigemId: 1, contaDestinoId: 2, valor: 100, tipo: 'TRANSFERENCIA', dataHora: '2026-02-16T10:00:00' },
    ];

    service.listarTransacoes().subscribe((transacoes) => {
      expect(transacoes.length).toBe(1);
      expect(transacoes).toEqual(mockTransacoes);
    });

    const req = httpMock.expectOne((r) => r.url.includes('/api/v1/transacoes'));
    expect(req.request.method).toBe('GET');
    req.flush(mockTransacoes);
  });
});
