# A04:2025 – Demostración integrada de cifrado sin autenticación

**Estado:** Pendiente de implementar. No se ha encontrado AES-ECB ni otro cifrado débil en el código actual.

## Contexto

La diapositiva «Cifrado autenticado» compara AES-GCM o ChaCha20-Poly1305 con ECB, que revela patrones y no autentica el contenido. El servicio sí tiene un flujo real para integrar la demostración: `OutboxJpaAdapter` serializa el evento `fuel_anomaly_detected_event`, lo guarda en `outbox_events.payload` y luego lo lee para publicarlo. Actualmente ese payload se guarda como JSON; el repositorio no afirma que esté cifrado.

## Trabajo propuesto para la demostración

- En la rama vulnerable, cifrar el payload del outbox con AES-ECB y una clave fija de laboratorio. Mantener la lectura y publicación del evento funcionando para que la vulnerabilidad ocurra en el flujo real.
- Demostrar que bloques de texto iguales generan bloques cifrados iguales y que el esquema no aporta autenticación del mensaje.
- En la rama corregida, usar AES-GCM con nonce aleatorio único por cifrado y verificación de la etiqueta al descifrar. Cargar la clave desde un secreto externo al repositorio.
- Documentar el formato de almacenamiento y la migración o limpieza de eventos cifrados con la versión vulnerable, ya que la rama corregida no podría leerlos directamente.

## Criterio de cierre

El mismo evento se registra y publica en ambas ramas; la versión corregida rechaza un ciphertext modificado. La clave de laboratorio nunca se usa con datos reales.

## Referencia

- [OWASP Top 10:2025 A04 – Cryptographic Failures](https://top10.owasp.org/2025/A04_2025-Cryptographic_Failures/)
