# Login por certificado digital (X.509 / mTLS)

El backend sirve **todo por un único puerto HTTPS con TLS mutuo (mTLS)**:
**8443**. Por ese mismo puerto funcionan los tres métodos de acceso:
usuario/clave, doble factor (TOTP) y certificado digital (X.509).

El certificado de cliente es **opcional** (`server.ssl.client-auth=want`): si el
navegador lo presenta, se usa para autenticar; si no, se sigue con usuario/clave.

En esta demo se usa una **CA de prueba** propia. En producción se sustituye el
truststore por la **cadena de CA de la FNMT** y se usa el certificado real del
usuario (el identificador se extrae del `SERIALNUMBER`, que contiene el DNI).

## 1. Generar los certificados de prueba

Requiere `keytool` (incluido en el JDK).

```powershell
cd portal-backend
powershell -ExecutionPolicy Bypass -File ".\scripts\generate-demo-certs.ps1"
```

Genera en `src/main/resources/certs/`:

- `server.p12` — keystore del servidor (cert firmado por la CA).
- `truststore.p12` — CA que valida los certificados de cliente.
- `client.p12` — certificado de cliente (password `changeit`), CN=admin.
- `ca.crt` — certificado público de la CA.

Estos archivos están en `.gitignore` (contienen claves privadas).

## 2. Arrancar el backend (HTTPS 8443)

```powershell
$env:DB_PASSWORD="1234"
.\mvnw.cmd -DskipTests spring-boot:run
```

La configuración SSL/mTLS está en `application.properties` (no requiere perfil).
Nota: los keystores deben existir antes de arrancar (paso 1).

El `DataSeeder` asigna de forma idempotente `certificateId = "admin"` al usuario
`admin`, que es el CN del certificado de cliente de prueba.

## 3. Probar el login por certificado

Sin certificado (acceso denegado):

```
GET https://localhost:8443/api/account/me   -> 403 Forbidden
```

Con certificado de cliente (autenticado como admin, sin usuario/clave):

```
GET https://localhost:8443/api/account/me   -> 200 OK
{"id":1,"username":"admin","role":"ADMIN","certificateId":"admin",...}
```

### Desde el navegador

1. Importa `client.p12` (password `changeit`) en el almacén de certificados
   personales del navegador/sistema.
2. (Opcional) Confía en `ca.crt` para evitar el aviso de certificado no fiable.
3. Abre `https://localhost:8443/api/account/me`; el navegador te pedirá elegir
   el certificado y la sesión se autenticará vía X.509.

## Configuración relevante

- `application.properties` — SSL, truststore y `server.ssl.client-auth=want`
  (pide el certificado pero no lo obliga, para no romper el login usuario/clave).
- `app.certificate.enabled=true` y `app.certificate.subject-regex` — activan el
  X.509 y definen cómo extraer el identificador del subject del certificado.
- Para FNMT real: `app.certificate.subject-regex=SERIALNUMBER=([^,]+)`.
