# A04:2025 – Demostración integrada de hash inseguro de contraseñas

**Estado:** Pendiente de implementar. No es una vulnerabilidad confirmada del servicio actual.

## Contexto

La diapositiva «Contraseñas con hash lento» pide mostrar por qué MD5 o SHA-1 sin salt son inseguros frente a Argon2id, scrypt o PBKDF2. El microservicio de Combustible no gestiona cuentas ni contraseñas de usuarios; su `DB_PASSWORD` es una credencial de conexión recibida del entorno y no debe transformarse en un hash.

Existe un ejemplo aislado en `../VulnerablePasswordHashDemo.java`, pero no participa en un flujo real del servicio. Por ello, no se debe presentar como un hallazgo de producción.

## Trabajo propuesto para la demostración

- Añadir un flujo de usuario de laboratorio que almacene un hash MD5 sin salt, conectado a una operación real del servicio. Mantenerlo exclusivamente en una rama de demostración, con cuentas y datos ficticios.
- Mostrar que dos usuarios con la misma contraseña generan el mismo hash y que un candidato se comprueba con rapidez.
- En la rama corregida, sustituirlo por un algoritmo de hash de contraseñas con salt único y costo configurado. Para una implementación nueva, preferir Argon2id; usar scrypt o PBKDF2 cuando corresponda a las bibliotecas y requisitos del proyecto.
- Documentar cómo reproducir el flujo y cómo verificar que los hashes cambian aunque la contraseña coincida.

## Criterio de cierre

La versión vulnerable y la corregida se ejercitan mediante el mismo flujo integrado, sin usar contraseñas reales y sin afirmar que el servicio original tenía autenticación de usuarios.

## Referencia

- [OWASP Password Storage Cheat Sheet](https://cheatsheetseries.owasp.org/cheatsheets/Password_Storage_Cheat_Sheet.html)
