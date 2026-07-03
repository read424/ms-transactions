# MS-Transaction Microservice

## Descripción

**MS-Transaction** es el microservicio responsable de gestionar todas las transacciones bancarias en la plataforma de banking. Implementa una arquitectura hexagonal con Domain Driven Design (DDD) y utiliza un enfoque completamente reactivo con Spring WebFlux.

### Responsabilidades Principales

- Procesamiento de depósitos, retiros y transferencias
- Validación de cuentas y créditos
- Comunicación con MS-Accounts y MS-Credits mediante HTTP
- Publicación de eventos de transacciones completadas a Kafka
- Almacenamiento de historial de transacciones en MongoDB
- Caching con Redis para optimización de lecturas

## Stack Tecnológico

- **Java 17** con Spring Boot 3.5.x
- **Spring WebFlux** - Programación reactiva
- **Spring Cloud** - Service discovery (Eureka), configuración centralizada
- **MongoDB** - Base de datos NoSQL para persistencia
- **Redis** - Caching distribuido
- **Kafka** - Message broker para arquitectura event-driven
- **Resilience4j** - Circuit breaker, retry y time limiter
- **OpenTelemetry** - Observabilidad y trazabilidad

## Arquitectura Hexagonal

```
┌─────────────────────────────────────────────────┐
│           Presentación (REST APIs)              │
├─────────────────────────────────────────────────┤
│ Application Layer (Use Cases / Input Ports)     │
├─────────────────────────────────────────────────┤
│  Domain Layer (Entities, Value Objects, Events) │
├─────────────────────────────────────────────────┤
│ Infrastructure (Adapters, Output Ports)         │
└─────────────────────────────────────────────────┘
```

### Capas

- **domain/**: Entidades, Value Objects, eventos de dominio
- **application/**: Puertos (interfaces) de casos de uso
- **infrastructure/**: Adaptadores HTTP, Kafka, MongoDB, Redis

## Requisitos Previos

- Docker y Docker Compose
- Maven 3.9+
- Java 17 JDK
- Red Docker externa `bootcamp-network`

## Levantamiento Local

### 1. Crear la red Docker (si no existe)

```bash
docker network create bootcamp-network
```

### 2. Levantar infraestructura (MongoDB, Redis)

```bash
docker compose up -d mongodb redis
```

### 3. Iniciar el microservicio

**Opción A: Con Docker Compose (recomendado)**

```bash
docker compose up -d ms-transaction
```

**Opción B: Localmente con Maven**

```bash
# Compilar
mvn clean package -DskipTests

# Ejecutar
java -jar target/ms-transaction-*.jar
```

## Configuración

### Variables de Entorno

```yaml
# Spring Profiles
SPRING_PROFILES_ACTIVE: prod|dev

# Config Server
SPRING_CONFIG_IMPORT: configserver:http://service-config-server:8888

# Eureka Discovery
EUREKA_CLIENT_SERVICE_URL_DEFAULT_ZONE: http://service-eureka:8761/eureka/

# Redis
SPRING_REDIS_HOST: redis-transaction
SPRING_REDIS_PORT: 6379
SPRING_REDIS_PASSWORD: eYVX7EwVmmxKPCDmwMtyKVge8oLd2t81

# Kafka
SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka_broker:9092

# Memory
JAVA_OPTS: -Xmx512m -Xms256m
```

### Archivos de Configuración

- **application.yaml** - Configuración local (dev)
- **application-prod.yaml** - Config Server (centralizada)

Las credenciales de MongoDB se obtienen desde el Config Server:
- Host: `mongodb-transaction`
- Puerto: `27017`
- Database: `ms_transaction_prod`
- Usuario: `admin`
- Contraseña: Encriptada en config server

## Endpoints

### Health Check

```bash
curl http://localhost:8084/actuator/health
```

### Respuesta esperada
```json
{
  "status": "UP",
  "groups": ["circuit-breaker", "readiness"],
  "components": {
    "circuitBreakerHealth": {
      "status": "UP",
      "details": {
        "service": "Account Validation Service",
        "circuitBreakerState": "CLOSED"
      }
    }
  }
}
```

## Puertos

- **8084** - API REST del microservicio
- **27020** - MongoDB (container)
- **6382** - Redis (container)

## Databases

### MongoDB - ms_transaction_prod

Colecciones:
- `transactions` - Historial de transacciones

### Redis

- Cache de validaciones
- Sessions

## Kafka Topics

- `transaction.deposit.completed` - Depósito completado
- `transaction.withdrawal.completed` - Retiro completado
- `transaction.transfer.completed` - Transferencia completada
- `transaction.credit.payment.completed` - Pago de crédito completado

## Service Discovery

El microservicio se registra automáticamente en Eureka con el nombre:

```
ms-transaction
```

**Clientes internos** pueden invocar:
```
http://ms-transaction:8084/...
```

## Circuito de Validación

Implementa Circuit Breaker para validaciones:

- **Account Validation Service** - Valida cuentas en MS-Accounts
- **Credit Validation Service** - Valida créditos en MS-Credits

Configuración:
- Sliding window: 10 llamadas
- Failure rate: 50%
- Timeout: 2 segundos
- Reintentos: 3

## Desarrollo

### Compilar

```bash
mvn clean package
```

### Compilar sin tests

```bash
mvn clean package -DskipTests
```

### Ejecutar tests

```bash
mvn test
```

### Construir imagen Docker

```bash
docker build -t read424/ms-transaction:latest .
docker tag read424/ms-transaction:latest read424/ms-transaction:v1.0.0
```

### Logs

```bash
# Container
docker logs -f ms-transaction

# Docker Compose
docker compose logs -f ms-transaction
```

## Troubleshooting

### Container no inicia

Verificar logs:
```bash
docker logs ms-transaction | grep -i error
```

### MongoDB no conecta

Verificar que `mongodb-transaction` esté corriendo:
```bash
docker ps | grep mongodb-transaction
```

Verificar credenciales en config-server:
```bash
curl http://localhost:8888/ms-transaction/prod
```

### Kafka desconectado

Verificar bootstrap servers:
```bash
# Debe estar accesible en docker-compose
SPRING_KAFKA_BOOTSTRAP_SERVERS: kafka_broker:9092
```

## Monitoreo

### Métricas Prometheus

```bash
curl http://localhost:8084/actuator/prometheus
```

### Healthchecks detallados

```bash
curl http://localhost:8084/actuator/health?pretty
```

## Referencias

- [Spring WebFlux Docs](https://spring.io/projects/spring-webflux)
- [Reactive Kafka](https://projectreactor.io/docs/kafka/release/reference/)
- [MongoDB Reactive](https://spring.io/projects/spring-data-mongodb)
- [Resilience4j](https://resilience4j.readme.io/)
