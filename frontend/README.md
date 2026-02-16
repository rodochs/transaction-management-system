# Frontend - Sistema de Gestão de Benefícios

Aplicação Angular para gerenciamento de benefícios corporativos.

## Tecnologias

- **Angular 21** com Standalone Components
- **TypeScript** com strict mode
- **Vitest** para testes unitários
- **RxJS** para programação reativa

## Desenvolvimento

```bash
# Instalar dependências
npm install

# Servidor de desenvolvimento (http://localhost:4200)
ng serve

# Executar testes
npm test

# Build de produção
npm run build
```

## Estrutura

```
src/app/
├── core/                    # Serviços e configurações
│   ├── beneficio.service.ts
│   ├── cliente.service.ts
│   ├── conta-beneficio.service.ts
│   ├── transacao-beneficio.service.ts
│   └── transfer.service.ts
├── dashboard/               # Componente principal
│   ├── beneficio-modal/     # Modal CRUD de benefícios
│   ├── cliente-modal/       # Modal CRUD de colaboradores
│   ├── conta-modal/         # Modal criação de contas
│   └── transfer-modal/      # Modal de transferências
└── shared/                  # Modelos e componentes compartilhados
```

## Funcionalidades

- ✅ Dashboard com resumo de saldos e contas
- ✅ CRUD completo de Benefícios
- ✅ CRUD completo de Colaboradores
- ✅ Criação de Contas de Benefício
- ✅ Transferências entre contas
- ✅ Histórico de transações

## Configuração da API

O endpoint da API está configurado em `src/app/core/api.config.ts`:

```typescript
export const API_BASE_URL = 'http://localhost:8080';
```
