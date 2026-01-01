# EVS Backend - Microservicios

## Requisitos Previos
- JDK 17 o superior (configurado en `C:\Users\jjace\.jdks\ms-21.0.9`)
- Docker Desktop (para PostgreSQL, MongoDB, Kafka)
- Maven (incluido en cada microservicio con wrapper mvnw)

## Servicios de Infraestructura (Docker)

### Iniciar Docker Compose
```bash
docker-compose up -d
```

Esto iniciará:
- **PostgreSQL** (puerto 5432)
  - Database: tfgdb
  - Usuario: postgres
  - Contraseña: curso

- **MongoDB** (puerto 27017)
  - Database: evs_documents
  - Usuario: evs_admin
  - Contraseña: evs_password

- **Kafka** (puertos 9092, 29092)
- **Zookeeper** (puerto 2181)
- **Kafka UI** (puerto 8090) - http://localhost:8090
- **Mongo Express** (puerto 8091) - http://localhost:8091

### Detener Docker Compose
```bash
docker-compose down
```

## Microservicios

### 1. Compilar Todos los Microservicios
```bash
.\compile-all.bat
```

### 2. Iniciar Todos los Microservicios
```bash
.\start-all.bat
```

Esto iniciará los servicios en el siguiente orden:
1. **Eureka Server** (puerto 8761) - Registro y descubrimiento de servicios
2. **Contability API** (puerto 8084) - Gestión de pagos y contabilidad
3. **Regatta API** (puerto 8085) - Gestión de regatas
4. **Course API** (puerto 8083) - Gestión de cursos y horarios
5. **Teacher API** (puerto 8082) - Gestión de profesores
6. **Students API** (puerto 8081) - Gestión de estudiantes

### Compilar Microservicios Individualmente

#### Eureka Server
```bash
cd MicroserviceErekaServer
.\mvnw.cmd clean package -DskipTests
java -jar target\MicroserviceErekaServer-0.0.1-SNAPSHOT.jar
```

#### Students API
```bash
cd MicroserviceStudentsAPI
.\mvnw.cmd clean package -DskipTests
java -jar target\MicroserviceStudentsAPI-0.0.1-SNAPSHOT.jar
```

#### Teacher API
```bash
cd MicroserviceTeacherRegisterAPI
.\mvnw.cmd clean package -DskipTests
java -jar target\MicroserviceTeacherRegisterAPI-0.0.1-SNAPSHOT.jar
```

#### Course API
```bash
cd MicroserviceCourseApi
.\mvnw.cmd clean package -DskipTests
java -jar target\MicroserviceCourseApi-0.0.1-SNAPSHOT.jar
```

#### Contability API
```bash
cd MicroserviceContabilityApi
.\mvnw.cmd clean package -DskipTests
java -jar target\MicroserviceContabilityApi-0.0.1-SNAPSHOT.jar
```

#### Regatta API
```bash
cd MicroserviceRegattaApi
.\mvnw.cmd clean package -DskipTests
java -jar target\MicroserviceRegattaApi-0.0.1-SNAPSHOT.jar
```

## URLs de los Servicios

### Microservicios
- Eureka Dashboard: http://localhost:8761
- Students API: http://localhost:8081/api/students
- Teacher API: http://localhost:8082/api/teachers
- Course API: http://localhost:8083/api/courses
- Contability API: http://localhost:8084/api/contability
- Regatta API: http://localhost:8085/api/regattas

### Herramientas de Monitoreo
- Kafka UI: http://localhost:8090
- Mongo Express: http://localhost:8091

## Configuración JWT
Todos los servicios usan la misma clave secreta JWT para autenticación:
```
RVZTX0xhQW50aWxsYV9TZWNyZXRLZXlfMjAyNF9URkdfU2FpbGluZ1NjaG9vbF9KV1RfVG9rZW5fS2V5
```

## Orden de Inicio Recomendado
1. **Docker Compose** (PostgreSQL, MongoDB, Kafka)
2. **Eureka Server** (esperar ~20 segundos)
3. **Microservicios** (pueden iniciarse en paralelo)

## Troubleshooting

### Error: "No compiler is provided in this environment"
Asegúrate de que JAVA_HOME apunte al JDK:
```bash
set JAVA_HOME=C:\Users\jjace\.jdks\ms-21.0.9
set PATH=%JAVA_HOME%\bin;%PATH%
```

### Error de conexión a base de datos
Verifica que Docker Compose esté corriendo:
```bash
docker ps
```

### Puerto ya en uso
Verifica qué proceso está usando el puerto:
```bash
netstat -ano | findstr :8761
```

## Arquitectura

```
┌─────────────────┐
│  Eureka Server  │
│   (Port 8761)   │
└────────┬────────┘
         │
    ┌────┴────────────────────────┐
    │                             │
┌───▼──────┐  ┌──────────┐  ┌────▼─────┐
│ Students │  │ Teachers │  │  Course  │
│   8081   │  │   8082   │  │   8083   │
└──────────┘  └──────────┘  └──────────┘
    │             │              │
    │         ┌───▼───┐      ┌───▼──────┐
    │         │Kafka  │      │ Regatta  │
    │         │ 29092 │      │   8085   │
    │         └───┬───┘      └──────────┘
    │             │
┌───▼─────────────▼───┐  ┌──────────────┐
│    PostgreSQL       │  │   MongoDB    │
│      5432           │  │    27017     │
└─────────────────────┘  └──────────────┘
```

## Tecnologías Utilizadas
- **Spring Boot 3.2.4**
- **Spring Cloud 2023.0.3**
- **Eureka** - Service Discovery
- **Kafka** - Mensajería asíncrona
- **PostgreSQL** - Base de datos relacional
- **MongoDB** - Almacenamiento de documentos
- **JWT** - Autenticación
- **OpenFeign** - Comunicación entre microservicios

