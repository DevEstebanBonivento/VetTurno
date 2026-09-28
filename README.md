# VetTurno

API REST para la gestión de citas de **Veterinaria Huellitas**.

**Repositorio:** URL_DEL_REPOSITORIO_AQUI

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
   UPDATE usuario SET rol = 'ADMIN' WHERE email = 'marta@huellitas.com';
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
| 2 | Registro válido | 200/201 + token, password hasheada | ✅ (201) |
| 3 | Registro con email inválido y clave corta | 400 con errores por campo | ✅ (400) |
| 4 | Login con credenciales válidas | 200 + JWT | ✅ |
| 5 | GET /api/citas sin token | Acceso rechazado | ✅ (403) |
| 6 | POST /api/veterinarios con USER | 403 Forbidden | ✅ (403) |
| 7 | POST /api/veterinarios con ADMIN | 201 y veterinario persistido | ✅ (201) |
| 8 | Creación válida de propietario | 201 y DTO sin colecciones anidadas | ✅ (201) |
| 9 | Creación de mascota con propietario existente | 201 y relación correcta | ✅ (201) |
| 10 | Mascota con propietario inexistente | 400 controlado; no se inserta fila | ✅ (400) |
| 11 | Cita futura con referencias válidas | 201 y cita persistida | ✅ (201) |
| 12 | Cita con fecha pasada | 400 con mensaje claro | ✅ (400) |
| 13 | Segundo intento con mismo veterinario y horario | 400; se conserva una sola cita | ✅ (400) |
| 14 | Filtro de citas por veterinario | 200 y solo coincidencias | ✅ (200) |
| 15 | Reinicio y prueba desde Swagger con Authorize | Datos persisten y flujo protegido funciona | ✅ (200) |

---

## Evidencias por parte

<!-- Reemplaza cada ruta de imagen por la ruta real de tu captura. La línea "Descripción" acompaña
     cada captura (accesibilidad): ajústala si tu captura muestra algo distinto. -->

### Parte 1 — Preparar el proyecto

**Captura del servidor iniciando y árbol de paquetes**

![Consola de IntelliJ con el mensaje Started VetTurnoApplication y el árbol de paquetes del proyecto](evidencias/Parte1/servidor-iniciado.png)

*Descripción:* consola mostrando que Spring Boot inició correctamente y, junto a ella, los paquetes
model, repository, service, controller, dto, security, config y exception.

**Enlace al commit inicial:** https://github.com/DevEstebanBonivento/VetTurno/commit/5e675d47a7e45f6cd4352a17961e2ca7bedbf193

**Diferencia entre `pom.xml` y la clase principal**

El `pom.xml` es el archivo de configuración de Maven: dice cómo se llama el proyecto (groupId,
artifactId, versión), qué versión de Java usa, qué versión de Spring Boot hereda y qué dependencias
necesita (JPA, Security, MySQL, JWT, Swagger…), además de cómo se construye. Es la lista de lo que el
proyecto necesita para existir y compilar.

La clase principal (`VetTurnoApplication`) es el punto de entrada: tiene `@SpringBootApplication` y el
método `main`. Al ejecutarla, Spring Boot levanta el servidor embebido, aplica la autoconfiguración y
busca los componentes (controllers, services, repositories) dentro de su paquete y subpaquetes. En
resumen: el `pom.xml` define *con qué* se construye el proyecto; la clase principal es *lo que lo
enciende*.

---

### Parte 2 — Modelar la información de Huellitas

**Diagrama del modelo**

```mermaid
erDiagram
    PROPIETARIO ||--o{ MASCOTA : "tiene"
    MASCOTA ||--o{ CITA : "recibe"
    VETERINARIO ||--o{ CITA : "atiende"
    PROPIETARIO {
        Long id
        String nombre
        String telefono
        String email
    }
    MASCOTA {
        Long id
        String nombre
        String especie
        String raza
        Long propietario_id FK
    }
    VETERINARIO {
        Long id
        String nombre
        String especialidad
    }
    CITA {
        Long id
        LocalDateTime fechaHora
        String motivo
        Long mascota_id FK
        Long veterinario_id FK
    }
    USUARIO {
        Long id
        String email
        String password
        String rol
    }
```

**Captura del esquema y tablas en Workbench**

![MySQL Workbench mostrando las tablas propietario, mascota, veterinario, cita y usuario del esquema vetturno](evidencias/Parte2/esquema-workbench.png)

*Descripción:* panel de esquemas de Workbench con las cinco tablas creadas por Hibernate, incluyendo
las llaves foráneas en `mascota` y `cita`.

![Consola de la aplicación con las sentencias create table ejecutadas por Hibernate](evidencias/Parte2/sql-hibernate.png)

*Descripción:* MySQL Workbench mostrando el resultado de una consulta a `information_schema` con las
tres llaves foráneas del esquema `vetturno`: `cita.mascota_id`, `cita.veterinario_id` y
`mascota.propietario_id`, cada una apuntando a su tabla referenciada.

**Decisión sobre la navegación entre entidades**

Las relaciones son unidireccionales: `Mascota` conoce a su `Propietario`, y `Cita` conoce a su
`Mascota` y a su `Veterinario`, pero no al revés. Las llaves foráneas quedan en el lado "muchos"
(`mascota.propietario_id`, `cita.mascota_id`, `cita.veterinario_id`), porque es la tabla con más
registros la que guarda la referencia. No se navega de `Propietario` a sus mascotas porque el contrato
no lo necesita y evita la recursión JSON; si hiciera falta, se resuelve con una consulta en el
repositorio.

**Diferencia entre JPA, Hibernate y JpaRepository**

- **JPA** es la especificación (el estándar): define cómo se mapean objetos Java a tablas mediante
  anotaciones como `@Entity`, `@ManyToOne` o `@Id`. Por sí sola no hace nada.
- **Hibernate** es la implementación de JPA: es quien realmente genera el SQL, habla con MySQL y crea
  las tablas.
- **JpaRepository** es una interfaz de Spring Data que nos da operaciones listas (`save`, `findAll`,
  `findById`, `deleteById`) y consultas derivadas por nombre, sin escribir SQL.

---

### Parte 3 — Flujo REST por capas

**Creación y consulta de propietario, mascota y veterinario**

![Postman con POST /api/propietarios respondiendo 201 y el propietario creado](evidencias/Parte3/propietario-post.png)

*Descripción:* petición de creación de propietario con respuesta 201 y el DTO con id, nombre, teléfono y email.

![Postman con POST /api/mascotas respondiendo 201 con propietarioId y propietarioNombre](evidencias/Parte3/mascota-post.png)

*Descripción:* mascota creada con respuesta plana que incluye `propietarioId` y `propietarioNombre`, sin anidar al propietario.

![Postman con POST /api/veterinarios respondiendo 201 con token de ADMIN](evidencias/Parte3/veterinario-post.png)

*Descripción:* veterinario creado con éxito usando el token de un usuario ADMIN.

![Postman con los GET de propietario](evidencias/Parte3/consultasPropietario-get.png)
![Postman con los GET de mascota](evidencias/Parte3/consultasMascota-get.png)
![Postman con los GET de veterinario](evidencias/Parte3/consultasVeterinario-get.png)

*Descripción:* consultas GET de las tres entidades devolviendo sus listas mediante DTO.

**Datos antes y después de reiniciar el servidor**

![Postman con los GET de propietario](evidencias/Parte3/consultasPropietario-get.png)
![Postman con los GET de mascota](evidencias/Parte3/consultasMascota-get.png)
![Postman con los GET de veterinario](evidencias/Parte3/consultasVeterinario-get.png)

![Postman con los GET de propietario](evidencias/Parte3/consultasPropietarioReinicio-get.png)
![Postman con los GET de mascota](evidencias/Parte3/consultasMascotaReinicio-get.png)
![Postman con los GET de veterinario](evidencias/Parte3/consultasVeterinarioReinicio-get.png)

*Descripción:* la misma consulta devuelve los mismos registros antes y después del reinicio, lo que
confirma la persistencia en MySQL (`ddl-auto=update`).

**Qué evita `MascotaDTO` frente a devolver `Mascota` directamente**

Evita exponer la estructura interna de la base de datos y, sobre todo, la recursión JSON: si se
devolviera la entidad con su `Propietario` completo, y este tuviera referencia de vuelta, Jackson
entraría en un ciclo infinito al convertir a JSON. El DTO devuelve solo lo necesario (`propietarioId`
y `propietarioNombre`) en una respuesta plana.

---

### Parte 4 — Agendar citas sin cruces

**Cita creada con 201**

![Postman con POST /api/citas respondiendo 201](evidencias/Parte4/cita-creada.png)

*Descripción:* cita futura creada con mascota y veterinario existentes, respuesta 201 con datos planos.

**Fecha pasada y horario duplicado**

![Postman con POST /api/citas con fecha pasada respondiendo 400](evidencias/Parte4/cita-fecha-pasada.png)

*Descripción:* intento de agendar una cita con fecha en el pasado, rechazado con 400.

![Postman con POST /api/citas repetida para el mismo veterinario y hora respondiendo 400](evidencias/Parte4/cita-duplicada.png)

*Descripción:* segundo intento con el mismo veterinario y la misma fecha y hora, rechazado con 400.

**Agenda filtrada por veterinario**

![Postman con GET /api/citas/veterinario/1 devolviendo solo las citas de ese veterinario](evidencias/Parte4/agenda-por-veterinario.png)

*Descripción:* consulta filtrada que devuelve únicamente las citas del veterinario indicado.

---

### Parte 5 — Seguridad con JWT y roles

**Hash de la contraseña en MySQL**

![Workbench con SELECT email, password FROM usuario mostrando el hash BCrypt](evidencias/Parte5/hash-mysql.png)

*Descripción:* la columna `password` contiene un hash BCrypt ilegible; la contraseña original no se
almacena en ningún lado.

**Login con token parcialmente oculto**

![Respuesta del login con el token recortado u ocultado en su mayor parte](evidencias/Parte5/login-token.png)

*Descripción:* respuesta 200 del login con el campo `token` parcialmente oculto.

**POST /api/veterinarios: USER → 403, ADMIN → 201**

![Postman con POST /api/veterinarios usando token de USER respondiendo 403](evidencias/Parte5/veterinario-user-403.png)

![Postman con POST /api/veterinarios usando token de ADMIN respondiendo 201](evidencias/Parte5/veterinario-admin-201.png)

*Descripción:* la misma petición es rechazada con 403 para un USER y aceptada con 201 para un ADMIN.

**Autenticación vs. autorización (caso del 403)**

*Autenticar* es comprobar quién eres: el login verifica email y contraseña y entrega un JWT.
*Autorizar* es decidir qué puedes hacer con esa identidad. Paula está autenticada (su token es válido),
pero su rol es USER, y crear veterinarios exige ADMIN; por eso recibe 403: el sistema sabe quién es,
pero no le da permiso.

---

### Parte 6 — Validaciones y errores

**400 con varios errores de campos**

![Postman con registro inválido respondiendo 400 con errores por campo para email y password](evidencias/Parte6/validacion-campos.png)

*Descripción:* registro con email inválido y contraseña corta; la respuesta 400 indica el mensaje de
cada campo que falló.

**400 por cruce de horario**

![Postman con cita duplicada respondiendo 400 con mensaje de horario ocupado](evidencias/Parte6/cruce-horario.png)

*Descripción:* error de regla de negocio por horario ocupado, con el mismo formato de respuesta de error.

**Por qué el manejador global no reemplaza las reglas de seguridad**

El `GlobalExceptionHandler` solo traduce a respuestas HTTP las excepciones que ocurren *dentro* de los
controllers y services (validaciones, recursos no encontrados, errores inesperados). La seguridad actúa
antes: el filtro JWT y `SecurityConfig` deciden quién puede llegar al controller, y una petición
rechazada allí (por falta de token o de rol) nunca llega a lanzar una excepción que el manejador
pueda capturar. El manejador da formato a los errores; no decide permisos. Por eso hacen falta ambos.

---

### Parte 7 — Documentar, probar y publicar

**Swagger con la información de VetTurno y el botón Authorize**

![Swagger UI mostrando el título VetTurno y el botón Authorize](evidencias/swagger-authorize.png)

*Descripción:* Swagger UI con el nombre y la descripción de la API y el botón Authorize visible.

**Ejecución siguiendo este README desde cero**

![Consola mostrando la aplicación iniciada tras seguir las instrucciones del README](evidencias/ejecucion-desde-cero.png)

*Descripción:* aplicación levantada siguiendo únicamente las instrucciones de este README.

**Matriz de pruebas:** ver la sección "Matriz de pruebas manuales" más arriba.

**Repositorio GitHub:** URL_DEL_REPOSITORIO_AQUI

---

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