import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Beneficio } from '../shared/models/beneficio.model';
import { API_BASE_URL } from './api.config';

export interface BeneficioRequest {
  nome: string;
  descricao: string;
  valor: number;
  ativo: boolean;
}

@Injectable({ providedIn: 'root' })
export class BeneficioService {
  private readonly baseUrl = `${API_BASE_URL}/api/v1/beneficios`;

  constructor(private http: HttpClient) {}

  getBeneficios(): Observable<Beneficio[]> {
    return this.http.get<Beneficio[]>(this.baseUrl);
  }

  getBeneficio(id: number): Observable<Beneficio> {
    return this.http.get<Beneficio>(`${this.baseUrl}/${id}`);
  }

  createBeneficio(request: BeneficioRequest): Observable<Beneficio> {
    return this.http.post<Beneficio>(this.baseUrl, request);
  }

  updateBeneficio(id: number, request: BeneficioRequest): Observable<Beneficio> {
    return this.http.put<Beneficio>(`${this.baseUrl}/${id}`, request);
  }

  deleteBeneficio(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
