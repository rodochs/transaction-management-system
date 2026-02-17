# Stage 1: Build
FROM node:20-alpine AS build
WORKDIR /app

# Cache de dependências
COPY frontend/package*.json ./
RUN npm ci --silent

# Build da aplicação
COPY frontend/ ./
RUN npm run build -- --configuration production

# Stage 2: Nginx
FROM nginx:1.25-alpine

# Copiar build do Angular
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html

# Configuração customizada do Nginx
COPY docker/frontend/nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost/ || exit 1

CMD ["nginx", "-g", "daemon off;"]
