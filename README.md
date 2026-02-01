# Medico - Hospital Management System

A comprehensive hospital management system built with modern technologies.

## Tech Stack

- **Frontend**: React 18 + TypeScript + Vite (PWA)
- **Backend**: Java 17 + Spring Boot 3.x
- **Database**: PostgreSQL 15
- **IAM**: Keycloak
- **Deployment**: Docker + Docker Compose

## Features

### Patient Management
- Patient registration and demographics
- Appointment scheduling
- Electronic Health Records (EHR)
- Prescription management

### Pharmacy/Inventory Management
- Medication catalog
- Stock tracking and management
- Prescription dispensing
- Inventory reports and alerts

### Laboratory Management
- Lab test ordering
- Results entry and management
- Test status tracking

### Billing & Finance
- Invoice generation
- Payment processing
- Financial reports and analytics

### Administration
- User management (via Keycloak)
- Role-based access control
- Dashboard with key metrics

### Feature Matrix

| Feature | Status | Notes |
| --- | --- | --- |
| Patient registration & demographics | ✅ | Patients CRUD, metadata, schema/indices, backend services & DTOs fully wired. |
| Appointment scheduling | ✅ | Controllers/services/repositories plus `AppointmentsPage` exist. |
| EHR (medical records) | ✅ | Medical record table, service, and frontend patient notes. |
| Prescription workflow | ⚠️ Partial | Backend handles creation, numbering, statuses, dispensing; frontend lacks creation UI yet. |
| Medication catalog | ✅ | Medications table/DTOs/services and frontend catalog showing `requiresPrescription`. |
| Inventory & stock management | ✅ | Inventory table, repositories, and `InventoryPage`. |
| Prescription dispensing | ✅ | Dispensing service enforces rules and updates prescription status. |
| Inventory reports & alerts | ⚠️ Partial | Schema supports statuses/alerts but no dedicated report UI yet. |
| Lab test ordering & results | ✅ | Lab controllers/services/orders/results + `LaboratoryPage`. |
| Invoicing & payments | ✅ | Invoice/payment entities, controllers, reporting service. |
| Administration & RBAC | ✅ | Keycloak integration, `SecurityConfig`, audit logs, dashboard stats. |

## Getting Started

### Prerequisites

- Docker and Docker Compose
- Node.js 20+ (for frontend development)
- Java 17+ (for backend development)
- Maven 3.9+ (for backend development)

### Running with Docker Compose

1. Clone the repository:
```bash
git clone <repository-url>
cd medico
```

2. Start all services:
```bash
cd docker
docker-compose up -d
```

3. Access the applications:
   - Frontend: http://localhost
   - Backend API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Keycloak: http://localhost:8180

### Default Users

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | Administrator |
| doctor | doctor123 | Doctor |
| pharmacist | pharmacist123 | Pharmacist |
| lab_tech | labtech123 | Lab Technician |
| billing | billing123 | Billing Staff |
| receptionist | receptionist123 | Receptionist |

### Development Setup

#### Backend

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

#### Frontend

```bash
cd frontend
npm install
npm run dev
```

## Project Structure

```
medico/
├── frontend/                 # React + TypeScript + Vite
│   ├── src/
│   │   ├── components/      # Reusable UI components
│   │   ├── contexts/        # React contexts
│   │   ├── lib/             # Utilities and API client
│   │   ├── pages/           # Page components
│   │   └── App.tsx
│   └── ...
├── backend/                  # Spring Boot
│   ├── src/main/java/com/medico/
│   │   ├── patient/         # Patient module
│   │   ├── pharmacy/        # Pharmacy module
│   │   ├── laboratory/      # Laboratory module
│   │   ├── billing/         # Billing module
│   │   ├── admin/           # Administration module
│   │   ├── common/          # Shared utilities
│   │   └── security/        # Security config
│   └── ...
├── docker/                   # Docker configurations
│   ├── docker-compose.yml
│   ├── Dockerfile.backend
│   ├── Dockerfile.frontend
│   └── keycloak/
└── README.md
```

## API Documentation

Once the backend is running, access the Swagger UI at:
http://localhost:8080/swagger-ui.html

## Security

- JWT-based authentication via Keycloak
- Role-based access control (RBAC)
- HTTPS in production
- CORS configuration
- Input validation

## License

MIT License

