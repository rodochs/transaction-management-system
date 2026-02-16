import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ErrorNotificationService {
  readonly message = signal<string | null>(null);

  notify(message: string): void {
    this.message.set(message);
  }

  clear(): void {
    this.message.set(null);
  }
}
