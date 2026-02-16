import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TransferRequest } from '../shared/models/transfer-request.model';
import { TransferResult } from '../shared/models/transfer-result.model';
import { API_BASE_URL } from './api.config';

@Injectable({ providedIn: 'root' })
export class TransferService {
  private readonly baseUrl = `${API_BASE_URL}/api/v1/transfers`;

  constructor(private http: HttpClient) {}

  transfer(request: TransferRequest): Observable<TransferResult> {
    return this.http.post<TransferResult>(this.baseUrl, request);
  }
}
