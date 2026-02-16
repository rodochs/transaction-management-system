import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ContaBeneficio } from '../shared/models/conta-beneficio.model';
import { API_BASE_URL } from './api.config';

@Injectable({
  providedIn: 'root',
})
export class ContaBeneficioService {
  private readonly baseUrl = `${API_BASE_URL}/api/v1/contas-beneficio`;

  constructor(private readonly http: HttpClient) {}

  getContas(): Observable<ContaBeneficio[]> {
    return this.http.get<ContaBeneficio[]>(this.baseUrl);
  }

  getContaById(id: number): Observable<ContaBeneficio> {
    return this.http.get<ContaBeneficio>(`${this.baseUrl}/${id}`);
  }
}
