# EVS La Antilla - Sistema de Gestión de Escuela de Vela

Sistema backend basado en microservicios para la gestión integral de una escuela de vela.

## 🏗️ Arquitectura

```
┌──────────────────────────────────────────────────────────────────────────────┐
│                           EVS La Antilla                                      │
├──────────────────────────────────────────────────────────────────────────────┤
│                                                                               │
│  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐  ┌─────────────┐          │
│  │  Teacher    │  │  Students   │  │   Course    │  │ Contability │          │
│  │    API      │  │    API      │  │    API      │  │    API      │          │
│  │   :8082     │  │   :8081     │  │   :8083     │  │   :8084     │          │
│  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘  └──────┬──────┘          │
│         │                │                │                │                  │
│         └────────────────┴────────────────┴────────────────┘                  │
│                                   │                                           │
│                           ┌───────┴───────┐                                   │
│                           │    KAFKA      │                                   │
│                           │   :29092      │                                   │
│                           └───────────────┘                                   │
│                                   │                                           │
│              ┌────────────────────┴────────────────────┐                      │
│              │                                         │                      │
│      ┌───────┴───────┐                        ┌────────┴────────┐             │
│      │  PostgreSQL   │                        │    MongoDB      │             │
│      │    :5432      │                        │    :27017       │             │
│      │ (Datos CRUD)  │                        │ (PDFs, Fotos)   │             │
│      └───────────────┘                        └─────────────────┘             │
│                                                                               │
│                           ┌───────────────┐                                   │
│                           │   Eureka      │                                   │
│                           │    :8761      │                                   │
│                           └───────────────┘                                   │
│                                                                               │
└──────────────────────────────────────────────────────────────────────────────┘
```

## 📦 Microservicios

### MicroserviceTeacherRegisterAPI (Puerto 8082)
- **Autenticación**: JWT, registro y login
- **CRUD de profesores**: Gestión completa
- **Especialidades**: WINDSURF, CATAMARAN, MINICATA, OPTIMIST, PADDLE_SURF, KAYAK, SUMMER_CAMP, VELA_LIGERA
- **Tipos de contrato**: FIJO (prioridad 1), TEMPORAL (prioridad 2), PRACTICAS (prioridad 3)
- **Algoritmo de asignación**: Distribución equitativa de horas respetando prioridades
- **Sistema de notificaciones**: Email para horarios y confirmaciones
- **Fotos de perfil**: Almacenamiento en MongoDB

### MicroserviceStudentsAPI (Puerto 8081)
- **CRUD de estudiantes**: Gestión completa
- **Niveles de habilidad**: BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
- **Inscripción a cursos**: Multi-curso por estudiante
- **Historial de clases**: Consulta de clases realizadas
- **Ejercicios completados**: Seguimiento de progreso
- **Predicción de viento**: Consulta de condiciones meteorológicas
- **Gestión de socios**: Diferenciación de clientes

### MicroserviceCourseApi (Puerto 8083)
- **CRUD de cursos**: Gestión completa
- **Planificador de rutas**: Generación de rutas de navegación según viento
- **Generador de imágenes**: Rutas visuales para profesores
- **Web Scraping meteorológico**: Datos de https://www.escuela-vela.com/meteo/
- **Generación de PDFs**: Documentos de clase con maniobras y bordos
- **Sistema de alquiler**: CRUD de equipamiento y alquileres
- **Verificación de aptitud**: Control de quien puede alquilar material
- **Sistema de regatas**: Inscripciones, mangas, resultados y clasificación
- **Rating de barcos**: Cálculo automático de handicap

### MicroserviceContabilityApi (Puerto 8084)
- **Gestión de pagos**: Clases, alquileres, summercamps, regatas
- **Control de sueldos**: Configuración salarial por profesor
- **Registro de horas**: Control de horas trabajadas con validación
- **Generación de nóminas**: Cálculo automático con deducciones (IRPF, SS)
- **Cuadre de caja**: Control diario de efectivo/transferencias/tarjeta/bizum
- **Detección de descuadres**: Análisis y desglose de discrepancias

### MicroserviceErekaServer (Puerto 8761)
- Descubrimiento de servicios
- Registro de microservicios

## 🔐 Roles del Sistema

| Rol | Descripción |
|-----|-------------|
| **ADMIN** | Administrador del sistema - Control total |
| **BOSS** | Dueños de la escuela - Eventos, regatas, nóminas, cuadre de caja |
| **TEACHER** | Profesores - Gestión de clases, alquileres |
| **STUDENT** | Alumnos - Consulta de historial y predicción |

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
