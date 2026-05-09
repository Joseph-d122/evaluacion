# Definición de Anotaciones (Vínculos) - Interceptor Bindings

## 📌 Concepto de InterceptorBinding

Las anotaciones de vínculo (InterceptorBinding) son el mecanismo que vincula un **interceptor** con los métodos que debe interceptar. Sin estas anotaciones, los interceptores no sabrían qué métodos deben procesar.

### Flujo de Vinculación
```
1. Definir anotación con @InterceptorBinding
           ↓
2. Aplicar anotación a un interceptor @Interceptor
           ↓
3. Marcar métodos con la misma anotación
           ↓
4. CDI vincula automáticamente interceptor ↔ método
```

---

## 📋 Anotaciones Disponibles

### 1. **@AuditMemo**
**Ubicación:** `org.acme.interceptor.AuditMemo`

Para auditoría de operaciones de correo electrónico.

```java
@AuditMemo("Envío de correo")
public boolean enviarCorreo(String to, String subject, String body) {
    // Se intercepta automáticamente
}
```

**Parámetros:**
- `value`: Descripción opcional de la operación

**Uso en Interceptor:**
```java
@Interceptor
@AuditMemo  // ← Vínculo establecido
public class AuditoriaMailInterceptor { ... }
```

---

### 2. **@AuditLog**
**Ubicación:** `org.acme.interceptor.AuditLog`

Para auditoría general de operaciones con niveles de severidad.

```java
@AuditLog(
    value = "Operación crítica de negocio",
    level = AuditLog.AuditLevel.CRITICAL
)
public void procesarPago(Pago pago) {
    // Se intercepta y audita como CRITICAL
}
```

**Parámetros:**
- `value`: Descripción de la operación
- `level`: Nivel de severidad (`DEBUG`, `INFO`, `WARN`, `ERROR`, `CRITICAL`)

**Niveles Disponibles:**
```
DEBUG    → Información detallada (desarrollo)
INFO     → Información general (producción)
WARN     → Advertencias (revisar)
ERROR    → Errores (acción requerida)
CRITICAL → Operaciones críticas (máxima prioridad)
```

---

### 3. **@AuditSecurity**
**Ubicación:** `org.acme.interceptor.AuditSecurity`

Para auditoría de operaciones de seguridad, autenticación y acceso.

```java
@AuditSecurity(
    operacion = "LOGIN",
    recurso = "usuario_administrativo",
    registrarParametros = false  // No registra credenciales
)
public boolean autenticar(String usuario, String password) {
    // Se intercepta sin guardar la contraseña
}
```

**Parámetros:**
- `operacion`: Tipo de operación (`LOGIN`, `LOGOUT`, `ACCESO`, `CAMBIO_PERMISOS`)
- `recurso`: Recurso afectado
- `registrarParametros`: Si es `true`, registra parámetros (cuidado con datos sensibles)

**Casos de Uso:**
```java
// Autenticación
@AuditSecurity(operacion = "LOGIN", recurso = "usuario")
public Usuario login(String email, String password) { ... }

// Control de acceso
@AuditSecurity(operacion = "ACCESO", recurso = "modulo_reportes")
public List<Reporte> obtenerReportes() { ... }

// Cambio de permisos
@AuditSecurity(operacion = "CAMBIO_PERMISOS", recurso = "rol_usuario")
public void asignarRol(Usuario usuario, Rol rol) { ... }
```

---

### 4. **@AuditPerformance**
**Ubicación:** `org.acme.interceptor.AuditPerformance`

Para monitoreo de rendimiento y detección de cuellos de botella.

```java
@AuditPerformance(
    umbralMs = 1000,           // Alerta si tarda más de 1 segundo
    registrarMemoria = true,   // Registra consumo de memoria
    registrarIteraciones = true // Registra número de iteraciones
)
public List<Usuario> buscarUsuarios(String filtro) {
    // Se intercepta y monitorea rendimiento
}
```

**Parámetros:**
- `umbralMs`: Tiempo máximo en milisegundos (default: 5000)
- `registrarMemoria`: Captura el uso de memoria (default: false)
- `registrarIteraciones`: Número de iteraciones en bucles (default: false)

**Casos de Uso:**
```java
// Consulta de base de datos
@AuditPerformance(umbralMs = 500, registrarMemoria = true)
public List<Producto> listarProductos() { ... }

// Procesamiento pesado
@AuditPerformance(umbralMs = 3000, registrarIteraciones = true)
public void procesarLote(List<Registro> registros) { ... }

// API externa
@AuditPerformance(umbralMs = 2000)
public DatosExternos consultarAPI(String endpoint) { ... }
```

---

## 🔗 Cómo Crear una Nueva Anotación de Vínculo

### Paso 1: Definir la Anotación

```java
@InterceptorBinding  // ← Marca como InterceptorBinding
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MiAuditoria {
    String descripcion() default "";
}
```

### Paso 2: Crear el Interceptor

```java
@Interceptor
@MiAuditoria  // ← Aplica el vínculo
public class MiInterceptor {
    
    @AroundInvoke
    public Object interceptar(InvocationContext context) throws Exception {
        // Lógica del interceptor
        return context.proceed();
    }
}
```

### Paso 3: Usar en Métodos

```java
@MiAuditoria(descripcion = "Mi operación")
public void miMetodo() {
    // Se intercepta automáticamente
}
```

---

## 📊 Matriz de Anotaciones

| Anotación | Propósito | Parámetros | Casos de Uso |
|-----------|-----------|-----------|--------------|
| **@AuditMemo** | Correos | `value` | Envío de mails |
| **@AuditLog** | General | `value`, `level` | Auditoría general |
| **@AuditSecurity** | Seguridad | `operacion`, `recurso`, `registrarParametros` | Login, permisos |
| **@AuditPerformance** | Rendimiento | `umbralMs`, `registrarMemoria`, `registrarIteraciones` | Consultas, APIs |

---

## ⚙️ Combinación de Anotaciones

Un método puede tener múltiples anotaciones:

```java
@AuditLog(level = AuditLog.AuditLevel.CRITICAL)
@AuditSecurity(operacion = "PAGO", registrarParametros = false)
@AuditPerformance(umbralMs = 2000)
public void procesarPagoSeguro(Pago pago) {
    // Se intercepta con 3 interceptores:
    // 1. AuditoriaMailInterceptor (no aplica, sin @AuditMemo)
    // 2. AuditLogInterceptor
    // 3. AuditSecurityInterceptor
    // 4. AuditPerformanceInterceptor
}
```

---

## ✅ Reglas Importantes

1. **Una anotación = Un vínculo**
   - Cada `@InterceptorBinding` es independiente
   - No hay herencia entre bindings

2. **El interceptor debe tener la anotación**
   ```java
   @Interceptor
   @MiAuditoria  // ← OBLIGATORIO
   public class MiInterceptor { ... }
   ```

3. **El método debe tener la anotación**
   ```java
   @MiAuditoria  // ← OBLIGATORIO
   public void miMetodo() { ... }
   ```

4. **Visibilidad en runtime**
   - Usa `@Retention(RetentionPolicy.RUNTIME)`
   - CDI necesita acceso en tiempo de ejecución

5. **Aplicable a método y tipo**
   - `@Target({ElementType.METHOD, ElementType.TYPE})`
   - Aplica a nivel de clase o método

---

## 🎯 Ejemplo Completo Integrado

```java
// Anotación
@InterceptorBinding
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuditMemo {
    String value() default "";
}

// Interceptor
@Interceptor
@AuditMemo
public class AuditoriaMailInterceptor {
    @AroundInvoke
    public Object auditar(InvocationContext context) throws Exception {
        System.out.println("Inicio: " + context.getMethod().getName());
        try {
            return context.proceed();
        } finally {
            System.out.println("Fin: " + context.getMethod().getName());
        }
    }
}

// Uso
@ApplicationScoped
public class MailService {
    @AuditMemo("Envío de correo")
    public void enviar(String to, String subject, String body) {
        // Se intercepta automáticamente
    }
}
```

---

## 📚 Referencias

- [Jakarta Interceptors Specification](https://jakarta.ee/specifications/interceptors/)
- [CDI Interceptor Bindings](https://jakarta.ee/learn/docs/jakartaee-tutorial/current/cdi)
- [Quarkus Interceptors](https://quarkus.io/guides/cdi#interceptors)
