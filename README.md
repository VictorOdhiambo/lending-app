# Lending App - Spring boot Microservices with PostgreSQL

This project consists of multiple Spring Boot microservices orchestrated with Docker Compose and connected to a shared PostgreSQL database. Services include:

- Discovery Service (Eureka)
- API Gateway
- Product Service
- Customer Service
- Loan Service
- Notification Service
- Scheduler Service
- Report Service

---

## 🧱 Services Overview

| Service               | Port  | Description                         |
|-----------------------|-------|-------------------------------------|
| PostgreSQL Database   | 5442  | Central database for all services   |
| Discovery Service     | 8761  | Eureka service registry             |
| API Gateway           | 8800  | Gateway for routing external traffic|
| Product Service       | 8801  | Manages products                    |
| Customer Service      | 8802  | Manages customer data               |
| Loan Service          | 8803  | Handles loans                       |
| Notification Service  | 8804  | Sends notifications                 |
| Scheduler Service     | 8805  | Schedules background jobs           |
| Report Service        | 8806  | Generates reports                   |

---

## 🚀 Getting Started

### Prerequisites

- [Docker](https://www.docker.com/)
- [Docker Compose](https://docs.docker.com/compose/)

---

### 🏁 How to Start

1. **Clone the repository**:
   ```bash
   git clone https://github.com/VictorOdhiambo/lending-app.git
   cd lending-app
   ```

2. **Ensure your directory structure looks like this**:
   ```
   .
   ├── docker-compose.yml
   ├── db/
   │   └── init.sql
   ├── discovery_service/
   ├── gateway/
   ├── product_service/
   ├── customer_service/
   ├── loan_service/
   ├── notification_service/
   ├── scheduler_service/
   └── report_service/
   ```

3. **Run Docker Compose**:
   ```bash
   docker-compose up --build
   ```

4. **Wait for services to start**. You can visit:
   - [Eureka Dashboard](http://localhost:8761)
   - [API Gateway](http://localhost:8800)

---

## 🛠️ Database Info

The services use a shared **PostgreSQL** database.

| Parameter       | Value          |
|-----------------|----------------|
| Host            | `postgres-db`  |
| Port            | `5442`         |
| Username        | `postgres`     |
| Password        | `pass@word1`   |
| Default DB Name | `lending_app_db` |

> The `init.sql` file in `db/init.sql` is automatically executed on first run to initialize schema or seed data.

---

## 🔌 Connecting to PostgreSQL

You can connect using any SQL client:

```bash
Host: localhost
Port: 5442
Username: postgres
Password: pass@word1
Database: lending_app_db
```

---

## 🧪 Testing Service Connections

Each service has its own port for internal testing and REST endpoints. Example:

```bash
curl http://localhost:8801/api/products
```

---

## 🔁 Restarting from Scratch

If you want to reset everything:

```bash
docker-compose down -v
docker-compose up --build
```

---

## 📦 Environment Variables

Each service automatically connects to PostgreSQL using these shared environment variables:

```env
DB_HOST=postgres-db
DB_PORT=5432
DB_NAME=lending_app_db
DB_USER=postgres
DB_PASSWORD=pass@word1
```
