import { NgForOf, NgIf } from '@angular/common';
import { ChangeDetectionStrategy, Component, OnInit, signal } from '@angular/core';
import { catchError, finalize, of } from 'rxjs';
import { BeneficioService } from '../core/beneficio.service';
import { Beneficio } from '../shared/models/beneficio.model';
import { BeneficioCardComponent } from '../shared/beneficio-card/beneficio-card.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [NgIf, NgForOf, BeneficioCardComponent],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DashboardComponent implements OnInit {
  beneficios = signal<Beneficio[] | null>(null);
  loading = signal<boolean>(false);
  error = signal<string | null>(null);

  constructor(private readonly beneficioService: BeneficioService) {}

  ngOnInit(): void {
    this.loadBeneficios();
  }

  loadBeneficios(): void {
    this.loading.set(true);
    this.error.set(null);

    this.beneficioService
      .getBeneficios()
      .pipe(
        catchError((err) => {
          this.error.set('Não foi possível carregar os benefícios. Tente novamente.');
          return of([] as Beneficio[]);
        }),
        finalize(() => this.loading.set(false))
      )
      .subscribe((items) => this.beneficios.set(items));
  }

  trackById(index: number, item: Beneficio): number {
    return item.id;
  }
}
