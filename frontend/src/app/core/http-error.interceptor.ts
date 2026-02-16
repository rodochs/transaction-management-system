import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { ErrorNotificationService } from './error-notification.service';

export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const notifier = inject(ErrorNotificationService);

  return next(req).pipe(
    catchError((error: unknown) => {
      if (error instanceof HttpErrorResponse) {
        let message = 'Ocorreu um erro inesperado ao realizar a operação.';

        if (error.error && typeof error.error === 'object' && 'error' in error.error) {
          const apiError = error.error as { error?: string; message?: string };
          if (apiError.message) {
            message = apiError.message;
          } else if (apiError.error) {
            message = apiError.error;
          }
        } else if (error.status === 0) {
          message = 'Não foi possível conectar ao servidor. Verifique se o backend está em execução.';
        }

        notifier.notify(message);
      }

      return throwError(() => error);
    })
  );
};
