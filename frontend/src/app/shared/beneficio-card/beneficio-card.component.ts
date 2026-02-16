import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { CurrencyPipe } from '@angular/common';
import { Beneficio } from '../models/beneficio.model';

@Component({
  selector: 'app-beneficio-card',
  standalone: true,
  imports: [CurrencyPipe],
  templateUrl: './beneficio-card.component.html',
  styleUrl: './beneficio-card.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BeneficioCardComponent {
  @Input({ required: true }) beneficio!: Beneficio;
}
