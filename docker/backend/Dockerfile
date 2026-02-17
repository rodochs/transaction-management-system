# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build

# Copiar e instalar EJB module primeiro (dependência do backend)
COPY ejb-module/pom.xml ./ejb-module/
COPY ejb-module/src ./ejb-module/src
RUN mvn -f ejb-module/pom.xml clean install -DskipTests -B -q

# Copiar e buildar backend
COPY backend-module/pom.xml ./backend-module/
COPY backend-module/src ./backend-module/src
RUN mvn -f backend-module/pom.xml clean package -DskipTests -B -q

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Criar usuário não-root para segurança
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar JAR
COPY --from=build /build/backend-module/target/*.jar app.jar

# Configurações
ENV JAVA_OPTS="-Xmx512m -Xms256m"
ENV SPRING_PROFILES_ACTIVE=docker

EXPOSE 8080

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
