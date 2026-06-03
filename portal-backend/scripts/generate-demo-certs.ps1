<#
.SYNOPSIS
  Genera una CA de prueba, un certificado de servidor (HTTPS) y un certificado
  de cliente (.p12) para demostrar el login por certificado digital (X.509 / mTLS).

  USO EXCLUSIVO PARA DESARROLLO / DEMO. No usar estos certificados en producción.
  En producción se sustituye el truststore por la cadena de la FNMT y se usa
  el certificado real del usuario.

  Requisitos: keytool (incluido en el JDK).

  Salida (en portal-backend/src/main/resources/certs):
    - server.p12      -> keystore del servidor (clave + cert firmado por la CA)
    - truststore.p12  -> truststore con la CA (para validar certs de cliente)
    - client.p12      -> certificado de cliente para importar en el navegador / curl
    - ca.crt          -> certificado público de la CA
#>

$ErrorActionPreference = "Stop"

# Carpeta de salida (resources para que sea accesible vía classpath:certs/)
$certDir = Join-Path $PSScriptRoot "..\src\main\resources\certs"
New-Item -ItemType Directory -Force -Path $certDir | Out-Null
Set-Location $certDir

$pass = "changeit"
$caStore     = "ca.p12"
$serverStore = "server.p12"
$clientStore = "client.p12"
$trustStore  = "truststore.p12"

# Limpieza previa
Remove-Item -ErrorAction SilentlyContinue *.p12, *.crt, *.csr

Write-Host "1) Generando CA de prueba..."
keytool -genkeypair -alias ca -keyalg RSA -keysize 2048 -validity 3650 `
  -dname "CN=Portal Internos Demo CA,O=Portal Internos,C=ES" `
  -ext "bc:c" -keystore $caStore -storetype PKCS12 -storepass $pass
keytool -exportcert -alias ca -keystore $caStore -storepass $pass -rfc -file ca.crt

Write-Host "2) Generando certificado de SERVIDOR (CN=localhost) y firmándolo con la CA..."
keytool -genkeypair -alias server -keyalg RSA -keysize 2048 -validity 3650 `
  -dname "CN=localhost,O=Portal Internos,C=ES" `
  -ext "san=dns:localhost,ip:127.0.0.1" `
  -keystore $serverStore -storetype PKCS12 -storepass $pass
keytool -certreq -alias server -keystore $serverStore -storepass $pass -file server.csr
keytool -gencert -alias ca -keystore $caStore -storepass $pass `
  -ext "san=dns:localhost,ip:127.0.0.1" -validity 3650 `
  -infile server.csr -outfile server.crt
keytool -importcert -alias ca -keystore $serverStore -storepass $pass -file ca.crt -noprompt
keytool -importcert -alias server -keystore $serverStore -storepass $pass -file server.crt -noprompt

Write-Host "3) Generando certificado de CLIENTE (CN=admin) y firmándolo con la CA..."
# El CN del cliente debe coincidir con el campo certificateId del usuario en BD.
keytool -genkeypair -alias client -keyalg RSA -keysize 2048 -validity 3650 `
  -dname "CN=admin,O=Portal Internos,C=ES" `
  -keystore $clientStore -storetype PKCS12 -storepass $pass
keytool -certreq -alias client -keystore $clientStore -storepass $pass -file client.csr
keytool -gencert -alias ca -keystore $caStore -storepass $pass -validity 3650 `
  -infile client.csr -outfile client.crt
keytool -importcert -alias ca -keystore $clientStore -storepass $pass -file ca.crt -noprompt
keytool -importcert -alias client -keystore $clientStore -storepass $pass -file client.crt -noprompt

Write-Host "4) Generando truststore con la CA (el servidor confía en certs firmados por ella)..."
keytool -importcert -alias ca -keystore $trustStore -storetype PKCS12 -storepass $pass -file ca.crt -noprompt

# Limpieza de intermedios
Remove-Item -ErrorAction SilentlyContinue *.csr, server.crt, client.crt, $caStore

Write-Host ""
Write-Host "Listo. Archivos generados en: $certDir"
Write-Host "  - server.p12 / truststore.p12  -> para el backend (perfil 'cert')"
Write-Host "  - client.p12 (password: $pass) -> importar en el navegador o usar con curl"
Write-Host "  - ca.crt                       -> CA a confiar en el navegador"
