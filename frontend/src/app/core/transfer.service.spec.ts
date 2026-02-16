import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { TransferService } from './transfer.service';
import { TransferRequest } from '../shared/models/transfer-request.model';
import { TransferResult } from '../shared/models/transfer-result.model';

describe('TransferService', () => {
  let service: TransferService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [TransferService],
    });

    service = TestBed.inject(TransferService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should execute transfer', () => {
    const request: TransferRequest = {
      fromAccountId: 5,
      toAccountId: 6,
      amount: 150,
    };

    const mockResult: TransferResult = {
      fromAccountId: 5,
      toAccountId: 6,
      amount: 150,
    };

    service.transfer(request).subscribe((result) => {
      expect(result).toEqual(mockResult);
    });

    const req = httpMock.expectOne((requestReceived) => {
      return requestReceived.method === 'POST' && requestReceived.url.endsWith('/api/v1/transfers');
    });
    expect(req.request.body).toEqual(request);
    req.flush(mockResult);
  });
});
