# EVS La Antilla - Sistema de Gestión de Escuela de Vela

Sistema backend basado en microservicios para la gestión integral de una escuela de vela.

## 🏗️ Arquitectura

```
┌─────────────────────────────────────────────────────────────────────┐
│                         EVS La Antilla                               │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐              │
│  │  Teacher    │    │  Students   │    │   Course    │              │
│  │   API       │    │    API      │    │    API      │              │
│  │  :8082      │    │   :8081     │    │   :8083     │              │
│  └──────┬──────┘    └──────┬──────┘    └──────┬──────┘              │
│         │                  │                  │                      │
│         └──────────────────┼──────────────────┘                      │
│                            │                                         │
│                    ┌───────┴───────┐                                 │
│                    │    KAFKA      │                                 │
│                    │   :29092      │                                 │
│                    └───────────────┘                                 │
│                            │                                         │
│                    ┌───────┴───────┐                                 │
│                    │  PostgreSQL   │                                 │
│                    │    :5432      │                                 │
│                    └───────────────┘                                 │
│                                                                      │
│                    ┌───────────────┐                                 │
│                    │   Eureka      │                                 │
│                    │    :8761      │                                 │
│                    └───────────────┘                                 │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## 📦 Microservicios

### MicroserviceTeacherRegisterAPI (Puerto 8082)
- **Autenticación**: JWT, registro y login
- **CRUD de profesores**: Gestión completa
- **Especialidades**: WINDSURF, CATAMARAN, MINICATA, OPTIMIST, PADDLE_SURF, KAYAK, SUMMER_CAMP, VELA_LIGERA
- **Tipos de contrato**: FIJO (prioridad 1), TEMPORAL (prioridad 2), PRACTICAS (prioridad 3)
- **Algoritmo de asignación**: Distribución equitativa de horas respetando prioridades
- **Sistema de notificaciones**: Email para horarios y confirmaciones

### MicroserviceStudentsAPI (Puerto 8081)
- **CRUD de estudiantes**: Gestión completa
- **Niveles de habilidad**: BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
- **Inscripción a cursos**: Multi-curso por estudiante
- **Gestión de socios**: Diferenciación de clientes

### MicroserviceCourseApi (Puerto 8083)
- **CRUD de cursos**: Gestión completa
- **Tipos de curso**: WINDSURF, CATAMARAN, MINICATA, etc.
- **Organizador de horarios**: Algoritmo automático de planificación
- **Planificador de rutas**: Genera rutas de navegación basadas en:
  - Dirección y velocidad del viento
  - Nivel del estudiante
  - Tipo de embarcación
  - Duración de la clase
- **Generador de imágenes**: Dibujo visual de la ruta con viradas y bordos

### MicroserviceErekaServer (Puerto 8761)
- Descubrimiento de servicios
- Registro de microservicios

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
- Kafka + Zookeeper (puerto 29092)
- Kafka UI (puerto 8090)

### 2. Compilar el proyecto
```bash
mvn clean install -DskipTests
```

### 3. Ejecutar microservicios

```bash
# Terminal 1 - Eureka Server
cd MicroserviceErekaServer
mvn spring-boot:run

# Terminal 2 - Teacher API
cd MicroserviceTeacherRegisterAPI
mvn spring-boot:run

# Terminal 3 - Students API
cd MicroserviceStudentsAPI
mvn spring-boot:run

# Terminal 4 - Course API
cd MicroserviceCourseApi
mvn spring-boot:run
```

## 🔐 Autenticación

### Registrar un administrador
```bash
curl -X POST "http://localhost:8082/api/auth/register/admin?email=admin@evs.com&password=admin123&name=Admin"
```

### Registrar un profesor
```bash
curl -X POST http://localhost:8082/api/auth/register/teacher \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan",
    "lastName": "García",
    "dni": "12345678A",
    "phone": "666777888",
    "email": "juan@evs.com",
    "password": "password123",
    "specialities": ["WINDSURF", "CATAMARAN"],
    "contractType": "FIJO",
    "maxWeeklyHours": 40
  }'
```

### Login
```bash
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@evs.com",
    "password": "admin123"
  }'
```

## 📚 API Endpoints

### Profesores
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /api/teachers | Listar todos |
| GET | /api/teachers/{id} | Obtener por ID |
| POST | /api/teachers | Crear profesor |
| PUT | /api/teachers/{id} | Actualizar |
| DELETE | /api/teachers/{id} | Eliminar |
| GET | /api/teachers/available/{speciality} | Por especialidad |
| POST | /api/teachers/assign | Asignar automáticamente |

### Estudiantes
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /api/students | Listar todos |
| GET | /api/students/{id} | Obtener por ID |
| POST | /api/students | Crear estudiante |
| PUT | /api/students/{id} | Actualizar |
| DELETE | /api/students/{id} | Eliminar |
| POST | /api/students/{id}/enroll/{courseId} | Inscribir en curso |

### Cursos
| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | /api/courses | Listar todos |
| GET | /api/courses/{id} | Obtener por ID |
| POST | /api/courses | Crear curso |
| PUT | /api/courses/{id} | Actualizar |
| DELETE | /api/courses/{id} | Eliminar |
| POST | /api/courses/generate-route | Generar plan de ruta |
| POST | /api/courses/organize-week | Organizar horarios semanales |

## 🧭 Planificador de Rutas

El sistema genera planes de navegación inteligentes:

### Ejemplo de solicitud
```bash
curl -X POST http://localhost:8083/api/courses/generate-route \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "courseType": "WINDSURF",
    "windDirection": "SW",
    "windSpeedKnots": 12,
    "classDurationMinutes": 60,
    "studentLevel": 2
  }'
```

### Respuesta
- Lista de tramos (legs) con rumbo, duración y distancia
- Tipos de maniobra: VIRADA, TRASLUCHADA, CEÑIDA, TRAVES, LARGO, POPA
- Imagen en Base64 del recorrido
- Notas de seguridad
- Resumen del plan

## 📊 Algoritmo de Asignación de Profesores

El sistema asigna profesores siguiendo estas reglas:

1. **Prioridad por tipo de contrato**:
   - FIJO (prioridad 1)
   - TEMPORAL (prioridad 2)
   - PRACTICAS (prioridad 3)

2. **Equidad dentro del mismo nivel**:
   - Se asigna al profesor con menos horas trabajadas
   - Si hay empate, se alterna aleatoriamente

3. **Restricciones**:
   - Solo profesores con la especialidad requerida
   - Solo profesores disponibles
   - No superar horas máximas semanales

## 📧 Sistema de Notificaciones

- Envío de horarios semanales por email
- Solicitud de confirmación de disponibilidad
- Recordatorios automáticos
- Notificación de reasignaciones
- Envío de planes de ruta

## 🗄️ Base de Datos

Todas las tablas están en PostgreSQL con esquema unificado:

- `users` - Usuarios del sistema
- `teachers` - Profesores con especialidades
- `students` - Estudiantes con niveles
- `courses` - Cursos y tipos
- `schedules` - Horarios
- `route_plans` - Planes de navegación
- `route_legs` - Tramos de rutas
- `nautical_zones` - Zonas del canal náutico

## 📬 Kafka Topics

- `teacher-events` - Eventos de profesores
- `student-events` - Eventos de estudiantes
- `course-events` - Eventos de cursos
- `schedule-events` - Eventos de horarios
- `notification-events` - Eventos de notificaciones

## 🔧 Configuración

Variables de entorno importantes:

```yaml
# Base de datos
POSTGRES_HOST: localhost
POSTGRES_PORT: 5432
POSTGRES_DB: tfgdb
POSTGRES_USER: postgres
POSTGRES_PASSWORD: curso

# Kafka
KAFKA_BOOTSTRAP_SERVERS: localhost:29092

# JWT
JWT_SECRET: EVS_LaAntilla_SecretKey_2024_TFG_SailingSchool_JWT_Token_Key
JWT_EXPIRATION: 86400000

# Mail (opcional)
MAIL_USERNAME: your-email@gmail.com
MAIL_PASSWORD: your-app-password
```

## 📁 Estructura del Proyecto

```
TFG_BACK/
├── docker-compose.yml
├── pom.xml
├── MicroserviceErekaServer/
├── MicroserviceTeacherRegisterAPI/
│   └── src/main/java/.../
│       ├── Controller/
│       ├── Entities/
│       ├── Repository/
│       ├── Service/
│       ├── Security/
│       └── Kafka/
├── MicroserviceStudentsAPI/
│   └── src/main/java/.../
│       ├── Controller/
│       ├── Entities/
│       ├── Repository/
│       ├── Service/
│       └── Security/
└── MicroserviceCourseApi/
    └── src/main/java/.../
        ├── Controller/
        ├── Entities/
        ├── Repository/
        ├── Service/
        ├── Security/
        └── Kafka/
```

## 👤 Autor

Proyecto TFG - Escuela de Vela La Antilla

## 📄 Licencia

Proyecto académico - DAM

