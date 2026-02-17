// Detecta se está rodando via Docker (proxy reverso do Nginx na porta 80)
// ou localmente (acesso direto ao backend na porta 8080)
const isDockerEnvironment = (): boolean => {
  const port = window.location.port;
  return port === '' || port === '80';
};

// Em Docker: usa proxy do Nginx (mesma origem, /api -> backend:8080)
// Em desenvolvimento local: acessa diretamente localhost:8080
export const API_BASE_URL = isDockerEnvironment()
  ? ''  // Usa mesma origem, Nginx faz proxy de /api para backend
  : 'http://localhost:8080';
