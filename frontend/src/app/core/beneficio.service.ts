import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Beneficio } from '../shared/models/beneficio.model';
import { API_BASE_URL } from './api.config';

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
}
