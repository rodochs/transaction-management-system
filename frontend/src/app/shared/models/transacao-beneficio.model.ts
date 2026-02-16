export interface TransacaoBeneficio {
  id: number;
  contaOrigemId: number;
  contaDestinoId: number | null;
  valor: number;
  tipo: 'CREDITO' | 'DEBITO' | 'TRANSFERENCIA';
  dataHora: string;
}
