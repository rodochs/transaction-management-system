import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { BeneficioService } from './beneficio.service';
import { Beneficio } from '../shared/models/beneficio.model';

describe('BeneficioService', () => {
  let service: BeneficioService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [BeneficioService],
    });

    service = TestBed.inject(BeneficioService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should fetch list of beneficios', () => {
    const mockResponse: Beneficio[] = [
      { id: 1, nome: 'Vale Alimentação', descricao: 'Cartão alimentação', valor: 500, ativo: true },
    ];

    service.getBeneficios().subscribe((beneficios) => {
      expect(beneficios).toEqual(mockResponse);
    });

    const req = httpMock.expectOne((request) => {
      return request.method === 'GET' && request.url.endsWith('/api/v1/beneficios');
    });
    expect(req.request.method).toBe('GET');
    req.flush(mockResponse);
  });

  it('should fetch a single beneficio by id', () => {
    const mockBeneficio: Beneficio = {
      id: 1,
      nome: 'Vale Refeição',
      descricao: 'Cartão refeição',
      valor: 600,
      ativo: true,
    };

    service.getBeneficio(1).subscribe((beneficio) => {
      expect(beneficio).toEqual(mockBeneficio);
    });

    const req = httpMock.expectOne((request) => {
      return request.method === 'GET' && request.url.endsWith('/api/v1/beneficios/1');
    });
    req.flush(mockBeneficio);
  });
});
