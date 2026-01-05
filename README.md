# E-Commerce Backend – Spring Boot Microservices Architecture

This project is a robust e-commerce backend built using Java and Spring Boot, following a microservices architecture. It demonstrates inter-service communication using gRPC, centralized routing with Spring Cloud Gateway, and observability with Prometheus and Grafana.

## 🏗 Architecture Overview

The system consists of the following microservices:

- **API Gateway** (`api-gateway`): The entry point for all client requests. Handles routing, security, and load balancing.
- **User Service** (`user-service`): Manages user registration and authentication.
- **Order Service** (`order-service`): Handles order placement and management. Communicates with Inventory and Payment services via gRPC.
- **Inventory Service** (`inventory-service`): Manages product stock levels.
- **Payment Service** (`payment-service`): Processes payments.

## Key Features

### 🔐 Security

- JWT-based authentication with decentralized token validation
- Mutual TLS (mTLS) for secure gRPC service-to-service communication
- Internal CA, SANs, EKU, PKCS#8 keys configuration

### ⚡ Performance & Communication

- REST APIs for external clients
- gRPC for low-latency internal communication
- Manual gRPC channel configuration for correct TLS binding

### 🛡 Reliability & Fault Tolerance

- Optimistic locking in Inventory Service to prevent overselling
- Automatic retries for transient failures
- Circuit breakers and timeouts using Resilience4j

### 📊 Monitoring & Observability

- Metrics exposed via Micrometer
- Monitoring dashboards using Prometheus and Grafana

### 🐳 Deployment

- Dockerized all services
- Production-ready container configuration

## 🛠 Tech Stack

- **Language**: Java 17
- **Framework**: Spring Boot 3.5.x, Spring Cloud Gateway (WebFlux)
- **Communication**: REST (External), gRPC (Internal)
- **Database**: PostgreSQL (JPA/Hibernate)
- **Observability**: Micrometer, Prometheus, Grafana
- **Containerization**: Docker, Docker Compose

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Maven
- Docker & Docker Compose

### Running the Project

1.  **Build the services**:

    ```bash
    mvn clean install
    ```

2.  **Start the infrastructure and services**:
    ```bash
    docker-compose up --build
    ```

## 🔌 API Endpoints

All requests should be routed through the API Gateway (Port `8080`).

| Service   | Method | Endpoint              | Description                        |
| :-------- | :----- | :-------------------- | :--------------------------------- |
| **User**  | POST   | `/api/users/register` | Register a new user                |
| **User**  | POST   | `/api/users/login`    | Login and get JWT                  |
| **Order** | POST   | `/api/orders`         | Create a new order (Requires Auth) |

## 📊 Observability

The project includes a monitoring stack to track application health and business metrics.

- **Prometheus**: Scrapes metrics from the services.
- **Grafana**: Visualizes metrics.
  - URL: `http://localhost:3000`
  - **Key Metrics**:
    - `orders_total`: Counter for orders.

### Metric Tags

The `orders_total` metric supports the following tags for filtering in Grafana:

- `status`: `created`, `failed`
- `reason`: `payment_rejected`, `insufficient_stock`, `payment_successful`

Example Query:

```promql
orders_total{status="failed"}
```

## 🔒 Generating Certificates for gRPC (mTLS)

To enable secure mutual TLS (mTLS) communication between microservices, you must generate an internal Certificate Authority (CA) and issue certificates for the services.

Create a directory named `certs` in the root of the project before running these commands.

### 1. Create an Internal CA

```bash
cd certs

# Generate CA private key
openssl genrsa -out ca.key 4096

# Generate CA certificate
openssl req -x509 -new -nodes -key ca.key -sha256 -days 3650 -out ca.crt \
  -subj "/CN=internal-ca"
```

### 2. Generate Server Certificates (Inventory & Payment)

Generate certificates for the gRPC servers.

**For Inventory Service:**

```bash
# Generate private key
openssl genrsa -out inventory.key 2048

# Generate CSR
openssl req -new -key inventory.key -out inventory.csr \
  -subj "/CN=inventory-service"

# Sign CSR with CA
openssl x509 -req -in inventory.csr -CA ca.crt -CAkey ca.key -CAcreateserial \
  -out inventory.crt -days 365 -sha256 \
  -extfile <(printf "subjectAltName=DNS:inventory-service")
```

**For Payment Service:**

```bash
# Generate private key
openssl genrsa -out payment.key 2048

# Generate CSR
openssl req -new -key payment.key -out payment.csr \
  -subj "/CN=payment-service"

# Sign CSR with CA
openssl x509 -req -in payment.csr -CA ca.crt -CAkey ca.key -CAcreateserial \
  -out payment.crt -days 365 -sha256 \
  -extfile <(printf "subjectAltName=DNS:payment-service")
```

### 3. Generate Client Certificate (Order Service)

The Order Service acts as a client and needs a certificate for mutual authentication.

```bash
# Generate private key
openssl genrsa -out order.key 2048

# Generate CSR
openssl req -new -key order.key -out order.csr \
  -subj "/CN=order-service"

# Sign CSR with CA
openssl x509 -req -in order.csr -CA ca.crt -CAkey ca.key -CAcreateserial \
  -out order.crt -days 365 -sha256 \
  -extfile <(printf "extendedKeyUsage=clientAuth\nsubjectAltName=DNS:order-service")
```

### 4. Verify Certificates

```bash
openssl verify -CAfile ca.crt inventory.crt payment.crt order.crt
```
