# VetTurno - Veterinaria Huellitas

Sistema API REST para gestionar propietarios, mascotas, veterinarios y citas de la Veterinaria Huellitas.

## Tecnologías

- JDK 25
- Spring Boot 3.5.16
- Spring Security + JWT
- MySQL
- Lombok
- SpringDoc OpenAPI (Swagger)
- Maven

## Estructura

```
com.esteban.vetturno/
├── model/          -> Entidades JPA
├── repository/     -> JpaRepository interfaces
├── service/        -> Lógica de negocio
├── controller/     -> Endpoints REST
├── dto/            -> DTOs (entrada/salida)
├── security/       -> JWT, filtros, config
├── config/         -> Configuración OpenAPI
└── exception/      -> Manejo de errores
```

## Configuración MySQL

Crear usuario y base de datos:
```sql
CREATE USER 'vetturno_user'@'localhost' IDENTIFIED BY 'tu_contraseña';
CREATE DATABASE vetturno;
GRANT ALL PRIVILEGES ON vetturno.* TO 'vetturno_user'@'localhost';
FLUSH PRIVILEGES;
```

## Ejecución

```bash
mvn clean install
mvn spring-boot:run
```

Swagger: http://localhost:8080/swagger-ui.html

## Estado

✅ Proyecto inicial creado con Spring Boot 3.5.16
