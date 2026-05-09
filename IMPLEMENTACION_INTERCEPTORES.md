# Implementación de Interceptores

## 📌 Concepto General

Un interceptor es una clase que intercepta las invocaciones de métodos para ejecutar lógica adicional. En el patrón de cadena de interceptores, múltiples interceptores pueden procesar la misma invocación de forma secuencial.

### Ciclo de vida de una invocación interceptada

```
┌─────────────────────┐
│  Código Cliente     │
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│ Interceptor 1 (Pre) │  ← Ejecuta antes
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│ Interceptor 2 (Pre) │  ← Ejecuta antes
└──────────┬──────────┘
           ↓
┌──────────────────────────┐
│  Método Original Ejecuta │
└──────────┬───────────────┘
           ↓
┌─────────────────────┐
│ Interceptor 2 (Post)│  ← Ejecuta después
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│ Interceptor 1 (Post)│  ← Ejecuta después
└──────────┬──────────┘
           ↓
┌─────────────────────┐
│   Devuelve Resultado│
└─────────────────────┘
```

---

## 🔗 Interceptores Implementados

### 1. **AuditoriaMailInterceptor**

**Vínculo:** `@AuditMemo`

**Ubicación:** `org.acme.interceptor.AuditoriaMailInterceptor`

**Propósito:** Auditar operaciones de correo electrónico

**Características:**
- Registra información de envío de mails
- Captura parámetros del correo
- Mide duración de la operación
- Diferencia entre éxito y error

**Ejemplo de Salida:**
```
=== AUDITORÍA MAIL - INICIO ===
Clase: MailService
Método: enviarCorreo
Parámetros: usuario@example.com, Asunto de prueba, Cuerpo del mensaje
Timestamp: 1735507200000

[Ejecuta el método]

Estado: EXITOSO
Tiempo de ejecución: 5 ms
=== AUDITORÍA MAIL - FIN (OK) ===
```

---

### 2. **AuditLogInterceptor**

**Vínculo:** `@AuditLog`

**Ubicación:** `org.acme.interceptor.AuditLogInterceptor`

**Propósito:** Auditoría general con niveles de severidad

**Características:**
- 5 niveles: `DEBUG`, `INFO`, `WARN`, `ERROR`, `CRITICAL`
- Ajusta el nivel de logging según severidad
- Mapea niveles a Logger methods:
  - `DEBUG` → `LOGGER.fine()`
  - `INFO` → `LOGGER.info()`
  - `WARN` → `LOGGER.warning()`
  - `ERROR` → `LOGGER.severe()`
  - `CRITICAL` → `LOGGER.severe("*** CRÍTICO ***")`

**Uso:**
```java
@AuditLog(value = "Procesamiento de pago", level = AuditLog.AuditLevel.CRITICAL)
public void procesarPago(Pago pago) { ... }
```

**Ejemplo de Salida (CRITICAL):**
```
*** CRÍTICO *** === AUDITORÍA LOG - INICIO ===
*** CRÍTICO *** Descripción: Procesamiento de pago
*** CRÍTICO *** Estado: EXITOSO
*** CRÍTICO *** Tiempo de ejecución: 250 ms
*** CRÍTICO *** === AUDITORÍA LOG - FIN (OK) ===
```

---

### 3. **AuditSecurityInterceptor**

**Vínculo:** `@AuditSecurity`

**Ubicación:** `org.acme.interceptor.AuditSecurityInterceptor`

**Propósito:** Auditoría de seguridad con protección de datos sensibles

**Características:**
- Registra usuario y dirección IP
- Diferencia operaciones: `LOGIN`, `LOGOUT`, `ACCESO`, `CAMBIO_PERMISOS`
- Opción para no registrar parámetros sensibles
- Oculta strings largos
- Diferencia entre acceso permitido y denegado

**Uso:**
```java
@AuditSecurity(
    operacion = "LOGIN",
    recurso = "usuario_administrativo",
    registrarParametros = false  // No registra contraseña
)
public boolean autenticar(String usuario, String password) { ... }
```

**Ejemplo de Salida (Exitoso):**
```
=== AUDITORÍA SEGURIDAD - INICIO ===
Operación: LOGIN
Recurso: usuario_administrativo
Clase: UsuarioService
Usuario: admin
IP: 127.0.0.1
Parámetros: [PROTEGIDOS - No se registran datos sensibles]
...
Estado: EXITOSO
=== AUDITORÍA SEGURIDAD - FIN (PERMITIDO) ===
```

**Ejemplo de Salida (Error/Denegado):**
```
*** ALERTA SEGURIDAD ***
Estado: ERROR
Operación fallida: LOGIN en recurso: usuario_administrativo
Excepción: AuthenticationException - Credenciales inválidas
Usuario: atacante
*** AUDITORÍA SEGURIDAD - FIN (DENEGADO) ***
```

---

### 4. **AuditPerformanceInterceptor**

**Vínculo:** `@AuditPerformance`

**Ubicación:** `org.acme.interceptor.AuditPerformanceInterceptor`

**Propósito:** Monitoreo de rendimiento y detección de cuellos de botella

**Características:**
- Mide tiempo en nanosegundos (precisión ms)
- Captura memoria inicial y final
- Registra cambio de memoria
- Alerta si tiempo > umbral configurado
- Formatea memoria legible (B, KB, MB, GB)

**Uso:**
```java
@AuditPerformance(
    umbralMs = 1000,
    registrarMemoria = true,
    registrarIteraciones = true
)
public List<Usuario> buscarUsuarios(String filtro) { ... }
```

**Ejemplo de Salida (Dentro del umbral):**
```
=== AUDITORÍA RENDIMIENTO - INICIO ===
Clase: UsuarioService
Método: buscarUsuarios
Umbral configurado: 1000 ms
Memoria inicial: 45.32 MB

Tiempo de ejecución: 234 ms
Memoria final: 46.15 MB
Cambio de memoria: 0.83 MB

Rendimiento dentro del umbral: 234/1000 ms
Estado: EXITOSO
=== AUDITORÍA RENDIMIENTO - FIN (OK) ===
```

**Ejemplo de Salida (Excede umbral):**
```
=== AUDITORÍA RENDIMIENTO - INICIO ===
Tiempo de ejecución: 1523 ms

*** ALERTA: RENDIMIENTO POR DEBAJO DEL UMBRAL ***
Tiempo de ejecución (1523 ms) > umbral (1000 ms)
Recomendación: Revisar e optimizar método buscarUsuarios
=== AUDITORÍA RENDIMIENTO - FIN (OK) ===
```

---

## 🔄 Cadena de Interceptores

### Orden de Ejecución

Cuando un método tiene múltiples anotaciones, los interceptores se aplican en orden:

```java
@AuditLog(level = AuditLog.AuditLevel.CRITICAL)
@AuditSecurity(operacion = "PAGO")
@AuditPerformance(umbralMs = 2000)
public void procesarPagoSeguro(Pago pago) {
    // Procesamiento real
}
```

**Flujo de ejecución:**

```
1. AuditLogInterceptor.auditar() - PRE
   └─> Log: Inicio de auditoría CRITICAL
       
2. AuditSecurityInterceptor.auditar() - PRE
   └─> Log: Operación PAGO, Usuario, IP
       
3. AuditPerformanceInterceptor.auditar() - PRE
   └─> Captura: Tiempo inicial, Memoria inicial
       
   4. MÉTODO REAL: procesarPagoSeguro()
   
3. AuditPerformanceInterceptor.auditar() - POST
   └─> Log: Duración, Memoria utilizada, Alerta si > 2000ms
       
2. AuditSecurityInterceptor.auditar() - POST
   └─> Log: Estado EXITOSO/ERROR, Detalles seguridad
       
1. AuditLogInterceptor.auditar() - POST
   └─> Log: Fin de auditoría CRITICAL
```

---

## 📝 Implementación de @AroundInvoke

Todo interceptor sigue este patrón:

```java
@Interceptor
@MiAuditoria
public class MiInterceptor {
    
    @AroundInvoke
    public Object interceptar(InvocationContext context) throws Exception {
        try {
            // ANTES de ejecutar el método
            System.out.println("Inicio: " + context.getMethod().getName());
            
            // Ejecutar el método original
            Object resultado = context.proceed();
            
            // DESPUÉS de ejecutar exitosamente
            System.out.println("Fin exitoso");
            
            return resultado;
            
        } catch (Exception e) {
            // CAPTURAR excepciones
            System.out.println("Error: " + e.getMessage());
            throw e;  // Propagar la excepción
        }
    }
}
```

### Métodos importantes de InvocationContext:

| Método | Descripción |
|--------|-------------|
| `getMethod()` | Obtiene el Method original |
| `getTarget()` | Obtiene la instancia del objeto |
| `getParameters()` | Obtiene los parámetros |
| `proceed()` | Ejecuta el siguiente interceptor o método |
| `setParameters()` | Modifica los parámetros |

---

## 🎯 Casos de Uso Prácticos

### Caso 1: Operación Crítica de Pago

```java
@AuditLog(value = "Procesamiento de pago", level = AuditLog.AuditLevel.CRITICAL)
@AuditSecurity(operacion = "PAGO", registro = "transaccion_financiera")
@AuditPerformance(umbralMs = 5000)
public boolean procesarPago(Pago pago, Usuario usuario) {
    // Genera 3 auditorías automáticamente
    return true;
}
```

**Registra:**
- ✓ Pago realizado (CRITICAL)
- ✓ Acceso a transacción financiera (Usuario, IP)
- ✓ Rendimiento de la operación

---

### Caso 2: Login de Usuario

```java
@AuditSecurity(
    operacion = "LOGIN",
    recurso = "cuenta_usuario",
    registrarParametros = false
)
public Usuario login(String email, String password) {
    // No registra password
    return usuario;
}
```

**Registra:**
- ✓ Intento de login
- ✓ Usuario, IP, Timestamp
- ✗ Contraseña (protegida)

---

### Caso 3: Búsqueda en Base de Datos

```java
@AuditLog(level = AuditLog.AuditLevel.INFO)
@AuditPerformance(
    umbralMs = 500,
    registrarMemoria = true
)
public List<Usuario> buscar(String criteria) {
    // Monitorea rendimiento y memoria
    return usuarios;
}
```

**Registra:**
- ✓ Búsqueda realizada
- ✓ Tiempo de ejecución (alerta si > 500ms)
- ✓ Memoria utilizada

---

## ⚙️ Configuración Recomendada

### Por tipo de operación:

| Tipo | @AuditLog | @AuditSecurity | @AuditPerformance | @AuditMemo |
|------|-----------|----------------|-------------------|-----------|
| Login | INFO | ✓ | - | - |
| Pago | CRITICAL | ✓ | ✓ | - |
| Reporte | INFO | - | ✓ | - |
| Mail | - | - | - | ✓ |
| Acceso BD | INFO | - | ✓ | - |
| Admin | CRITICAL | ✓ | ✓ | - |

---

## 🚀 Mejores Prácticas

1. **Específico, no genérico**
   - Usar `@AuditSecurity` solo para operaciones sensibles
   - No interceptar todo (costo de rendimiento)

2. **Proteger datos sensibles**
   - `registrarParametros = false` para Login
   - Ocultar valores largos automáticamente

3. **Umbrales realistas**
   - BD: 500-1000ms
   - APIs externas: 2000-5000ms
   - Procesamiento: 5000-10000ms

4. **Niveles apropiados**
   - DEBUG: Solo desarrollo
   - INFO: Operaciones normales
   - CRITICAL: Operaciones financieras

5. **Cadenas sensatas**
   - Max 3-4 interceptores por método
   - No combinar innecesariamente

---

## 📚 Referencias

- [Jakarta Interceptors](https://jakarta.ee/specifications/interceptors/)
- [CDI AroundInvoke](https://jakarta.ee/learn/docs/jakartaee-tutorial/current/cdi)
- [Quarkus Guide](https://quarkus.io/guides/cdi#interceptors)
