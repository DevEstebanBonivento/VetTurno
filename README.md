# VetTurno

API REST para la gestión de citas de **Veterinaria Huellitas**.

## Historia

Veterinaria Huellitas es una clínica que atiende con el doctor Andrés y Paula en recepción. Antes
de VetTurno, la agenda vivía entre un cuaderno y mensajes de WhatsApp, lo que generaba citas
duplicadas y datos de contacto perdidos. VetTurno centraliza esa información en una API persistente,
permitiendo registrar responsables, mascotas y veterinarios, y agendar citas sin cruces de horario.

## Alcance

Incluye: registro y login con roles USER/ADMIN, gestión de propietarios, mascotas, veterinarios y
citas, prevención de horarios duplicados, validación de datos, manejo global de errores, Swagger/OpenAPI,
persistencia en MySQL.

No incluye: historia clínica, pagos/facturación, notificaciones por correo/WhatsApp, interfaz web o
móvil, despliegue en la nube.

## Tecnologías

- Java 25
- Spring Boot 3.5.16
- Spring Data JPA / Hibernate
- MySQL 8
- Spring Security + JWT (jjwt 0.12.7)
- Bean Validation
- Swagger / OpenAPI (springdoc 2.8.14)
- Maven

## Estructura del proyecto

```
com.DEVSenior.vetturno/
├── model/          -> Entidades JPA (Propietario, Mascota, Veterinario, Cita, Usuario)
├── repository/     -> Interfaces JpaRepository
├── service/        -> Lógica de negocio
├── controller/     -> Endpoints REST
├── dto/            -> DTOs de entrada y salida
├── security/       -> JWT, filtro de autenticación
├── config/         -> Configuración de Swagger/OpenAPI y seguridad
└── exception/      -> Excepciones propias y manejo global de errores
```

## Modelo de datos

| Entidad | Campos | Relación |
|---|---|---|
| Propietario | id, nombre, teléfono, email | — |
| Mascota | id, nombre, especie, raza, propietario | pertenece a un Propietario |
| Veterinario | id, nombre, especialidad | — |
| Cita | id, fechaHora, motivo, mascota, veterinario | pertenece a una Mascota y un Veterinario |
| Usuario | id, email, password (BCrypt), rol | rol: USER o ADMIN |

## Configuración de la base de datos

1. Crear la base en MySQL Workbench:
   ```sql
   CREATE DATABASE IF NOT EXISTS vetturno;
   ```
2. Las credenciales se leen desde variables de entorno (nunca quedan escritas en este repositorio):
   ```properties
   spring.datasource.url=${DB_URL}
   spring.datasource.username=${DB_USER}
   spring.datasource.password=${DB_PASSWORD}
   ```
3. Antes de ejecutar, define las variables de entorno correspondientes (ej. en la configuración de
   ejecución de tu IDE, o en la terminal antes de correr el proyecto):
   ```
   DB_URL=jdbc:mysql://localhost:3306/vetturno
   DB_USER=vetturno_user
   DB_PASSWORD=<tu_contraseña>
   JWT_SECRET=<una cadena larga y aleatoria, no memorable>
   ```
   `JWT_SECRET` es la clave con la que se firman los tokens JWT — sin ella, la aplicación no
   arranca (no tiene un valor por defecto hardcodeado, a propósito).

## Cómo ejecutar

```bash
# Windows
.\mvnw.cmd spring-boot:run

# macOS/Linux
./mvnw spring-boot:run
```

La API queda disponible en `http://localhost:8080`.

Swagger UI: `http://localhost:8080/swagger-ui/index.html`

## Endpoints

| Método | Ruta | Acceso | Descripción |
|---|---|---|---|
| POST | `/api/auth/register` | Público | Registra un usuario (siempre como USER) y devuelve un token |
| POST | `/api/auth/login` | Público | Verifica credenciales y devuelve un JWT |
| POST | `/api/propietarios` | USER/ADMIN | Crea un responsable |
| GET | `/api/propietarios` | USER/ADMIN | Lista responsables |
| POST | `/api/mascotas` | USER/ADMIN | Crea una mascota (requiere propietario existente) |
| GET | `/api/mascotas` | USER/ADMIN | Lista mascotas |
| POST | `/api/veterinarios` | **Solo ADMIN** | Crea un veterinario |
| GET | `/api/veterinarios` | USER/ADMIN | Lista veterinarios |
| POST | `/api/citas` | USER/ADMIN | Agenda una cita (valida fecha futura y evita cruce de horario) |
| GET | `/api/citas` | USER/ADMIN | Devuelve la agenda completa |
| GET | `/api/citas/veterinario/{id}` | USER/ADMIN | Filtra citas por veterinario |

## Roles y seguridad

- **USER**: registra propietarios, mascotas y citas; consulta la agenda.
- **ADMIN**: todo lo anterior + registrar veterinarios.
- El registro siempre asigna el rol USER, sin importar lo que envíe el cliente.
- Las contraseñas se almacenan con BCrypt; la sesión es stateless (JWT en cada petición vía
  `Authorization: Bearer <token>`).

### Cómo habilitar el primer ADMIN

Por seguridad, ningún usuario puede auto-asignarse ADMIN desde la API. El proceso es:

1. Registrar el usuario normalmente por `/api/auth/register` (queda como USER).
2. Ejecutar en MySQL Workbench:
   ```sql
   UPDATE usuario SET rol = 'ADMIN' WHERE email = 'admin@huellitas.com';
   ```
3. Volver a iniciar sesión por `/api/auth/login` — el nuevo token ya incluye el rol ADMIN.

## Cómo probar

1. Abrir Swagger (`http://localhost:8080/swagger-ui/index.html`).
2. Registrar un usuario desde `POST /api/auth/register`, o hacer login si ya existe.
3. Copiar el `token` de la respuesta.
4. Click en el botón **Authorize** (arriba a la derecha) y pegar el token.
5. Probar cualquier endpoint protegido con **Try it out → Execute**.

También se puede probar con Postman, agregando el token en la pestaña **Authorization → Bearer Token**
de cada petición.

## Matriz de pruebas manuales

| # | Escenario | Resultado esperado | Resultado obtenido |
|---|---|---|---|
| 1 | App inicia con MySQL disponible | Servidor activo | ✅ |
| 2 | Registro válido | 200/201 + token, password hasheada | ✅ |
| 3 | Registro con email inválido y clave corta | 400 con errores por campo | ✅ |
| 4 | Login con credenciales válidas | 200 + JWT | ✅ |
| 5 | GET /api/citas sin token | Acceso rechazado | ✅ (403) |
| 6 | POST /api/veterinarios con USER | 403 Forbidden | ✅ |
| 7 | POST /api/veterinarios con ADMIN | 201 y veterinario persistido | ✅ |
| 8 | Creación válida de propietario | 201 y DTO sin colecciones anidadas | ✅ |
| 9 | Creación de mascota con propietario existente | 201 y relación correcta | ✅ |
| 10 | Mascota con propietario inexistente | 400 controlado; no se inserta fila | ✅ |
| 11 | Cita futura con referencias válidas | 201 y cita persistida | ✅ |
| 12 | Cita con fecha pasada | 400 con mensaje claro | ✅ |
| 13 | Segundo intento con mismo veterinario y horario | 400; se conserva una sola cita | ✅ |
| 14 | Filtro de citas por veterinario | 200 y solo coincidencias | ✅ |
| 15 | Reinicio y prueba desde Swagger con Authorize | Datos persisten y flujo protegido funciona | ✅ |

## Nota sobre uso de IA

Se usó IA en distintos momentos y de distintas formas durante el desarrollo:

- Se elaboró primero un documento de especificación técnica (spec) con la ayuda de IA, definiendo
  la arquitectura, el modelo de datos, los endpoints y las reglas de negocio antes de escribir código.
- A partir de ese spec, el agente de GitHub Copilot generó gran parte del código de las Partes 2, 3, 4
  y 5 (entidades, repositorios, servicios, controllers, seguridad JWT y roles).
- Ese código generado fue revisado después con apoyo de IA para entender el porqué de cada decisión
  (relaciones JPA, DTO, filtro JWT, reglas de citas), y se corrigieron varios problemas detectados en
  esa revisión: credenciales hardcodeadas en `application.properties`, `ddl-auto=create-drop` (que
  borraba los datos en cada reinicio), inyección por campo en vez de por constructor, y un código de
  respuesta HTTP incorrecto en el manejo de errores.
- La Parte 6 (validaciones con Bean Validation y ampliación del manejo global de errores) se escribió
  a mano, con IA explicando cada anotación y su propósito antes de incorporarla.
- Las 15 pruebas de la matriz se ejecutaron manualmente contra la API en ejecución (Postman y Swagger),
  y sus resultados en este README corresponden a esas ejecuciones reales.