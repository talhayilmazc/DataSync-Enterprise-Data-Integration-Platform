# DataSync — Enterprise Data Integration Platform

> Production-grade data integration platform with Spring Cloud microservices, Kafka event streaming, Circuit Breaker, Redis caching, and distributed tracing.
> Java 17 + Spring Boot 3 + Spring Cloud + Kafka + MongoDB + PostgreSQL + Redis + Docker + CI/CD

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3-green?logo=springboot)
![Spring Cloud](https://img.shields.io/badge/Spring%20Cloud-2023.0-blue?logo=spring)
![Kafka](https://img.shields.io/badge/Apache%20Kafka-3.7-black?logo=apachekafka)
![MongoDB](https://img.shields.io/badge/MongoDB-7-green?logo=mongodb)
![Redis](https://img.shields.io/badge/Redis-7-red?logo=redis)
![Docker](https://img.shields.io/badge/Docker-Compose-blue?logo=docker)
![CI](https://github.com/talhayilmazc/DataSync-Enterprise-Data-Integration-Platform/actions/workflows/ci.yml/badge.svg)

---

## 🔄 What This System Does

DataSync is a production-style enterprise data integration platform. It models how real financial institutions and large-scale systems synchronize data across heterogeneous data sources — PostgreSQL, MongoDB, REST APIs, Kafka streams, and more.

This is **not a simple ETL demo** — it implements:

- ✅ Multi-source data integration (PostgreSQL, MongoDB, Redis, REST API, Kafka, CSV)
- ✅ Full sync job lifecycle (Pending → Running → Completed/Failed → Cancelled/Paused)
- ✅ Event-driven architecture with Apache Kafka
- ✅ Event sourcing with MongoDB (immutable sync event log)
- ✅ Circuit Breaker pattern with Resilience4j
- ✅ Service discovery with Eureka
- ✅ Redis caching layer for high-performance reads
- ✅ Distributed tracing with Micrometer
- ✅ Dual-database architecture (PostgreSQL + MongoDB)
- ✅ OpenAPI/Swagger documentation
- ✅ GitHub Actions CI/CD pipeline
- ✅ Docker Compose ready
- ✅ 13 unit tests

---

## 🏗️ Architecture
REST Client
│
▼
REST Controllers (DataSource, SyncJob, SyncLog)
│
▼
Service Layer ──► PostgreSQL (JPA) — operational data
│
├──► Redis Cache — high-performance reads
│
├──► Kafka Producer ──► sync-events / sync-completed / sync-failed topics
│
└──► Kafka Consumer ──► MongoDB (event sourcing)

---

## ⚙️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.3, Spring MVC |
| Cloud | Spring Cloud 2023.0, Eureka, Resilience4j, OpenFeign |
| Event Streaming | Apache Kafka 3.7 — 3 topics, 3 partitions each |
| Primary DB | PostgreSQL 15 + Hibernate/JPA ORM |
| Event Store | MongoDB 7 — immutable event sourcing |
| Caching | Redis 7 — data source cache |
| API Docs | SpringDoc OpenAPI / Swagger UI |
| Build | Maven 3.9 |
| DevOps | Docker, Docker Compose, GitHub Actions CI/CD |
| Testing | JUnit 5, Mockito, AssertJ — 13 unit tests |

---

## 📊 Domain Model

### DataSource
Represents a data connection (PostgreSQL, MongoDB, REST API, etc.)
- Connection URL, credentials, type
- Status tracking (PENDING, COMPLETED, FAILED)
- Retry count and error logging
- Active/inactive toggle

### SyncJob
Represents a data synchronization task between two data sources
- Source → Target mapping
- Sync types: FULL, INCREMENTAL, REAL_TIME, SCHEDULED
- Cron expression support for scheduled jobs
- Job lifecycle: PENDING → RUNNING → COMPLETED/FAILED

### SyncLog
Execution history for each sync job run
- Start/end timestamps
- Records processed and failed counts
- Error details

### SyncEvent (MongoDB)
Immutable event log — every sync lifecycle event stored in MongoDB
- SYNC_STARTED, SYNC_COMPLETED, SYNC_FAILED, CIRCUIT_BREAKER_OPEN, RETRY_ATTEMPTED

---

## 🚀 Kafka Topics

| Topic | Purpose | Partitions |
|---|---|---|
| `sync-events` | All sync lifecycle events | 3 |
| `sync-completed` | Successful completion events | 3 |
| `sync-failed` | Failure events with error details | 3 |

---

## 📁 Project Structure
src/main/java/com/datasync/datasync/
├── config/          # JPA, Redis, Kafka, Security, OpenAPI
├── controller/      # DataSource, SyncJob, SyncLog REST controllers
├── domain/
│   ├── document/    # SyncEvent (MongoDB)
│   ├── entity/      # DataSource, SyncJob, SyncLog (PostgreSQL)
│   ├── enums/       # DataSourceType, SyncStatus, SyncType, EventType
│   └── repository/  # JPA + MongoDB repositories
├── dto/
│   ├── request/     # DataSourceRequest, SyncJobRequest, SyncJobTriggerRequest
│   └── response/    # DataSourceResponse, SyncJobResponse, SyncStatsResponse...
├── exception/       # Global exception handling
├── kafka/
│   ├── producer/    # SyncEventProducer
│   └── consumer/    # SyncEventConsumer
└── service/         # Business logic (interfaces + implementations)

---

## 🚀 Running Locally

### Prerequisites
- Docker Desktop
- Java 17
- Maven 3.9+

### Start all services

```bash
docker compose up -d
```

This starts:
- **App** → http://localhost:8083
- **PostgreSQL** → localhost:5434
- **MongoDB** → localhost:27017
- **Redis** → localhost:6381
- **Kafka** → localhost:9093
- **Kafka UI** → http://localhost:8091

### API Documentation
http://localhost:8083/swagger-ui/index.html

### Health Check
http://localhost:8083/actuator/health

---

## 📊 API Endpoints

### Data Sources
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/data-sources` | Create data source |
| GET | `/api/v1/data-sources` | List all |
| GET | `/api/v1/data-sources/active` | List active |
| PUT | `/api/v1/data-sources/{id}` | Update |
| DELETE | `/api/v1/data-sources/{id}` | Delete |
| POST | `/api/v1/data-sources/{id}/test` | Test connection |
| POST | `/api/v1/data-sources/{id}/activate` | Activate |

### Sync Jobs
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/v1/sync-jobs` | Create sync job |
| GET | `/api/v1/sync-jobs` | List all jobs |
| GET | `/api/v1/sync-jobs/pending` | List pending |
| POST | `/api/v1/sync-jobs/trigger` | Trigger job |
| POST | `/api/v1/sync-jobs/{jobId}/cancel` | Cancel |
| POST | `/api/v1/sync-jobs/{jobId}/pause` | Pause |
| POST | `/api/v1/sync-jobs/{jobId}/resume` | Resume |
| GET | `/api/v1/sync-jobs/stats` | Statistics |

---

## 🧪 Testing

```bash
mvn test
```

- ✅ DataSourceServiceTest — 6 unit tests
- ✅ SyncJobServiceTest — 6 unit tests
- ✅ DatasyncApplicationTests — context loads

---

## 🔄 CI/CD Pipeline

GitHub Actions pipeline on every push:

1. **Build** — `mvn clean compile`
2. **Test** — `mvn test` with PostgreSQL + MongoDB + Redis
3. **Docker Build** — builds image on `main` and `develop`

---

## 👨‍💻 Author

**Talha Yılmaz**
[github.com/talhayilmazc](https://github.com/talhayilmazc) · [linkedin.com/in/talha-yilmaz-38a13a225](https://linkedin.com/in/talha-yilmaz-38a13a225)