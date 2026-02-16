import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TransacaoBeneficio } from '../shared/models/transacao-beneficio.model';
import { API_BASE_URL } from './api.config';

@Injectable({
  providedIn: 'root',
})
export class TransacaoBeneficioService {
  private readonly baseUrl = `${API_BASE_URL}/api/v1/transacoes`;

  constructor(private readonly http: HttpClient) {}

  listarTransacoes(): Observable<TransacaoBeneficio[]> {
    return this.http.get<TransacaoBeneficio[]>(this.baseUrl);
  }
}
