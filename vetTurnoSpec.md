# SPEC — VetTurno (Java AI Engineer · Módulo 3)

> Este documento es la especificación técnica para construir VetTurno de punta a punta.
> Sigue las partes en orden (1 → 7). No implementes nada que no esté descrito aquí;
> si algo no está claro, pregunta antes de improvisar.

## Contexto operativo (léelo antes de tocar código)

Este spec no es una lista de tareas para ejecutar de corrido: es la referencia técnica de todo el
proyecto. Antes de escribir una sola línea de código, el agente debe:

1. **Leer el documento completo** (las 13 secciones), no solo la parte que va a implementar en ese
   momento. Las decisiones de la sección 0 aplican a todo el proyecto, no solo a la parte 1.
2. **Producir un plan de trabajo** (una lista de tareas/todo) derivado de la sección 10, desglosando
   cada una de las 7 partes en pasos concretos: qué archivos va a crear o editar, en qué orden, y qué
   va a probar antes de dar la parte por cerrada. Ese plan se presenta o se mantiene visible como
   checklist — no se salta directo a generar código.
3. **Ejecutar una parte a la vez**, en el orden de la sección 10. No avanzar a la siguiente parte
   sin haber cerrado los puntos de control de la parte actual (las listas de verificación que trae
   cada parte del enunciado original) ni sin haber repasado el checklist de errores comunes
   (sección 13).
4. **Al cerrar cada parte**, reportar en pocas líneas qué se implementó y qué se probó, antes de
   seguir con la siguiente. Esto le permite a Esteban revisar el avance sin tener que leer todo el
   código.
5. **No improvisar decisiones de arquitectura.** Si algo no está cubierto por la sección 0 (por
   ejemplo, una versión de librería que no resuelve, o una regla de negocio ambigua), preguntar antes
   de asumir — no inventar una solución silenciosa que luego contradiga el resto del spec.
6. **No mezclar partes fuera de orden.** Por ejemplo, no tocar seguridad (parte 5) mientras el flujo
   REST base (parte 3) no esté probado y funcionando; una parte apoyada en una anterior sin terminar
   generará retrabajo.

## 0. Decisiones de arquitectura (ya tomadas — no las cambies)

- **JDK 25** (el que ya tienes instalado) con **Spring Boot 3.5.x** (usar el patch más reciente de
  la rama 3.5 disponible al crear el proyecto, ej. `3.5.16`). Spring Boot 3.3 y 3.4 **no soportan
  oficialmente JDK 25** (llegan hasta 23/24) — por eso saltamos directo a 3.5, que sí lo soporta,
  sin llegar a Spring Boot 4.x (ese cambia el modelo de configuración de Spring Security y no es
  compatible con las guías del curso). En Spring Initializr, al elegir "Java" pon `25`.
- **Maven** como build tool.
- **Relaciones JPA unidireccionales únicamente.** Mascota tiene una referencia a Propietario;
  Cita tiene referencias a Mascota y Veterinario. **Nunca** agregues una lista inversa
  (`List<Mascota>` en Propietario, etc.) — esa es la causa #1 de recursión JSON en este tipo de
  proyecto. Si en algún punto "necesitas" navegar en el otro sentido, resuélvelo con una consulta
  en el repositorio, no con una relación bidireccional.
- **Todas las respuestas de la API usan DTO**, nunca entidades directamente.
- **El primer ADMIN se crea manualmente en MySQL**, no por código. Flujo:
    1. Doña Marta se registra por `/api/auth/register` (queda como USER).
    2. Se ejecuta un `UPDATE usuario SET rol = 'ADMIN' WHERE email = '...'` directamente en MySQL Workbench.
    3. Ella vuelve a iniciar sesión (`/api/auth/login`) para que el nuevo JWT incluya el rol ADMIN.
    - No crear ningún `CommandLineRunner`, seed, ni credenciales de admin hardcodeadas en el código
      o en `application.properties`. Ese es un requisito de seguridad, no una preferencia de estilo.
- **Dependencias y versiones a pinear en el `pom.xml`:**
    - `spring-boot-starter-web`
    - `spring-boot-starter-data-jpa`
    - `mysql-connector-j` (la que resuelva el parent de Spring Boot 3.5.x)
    - `spring-boot-starter-security`
    - `spring-boot-starter-validation`
    - `io.jsonwebtoken:jjwt-api:0.12.7`, `jjwt-impl:0.12.7` (runtime), `jjwt-jackson:0.12.7` (runtime)
      (última patch de la línea 0.12.x; existe ya un 0.13.0 pero cambia de línea y puede introducir
      diferencias de API frente a lo visto en clase, así que nos quedamos en 0.12.x)
    - `org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.x` (la última patch de la línea 2.8,
      ej. `2.8.14` — esta es la línea para Spring Boot 3.x; **no** usar springdoc 3.0.x, que es para
      Spring Boot 4)
    - `lombok` (opcional, si Esteban ya lo usa en sus otros proyectos; si no, escribir getters/setters
      explícitos)
    - `spring-boot-starter-test` (scope test)
    - Si alguna versión no resuelve al construir, buscar la versión estable más reciente compatible
      con Spring Boot 3.5.x antes de continuar — no bajar a versiones muy antiguas de jjwt (evitar 0.9.x,
      tiene una API distinta e incompatible).

## 1. Historia y alcance (contexto de negocio — no técnico)

Veterinaria Huellitas necesita una API para reemplazar su cuaderno + WhatsApp. Usuarios:
- **Paula (USER)**: registra propietarios, mascotas, agenda citas, consulta la agenda.
- **Doña Marta (ADMIN)**: todo lo anterior + registrar veterinarios.
- **Doctor Andrés**: no inicia sesión, solo aparece como dato en las citas.

Fuera de alcance (no implementar): historia clínica, pagos, notificaciones, interfaz web/móvil,
Docker (hay una sección opcional al final, 0 puntos, solo hacerla si sobra tiempo).

## 2. Estructura de paquetes

Un único paquete base, por ejemplo `com.esteban.vetturno`, con subpaquetes:

```
model/       -> entidades JPA
repository/  -> interfaces JpaRepository
service/     -> lógica de negocio
controller/  -> endpoints REST
dto/         -> records/clases de entrada y salida
security/    -> JWT, filtros, UserDetailsService, SecurityConfig
config/      -> OpenApiConfig
exception/   -> ApiError, GlobalExceptionHandler
```

## 3. Modelo de datos

| Entidad | Campos | Relación |
|---|---|---|
| `Propietario` | id, nombre, teléfono, email | ninguna referencia saliente |
| `Mascota` | id, nombre, especie, raza, `propietario` (`@ManyToOne`) | apunta a Propietario |
| `Veterinario` | id, nombre, especialidad | ninguna referencia saliente |
| `Cita` | id, fechaHora, motivo, `mascota` (`@ManyToOne`), `veterinario` (`@ManyToOne`) | apunta a Mascota y Veterinario |
| `Usuario` | id, email (único), password (hash BCrypt), rol | rol es texto: `USER` o `ADMIN` |

Reglas de validación de campos (Bean Validation en los DTO de entrada, no en las entidades):
- Propietario: nombre y teléfono obligatorios (`@NotBlank`), email con formato válido (`@Email`).
- Mascota: nombre, especie y `propietarioId` obligatorios.
- Veterinario: nombre y especialidad obligatorios.
- Cita: `fechaHora` obligatoria y debe ser futura (`@Future`), motivo obligatorio,
  `mascotaId` y `veterinarioId` obligatorios.
- Registro: email obligatorio y válido, password obligatorio con longitud mínima (ej. 8).

## 4. Endpoints

| Método | Ruta | Acceso | Notas |
|---|---|---|---|
| POST | `/api/auth/register` | público | fuerza rol USER sin importar lo que mande el cliente |
| POST | `/api/auth/login` | público | devuelve JWT |
| POST | `/api/propietarios` | USER/ADMIN | 201 |
| GET | `/api/propietarios` | USER/ADMIN | lista vía DTO |
| POST | `/api/mascotas` | USER/ADMIN | valida que el propietario exista; 201 |
| GET | `/api/mascotas` | USER/ADMIN | sin anidar citas |
| POST | `/api/veterinarios` | **solo ADMIN** | USER recibe 403 |
| GET | `/api/veterinarios` | USER/ADMIN | |
| POST | `/api/citas` | USER/ADMIN | valida fecha futura + sin cruce de horario; 201 |
| GET | `/api/citas` | USER/ADMIN | agenda completa vía DTO |
| GET | `/api/citas/veterinario/{id}` | USER/ADMIN | filtro por veterinario |

## 5. DTOs (contrato exacto)

- `RegistroRequest`: email, password
- `LoginRequest`: email, password
- `AuthResponse`: token
- `PropietarioRequest`: nombre, teléfono, email
- `PropietarioDTO`: id, nombre, teléfono, email
- `MascotaRequest`: nombre, especie, raza, propietarioId
- `MascotaDTO`: id, nombre, especie, raza, propietarioId, propietarioNombre
- `VeterinarioRequest`: nombre, especialidad
- `VeterinarioDTO`: id, nombre, especialidad
- `CitaRequest`: fechaHora, motivo, mascotaId, veterinarioId
- `CitaDTO`: id, fechaHora, motivo, mascota (nombre), propietario (nombre), veterinario (nombre)
- `ApiError`: status, mensaje, errores por campo (map o lista), timestamp

## 6. Reglas de negocio (viven en el Service, no en el Controller)

- `CitaService`: antes de guardar, busca mascota y veterinario por id (si no existen → error de
  negocio → 400). Verifica que `fechaHora` sea futura. Verifica, con una **consulta derivada** en
  `CitaRepository` (ej. `existsByVeterinarioIdAndFechaHora(...)`), que no exista ya una cita para
  ese veterinario en esa fechaHora exacta. Si existe → 400.
- `MascotaService`: verifica que el `propietarioId` recibido exista antes de crear la mascota.

## 7. Seguridad

- Registro y login públicos. Todo lo demás requiere `Authorization: Bearer <token>`.
- Rutas de Swagger (`/swagger-ui/**`, `/v3/api-docs/**`) también públicas, pero sin abrir
  endpoints de negocio.
- Sesión **stateless** (sin `HttpSession`).
- `/api/veterinarios` en POST exige rol ADMIN — el resto de negocio acepta USER o ADMIN.
- El registro (`AuthService`) siempre guarda `rol = USER`, ignorando cualquier rol que venga
  en el request.
- Password con BCrypt (`PasswordEncoder` bean).
- Primer ADMIN: ver sección 0 (manual, por SQL).

## 8. Manejo de errores

- `ApiError` con status, mensaje, errores por campo, timestamp.
- `GlobalExceptionHandler` (`@RestControllerAdvice`) traduce:
    - Errores de `@Valid` → 400 con detalle por campo.
    - Excepciones de reglas de negocio (mascota/veterinario inexistente, cruce de horario,
      fecha pasada) → 400 con mensaje claro.
    - Cualquier excepción no controlada → 500 con mensaje genérico, sin trazas ni nombres de clases.
- Estados HTTP: 200 (consulta/login), 201 (creación), 400 (inválido/regla de negocio), 403 (sin permiso), 500 (error interno).

## 9. Swagger / OpenAPI

- Configurar `OpenApiConfig` con nombre "VetTurno", descripción de Veterinaria Huellitas,
  y esquema de seguridad Bearer JWT (para que aparezca el botón "Authorize").

## 10. Orden de implementación (sigue esto, no saltes partes)

1. **Parte 1**: crear proyecto (Spring Initializr), paquetes vacíos, `.gitignore`, primer commit,
   README inicial, confirmar que arranca.
2. **Parte 2**: entidades + repositorios + conexión MySQL (`vetturno`), confirmar tablas y FKs en
   Workbench.
3. **Parte 3**: Propietario, Mascota, Veterinario completos (service + controller + DTO), probar
   con Postman/Swagger antes de seguir.
4. **Parte 4**: Cita completa con las reglas de fecha futura y anti-cruce.
5. **Parte 5**: seguridad completa (Usuario, JWT, filtro, roles) — habilitar el primer ADMIN
   siguiendo la sección 0 y volver a probar `/api/veterinarios` con USER (403) y ADMIN (201).
6. **Parte 6**: validaciones + `ApiError` + `GlobalExceptionHandler`, reprobar los casos de error
   de las partes anteriores para confirmar que ahora responden bien.
7. **Parte 7**: Swagger, correr la matriz de 15 pruebas manuales (abajo), completar README,
   commits limpios, push a GitHub.

## 11. Matriz de pruebas manuales (obligatoria, con evidencia real)

1. App inicia con MySQL disponible.
2. Registro válido de Paula → 200 + token, password hasheada.
3. Registro con email inválido y password corta → 400 con errores por campo.
4. Login válido → 200 + JWT.
5. `GET /api/citas` sin token → rechazado.
6. `POST /api/veterinarios` con USER → 403.
7. `POST /api/veterinarios` con ADMIN → 201.
8. Creación válida de propietario → 201, DTO plano.
9. Creación de mascota con propietario existente → 201, relación correcta.
10. Mascota con propietario inexistente → 400, no se inserta fila.
11. Cita futura válida → 201, persistida.
12. Cita con fecha pasada → 400.
13. Segundo intento mismo veterinario + horario → 400, solo queda una cita.
14. Filtro de citas por veterinario → 200, solo coincidencias.
15. Reinicio de la app + prueba desde Swagger con Authorize → los datos persisten.

## 12. README final (entregable)

Debe incluir: historia de la veterinaria, alcance, tecnologías, arquitectura, cómo configurar
MySQL, cómo ejecutar, lista de endpoints, roles, cómo probar (con el paso a paso de la sección 0
para el primer ADMIN incluido), y la matriz de pruebas con resultados reales.

## 13. Errores comunes a evitar (checklist final antes de dar por terminada cada parte)

- [ ] ¿Alguna entidad tiene una relación bidireccional? → eliminarla.
- [ ] ¿Algún controller devuelve una entidad directamente en vez de un DTO? → corregir.
- [ ] ¿Hay alguna contraseña o secreto en el código, en `application.properties` versionado,
  o en el README? → sacarlo, usar variables de entorno si es necesario.
- [ ] ¿El registro permite que el cliente elija su propio rol? → siempre forzar USER en el service.
- [ ] ¿La comprobación de cruce de horario vive en el Service (no en el Controller)?
- [ ] ¿`target/`, `.idea/`, `*.iml` están en `.gitignore`?

## 14. Estado actual del proyecto (avance real al día de hoy)

**Hecho y verificado:**
- Parte 1, 2 y 3 completas: proyecto arranca, MySQL conectado, entidades y repositorios de las 5
  tablas creados, CRUD completo (Propietario, Mascota, Veterinario) con DTO e inyección por
  constructor.
- `application.properties` ya usa `${DB_PASSWORD}` (sin secretos en texto plano) y
  `ddl-auto=update` (los datos persisten entre reinicios).
- Ya existe `RecursoNoEncontradoException` + `GlobalExceptionHandler` para el caso de
  propietario inexistente al crear una mascota.

**Pendiente de un ajuste de una línea:**
- `GlobalExceptionHandler.handleRecursoNoEncontrado` responde `HttpStatus.NOT_FOUND` (404).
  Según la sección 8 de este spec y la prueba #10 de la matriz, debe responder
  `HttpStatus.BAD_REQUEST` (400).

**Falta por implementar (Partes 4 a 7):**
- `CitaService` + `CitaController` (regla de fecha futura + anti-cruce de horario).
- `Usuario`/JWT/`SecurityConfig`/roles — los modelos `Usuario` y `UsuarioRepository` ya existen,
  falta el flujo de auth, el filtro JWT y la configuración de seguridad.
- Bean Validation (`@Valid`) en los DTO de entrada, y extender `GlobalExceptionHandler` para
  cubrir errores de validación y las reglas de negocio de citas.
- `OpenApiConfig` (Swagger), correr la matriz de 15 pruebas con evidencia, completar el README,
  y publicar en GitHub.

**Detalle menor pendiente:** el README dice `com.esteban.vetturno` y el paquete real es
`com.DEVSenior.vetturno` — antes de la entrega final, unificar el nombre en README, pom.xml
(`groupId`) y el código para que sean coherentes (la rúbrica evalúa "nombres coherentes con
VetTurno" en el criterio 1).