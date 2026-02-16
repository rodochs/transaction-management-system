import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { ClienteService, ClienteRequest } from './cliente.service';
import { Cliente } from '../shared/models/cliente.model';

describe('ClienteService', () => {
  let service: ClienteService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ClienteService],
    });
    service = TestBed.inject(ClienteService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should fetch all clientes', () => {
    const mockClientes: Cliente[] = [
      { id: 1, nome: 'Ana Silva', email: 'ana@email.com' },
      { id: 2, nome: 'Bruno Santos', email: 'bruno@email.com' },
    ];

    service.getClientes().subscribe((clientes) => {
      expect(clientes.length).toBe(2);
      expect(clientes).toEqual(mockClientes);
    });

    const req = httpMock.expectOne((r) => r.url.includes('/api/v1/clientes'));
    expect(req.request.method).toBe('GET');
    req.flush(mockClientes);
  });

  it('should create a cliente', () => {
    const request: ClienteRequest = { nome: 'Novo Cliente', email: 'novo@email.com' };
    const mockResponse: Cliente = { id: 1, nome: 'Novo Cliente', email: 'novo@email.com' };

    service.createCliente(request).subscribe((cliente) => {
      expect(cliente).toEqual(mockResponse);
    });

    const req = httpMock.expectOne((r) => r.url.includes('/api/v1/clientes'));
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(request);
    req.flush(mockResponse);
  });
});
