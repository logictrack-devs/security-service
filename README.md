# security-service
# Servicio de Seguridad y Autenticación (security-service)

Microservicio centralizado de la arquitectura LogiTrack encargado de la gestión de identidades, encriptación de credenciales, emisión de tokens JWT y control de acceso basado en roles y pertenencia de recursos.

## Funcionalidades Mínimas Implementadas
*   **Registro Seguro de Usuarios:** Alta de nuevos perfiles almacenando contraseñas mediante algoritmos de hash robustos (`BCryptPasswordEncoder`) proporcionados por Spring Security.
*   **Autenticación y Emisión de JWT:** Validación de credenciales y generación de tokens JWT firmados criptográficamente con clave secreta verificable.
*   **Validación Descentralizada:** Configuración de firma segura que permite a los microservicios (`shipment-service` y `courier-service`) validar autenticidad, integridad y expiración del token de manera local, sin depender de consultas HTTP constantes a `security-service`.
*   **Gestión de Roles:** Administración nativa de perfiles de seguridad para `ADMIN`, `CLIENTE` y `REPARTIDOR`.
*   **Endpoints REST:** Exposición de rutas para el registro, inicio de sesión (login) y consulta del perfil del usuario autenticado.

## Matriz de Autorización y Control de Acceso
El sistema aplica reglas estrictas combinando validación de roles y propiedad del recurso:
*   **`ADMIN`:** Acceso total para administrar envíos y repartidores, consultar información operativa global y gestionar asignaciones.
*   **`CLIENTE`:** Permiso para registrar nuevos envíos y consultar únicamente el historial de envíos asociados a su propia identidad.
*   **`REPARTIDOR`:** Acceso restringido para consultar exclusivamente los envíos asignados a su perfil y actualizar su estado operativo.

## Configuración Principal (`application.yml`)
```yaml
server:
  port: 8081

spring:
  application:
    name: security-service
  datasource:
    url: jdbc:mysql://localhost:3306/logitrack_security_db?createDatabaseIfNotExist=true
    username: root
    password: tu_password_mysql
  jpa:
    hibernate:
      ddl-auto: update

jwt:
  secret: ClaveSecretaSuperSeguraParaFirmarTokensLogiTrack2026
  expiration: 86400000

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

## Endpoints Principales Disponibles
*   `POST /api/v1/auth/register` - Registro de nuevos usuarios con encriptación de contraseña.
*   `POST /api/v1/auth/login` - Autenticación de credenciales y retorno del token JWT.
*   `GET /api/v1/auth/me` - Consulta de los datos y rol del usuario autenticado mediante su cabecera `Authorization`.
