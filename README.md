# EVS La Antilla - Sistema de Gestión de Escuela de Vela

Sistema backend basado en microservicios para la gestión integral de una escuela de vela.

## 🏗️ Arquitectura

```
┌───────────────────────────────────────────────────────────────────────────────────────┐
│                              EVS La Antilla                                            │
├───────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                        │
│  ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐ ┌──────────┐                     │
│  │ Teacher  │ │ Students │ │  Course  │ │ Account  │ │ Regatta  │                     │
│  │   API    │ │   API    │ │   API    │ │   API    │ │   API    │                     │
│  │  :8082   │ │  :8081   │ │  :8083   │ │  :8084   │ │  :8085   │                     │
│  └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘ └────┬─────┘                     │
│       │            │            │            │            │                            │
│       └────────────┴────────────┴────────────┴────────────┘                            │
│                                 │                                                      │
│                         ┌───────┴───────┐                                              │
│                         │    KAFKA      │                                              │
│                         │   :29092      │                                              │
│                         └───────────────┘                                              │
│                                 │                                                      │
│            ┌────────────────────┴────────────────────┐                                 │
│            │                                         │                                 │
│    ┌───────┴───────┐                        ┌────────┴────────┐                        │
│    │  PostgreSQL   │                        │    MongoDB      │                        │
│    │    :5432      │                        │    :27017       │                        │
│    └───────────────┘                        └─────────────────┘                        │
│                                                                                        │
│                         ┌───────────────┐                                              │
│                         │   Eureka      │                                              │
│                         │    :8761      │                                              │
│                         └───────────────┘                                              │
│                                                                                        │
└───────────────────────────────────────────────────────────────────────────────────────┘
```

## 📦 Microservicios

### MicroserviceTeacherRegisterAPI (Puerto 8082)
- **Autenticación**: JWT, registro y login
- **CRUD de profesores**: Gestión completa
- **Especialidades**: WINDSURF, CATAMARAN, MINICATA, OPTIMIST, etc.
- **Tipos de contrato**: FIJO, TEMPORAL, PRACTICAS (con prioridad)
- **Algoritmo de asignación**: Distribución equitativa respetando prioridades
- **Sistema de notificaciones**: Email para horarios y confirmaciones
- **Fotos de perfil**: Almacenamiento en MongoDB

### MicroserviceStudentsAPI (Puerto 8081)
- **CRUD de estudiantes**: Gestión completa
- **Niveles de habilidad**: BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
- **Inscripción a cursos**: Multi-curso por estudiante
- **Historial de clases**: Consulta de clases realizadas
- **Ejercicios completados**: Seguimiento de progreso
- **Predicción de viento**: Consulta de condiciones meteorológicas

### MicroserviceCourseApi (Puerto 8083)
- **CRUD de cursos**: Gestión completa
- **Planificador de rutas**: Generación de rutas según viento
- **Generador de imágenes**: Rutas visuales para profesores
- **Web Scraping meteorológico**: Datos en tiempo real
- **Generación de PDFs**: Documentos de clase
- **Sistema de alquiler**: Equipamiento y verificación de aptitud

### MicroserviceContabilityApi (Puerto 8084)
- **Gestión de pagos**: Clases, alquileres, summercamps
- **Control de sueldos**: Configuración salarial por profesor
- **Registro de horas**: Control de horas trabajadas
- **Generación de nóminas**: Cálculo automático (IRPF, SS)
- **Cuadre de caja**: Control diario efectivo/transferencia/tarjeta

### MicroserviceRegattaApi (Puerto 8085) 🆕
- **Gestión de regatas**: CRUD completo (solo BOSS/ADMIN)
- **Gestión de barcos**: Registro con rating automático
- **Inscripción de participantes**: Patrón y tripulación
- **Sistema de mangas**: Creación, inicio, finalización
- **Registro de resultados**: FINISH, DNF, DNS, DSQ, OCS
- **Clasificación automática**: Cálculo de posiciones y puntos
- **Sistema de descartes**: Aplicación de peores resultados

### MicroserviceErekaServer (Puerto 8761)
- Descubrimiento de servicios

## 🔐 Roles del Sistema

| Rol | Descripción |
|-----|-------------|
| **ADMIN** | Control total del sistema |
| **BOSS** | Regatas, nóminas, cuadre de caja |
| **TEACHER** | Gestión de clases, alquileres |
| **STUDENT** | Consulta de historial y clima |

## 🏛️ Arquitectura SOLID

Cada microservicio sigue los principios SOLID:

### Single Responsibility
- Interfaces separadas por funcionalidad
- `IRegattaService`, `IBoatService`, `IParticipantService`, `IRaceService`, etc.

### Open/Closed
- Servicios extensibles via interfaces
- Implementaciones intercambiables

### Liskov Substitution
- Interfaces bien definidas
- Cualquier implementación puede sustituir a otra

### Interface Segregation
- Interfaces pequeñas y específicas
- `IClassificationService`, `IRaceResultService`, etc.

### Dependency Inversion
- Dependencia de abstracciones (interfaces)
- Inyección de dependencias con Spring

## 🚀 Inicio Rápido

### Requisitos
- Java 17+
- Docker y Docker Compose
- Maven 3.8+

### 1. Levantar infraestructura
```bash
docker-compose up -d
```

Esto levanta:
- PostgreSQL (puerto 5432)
- MongoDB (puerto 27017)
- Kafka + Zookeeper (puerto 29092)
- Kafka UI (puerto 8090)
- Mongo Express (puerto 8091)

### 2. Compilar el proyecto
```bash
mvn clean install -DskipTests
```

### 3. Iniciar microservicios (en orden)
```bash
# 1. Eureka Server
cd MicroserviceErekaServer && mvn spring-boot:run

# 2. Los demás servicios (en terminales separadas)
cd MicroserviceTeacherRegisterAPI && mvn spring-boot:run
cd MicroserviceStudentsAPI && mvn spring-boot:run
cd MicroserviceCourseApi && mvn spring-boot:run
cd MicroserviceContabilityApi && mvn spring-boot:run
```

## 📋 Endpoints Principales

### Autenticación
- `POST /api/auth/register` - Registro de usuario
- `POST /api/auth/login` - Login y obtención de JWT

### Profesores
- `GET/POST/PUT/DELETE /api/teachers` - CRUD
- `POST /api/teachers/{id}/photo` - Subir foto de perfil
- `GET /api/teachers/{id}/photo` - Obtener foto

### Alumnos
- `GET/POST/PUT/DELETE /api/students` - CRUD
- `GET /api/students/{id}/classes` - Historial de clases
- `GET /api/students/{id}/exercises` - Ejercicios completados
- `GET /api/students/weather-prediction` - Predicción del viento

### Cursos
- `GET/POST/PUT/DELETE /api/courses` - CRUD
- `GET /api/weather/current` - Datos meteorológicos actuales
- `POST /api/class-documents/generate` - Generar PDF de clase

### Alquileres
- `GET/POST /api/rentals/equipment` - Gestión de equipamiento
- `POST /api/rentals` - Crear alquiler
- `POST /api/rentals/{id}/complete` - Completar alquiler

### Regatas
- `GET/POST /api/regattas` - Gestión de regatas (BOSS)
- `POST /api/regattas/{id}/participants` - Inscribirse
- `GET /api/regattas/{id}/classification` - Clasificación

### Contabilidad
- `POST /api/payments` - Registrar pago
- `POST /api/worked-hours` - Registrar horas trabajadas
- `POST /api/payrolls/generate` - Generar nómina
- `POST /api/cash-register/close` - Cerrar caja del día
- `GET /api/cash-register/discrepancy-report/{date}` - Informe de descuadre

## 📡 Comunicación Kafka

Los microservicios se comunican mediante eventos Kafka:

| Topic | Descripción |
|-------|-------------|
| `schedule-events` | Horarios y asignaciones |
| `payment-events` | Pagos registrados |
| `worked-hours-events` | Horas trabajadas |
| `rental-events` | Alquileres |
| `regatta-events` | Eventos de regatas |
| `class-completed-events` | Clases completadas |

## 🗄️ Bases de Datos

### PostgreSQL (Datos relacionales)
- Profesores, Alumnos, Cursos
- Pagos, Nóminas, Horas trabajadas
- Equipamiento, Alquileres
- Regatas, Participantes, Resultados

### MongoDB (Documentos)
- Fotos de perfil de profesores
- PDFs de clases generados
- Datos meteorológicos históricos
- Documentos de clase

## 👨‍💻 Desarrollo

Creado para EVS La Antilla - Escuela de Vela
