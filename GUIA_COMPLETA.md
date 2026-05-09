# 📚 TALLER 11: CADENA DE INTERCEPTORES - GUÍA COMPLETA

## 🎯 Objetivo

Implementar una **cadena de interceptores** en Quarkus/CDI que permite auditar diferentes tipos de operaciones de forma automática mediante anotaciones (vínculos).

---

## 🏗️ Estructura del Proyecto

```
quarkus-getting-started/
├── src/main/java/org/acme/
│   ├── interceptor/                    # 📍 Anotaciones e Interceptores
│   │   ├── AuditMemo.java             # Binding para correos
│   │   ├── AuditLog.java              # Binding para auditoría general
│   │   ├── AuditSecurity.java         # Binding para seguridad
│   │   ├── AuditPerformance.java      # Binding para rendimiento
│   │   ├── AuditoriaMailInterceptor.java
│   │   ├── AuditLogInterceptor.java
│   │   ├── AuditSecurityInterceptor.java
│   │   └── AuditPerformanceInterceptor.java
│   │
│   ├── mail/                           # 📬 Servicio de Mail
│   │   └── MailService.java
│   │
│   ├── service/                        # 👥 Servicios de Negocio
│   │   ├── UsuarioService.java
│   │   └── ReporteService.java
│   │
│   └── Main.java                       # 🎬 Aplicación Principal
│
├── TALLER11.md                         # 📖 Documentación inicial
├── ANOTACIONES_VINCULOS.md             # 📋 Guía de anotaciones
├── IMPLEMENTACION_INTERCEPTORES.md     # ⚙️ Guía de interceptores
└── GUIA_COMPLETA.md                    # 📚 Este archivo
```

---

## 📍 Componentes Principales

### 1. Anotaciones de Vínculo (Bindings)

Son la "llave" que une un **interceptor** a los **métodos** que debe interceptar.

```
┌─────────────────────────────────────────────────────────┐
│                  InterceptorBinding                     │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  @AuditMemo         → Operaciones de Mail             │
│  @AuditLog          → Auditoría General               │
│  @AuditSecurity     → Operaciones de Seguridad        │
│  @AuditPerformance  → Monitoreo de Rendimiento        │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

### 2. Interceptores

Son clases que interceptan invocaciones de métodos para ejecutar lógica adicional.

```
┌─────────────────────────────────────────────────────────┐
│                    Interceptor                          │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  @Interceptor                                           │
│  @AuditMemo (vinculado a anotación)                    │
│  class AuditoriaMailInterceptor {                       │
│      @AroundInvoke                                      │
│      public Object auditar(...) { ... }                 │
│  }                                                      │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

### 3. Servicios Auditados

Clases que utilizan las anotaciones para marcar métodos que deben interceptarse.

```
@ApplicationScoped
class MailService {
    @AuditMemo("Envío de correo")    ← Se intercepta
    public void enviarCorreo(...) { }
    
    public String estado() { }        ← No se intercepta
}
```

---

## 🔗 Flujo de Funcionamiento

### Paso 1: Marcar Método con Anotación
```java
@AuditMemo("Envío de correo")
public void enviarCorreo(String to, String subject, String body) {
    // Implementación
}
```

### Paso 2: CDI Detecta la Anotación
El contenedor CDI escanea y encuentra:
- El interceptor `@Interceptor @AuditMemo`
- El método `@AuditMemo` en `MailService`

### Paso 3: Vinculación Automática
CDI crea un vínculo entre el interceptor y el método.

### Paso 4: Interceptación en Tiempo de Ejecución
```
mailService.enviarCorreo(...)
    ↓
AuditoriaMailInterceptor.auditarMail() ANTES
    ↓
    Registra: inicio, clase, método, parámetros
    ↓
context.proceed()  ← Ejecuta el método original
    ↓
    Registra: duración, resultado, estado
    ↓
AuditoriaMailInterceptor.auditarMail() DESPUÉS
    ↓
Retorna resultado
```

---

## 📊 Matriz de Interceptores

| Binding | Interceptor | Propósito | Casos de Uso |
|---------|-------------|-----------|--------------|
| **@AuditMemo** | `AuditoriaMailInterceptor` | Correos | `enviarCorreo()`, `enviarCorreoMasivo()` |
| **@AuditLog** | `AuditLogInterceptor` | Auditoría general | `registrar()`, `asignarRolAdmin()` |
| **@AuditSecurity** | `AuditSecurityInterceptor` | Seguridad | `login()`, `cambiarPermisos()` |
| **@AuditPerformance** | `AuditPerformanceInterceptor` | Rendimiento | `buscar()`, `generarReporte()` |

---

## 🎯 Ejemplos de Uso

### Ejemplo 1: Auditoría Simple de Mail

```java
@AuditMemo("Envío de correo")
public boolean enviarCorreo(String to, String subject, String body) {
    // Lógica de envío
    return true;
}
```

**Resultado:**
```
=== AUDITORÍA MAIL - INICIO ===
Clase: MailService
Método: enviarCorreo
Parámetros: usuario@example.com, Asunto, Cuerpo
...
Estado: EXITOSO
Tiempo de ejecución: 5 ms
=== AUDITORÍA MAIL - FIN (OK) ===
```

### Ejemplo 2: Auditoría de Seguridad (Login)

```java
@AuditSecurity(
    operacion = "LOGIN",
    recurso = "cuenta_usuario",
    registrarParametros = false
)
public Usuario login(String email, String password) {
    // Autenticación
    return usuario;
}
```

**Características:**
- ✓ Registra: Usuario, IP, Timestamp
- ✗ No registra: Password (protegido)
- Diferencia acceso permitido/denegado

### Ejemplo 3: Monitoreo de Rendimiento

```java
@AuditPerformance(
    umbralMs = 500,
    registrarMemoria = true
)
public List<Usuario> buscar(String criteria) {
    // Búsqueda en BD
    return usuarios;
}
```

**Características:**
- ✓ Mide duración
- ✓ Registra memoria inicial y final
- ⚠️ Alerta si tiempo > umbral

### Ejemplo 4: Múltiples Interceptores

```java
@AuditLog(level = AuditLog.AuditLevel.CRITICAL)
@AuditSecurity(operacion = "PAGO")
@AuditPerformance(umbralMs = 2000)
public void procesarPago(Pago pago) {
    // Procesa el pago
}
```

**Cadena de ejecución:**
```
1. AuditLogInterceptor → registra
2. AuditSecurityInterceptor → registra
3. AuditPerformanceInterceptor → cronometra
4. MÉTODO ORIGINAL
5. AuditPerformanceInterceptor → duración
6. AuditSecurityInterceptor → estado
7. AuditLogInterceptor → fin
```

---

## 🚀 Cómo Ejecutar

### Compilar
```bash
./mvnw compile
```

### Ejecutar Main
```bash
./mvnw exec:java -Dexec.mainClass="Main"
```

### Modo Desarrollo Quarkus
```bash
./mvnw quarkus:dev
```

### Empaquetar
```bash
./mvnw package
java -jar target/quarkus-app/quarkus-run.jar
```

---

## 📋 Archivos Generados

### 1. **Anotaciones de Vínculo** (en `interceptor/`)
- `AuditMemo.java` - Para auditoría de correos
- `AuditLog.java` - Para auditoría general con niveles
- `AuditSecurity.java` - Para operaciones de seguridad
- `AuditPerformance.java` - Para monitoreo de rendimiento

### 2. **Interceptores** (en `interceptor/`)
- `AuditoriaMailInterceptor.java` - Intercepta @AuditMemo
- `AuditLogInterceptor.java` - Intercepta @AuditLog
- `AuditSecurityInterceptor.java` - Intercepta @AuditSecurity
- `AuditPerformanceInterceptor.java` - Intercepta @AuditPerformance

### 3. **Servicios** (en `mail/` y `service/`)
- `MailService.java` - Operaciones de correo
- `UsuarioService.java` - Gestión de usuarios
- `ReporteService.java` - Generación de reportes

### 4. **Aplicación Principal**
- `Main.java` - Demostraciones prácticas

### 5. **Documentación**
- `TALLER11.md` - Introducción al taller
- `ANOTACIONES_VINCULOS.md` - Guía de anotaciones
- `IMPLEMENTACION_INTERCEPTORES.md` - Guía de interceptores
- `GUIA_COMPLETA.md` - Este archivo

---

## 🧪 Pruebas Incluidas en Main

### Interceptores Individuales

```
1️⃣ @AuditMemo
   ├─ Envío simple
   ├─ Envío masivo
   └─ Error en operación

2️⃣ @AuditSecurity
   ├─ Login (sin registrar credenciales)
   └─ Cambio de permisos (CRITICAL)

3️⃣ @AuditLog
   ├─ Registro de usuario
   └─ Generación de reporte

4️⃣ @AuditPerformance
   ├─ Búsqueda rápida
   ├─ Reporte dentro de umbral
   └─ Reporte que excede umbral ⚠️
```

### Cadenas Combinadas

```
Cambio de permisos:
├─ @AuditLog (CRITICAL)
└─ @AuditSecurity (CAMBIO_PERMISOS)
```

---

## 💡 Conceptos Clave

### InterceptorBinding
Anotación meta-anotada con `@InterceptorBinding` que vincula interceptores a métodos.

### @AroundInvoke
Anotación que marca el método que ejecuta la lógica de interceptación (antes y después).

### InvocationContext
Contexto con información sobre la invocación:
- `getMethod()` - Método original
- `getTarget()` - Instancia del objeto
- `getParameters()` - Parámetros
- `proceed()` - Ejecuta el siguiente interceptor/método

### Cadena de Interceptores
Múltiples interceptores procesando una invocación en orden.

---

## 🔒 Buenas Prácticas de Seguridad

### 1. Proteger Datos Sensibles
```java
@AuditSecurity(
    registrarParametros = false  // No registra contraseñas
)
```

### 2. Usar Niveles Apropiados
```java
@AuditLog(level = AuditLog.AuditLevel.CRITICAL)
// Solo para operaciones críticas
```

### 3. Umbrales Realistas
```java
@AuditPerformance(umbralMs = 1000)  // No demasiado bajo
```

### 4. Auditoría de Acceso
```java
@AuditSecurity(operacion = "ACCESO", recurso = "datos_sensibles")
```

---

## 📈 Casos de Uso en Producción

### E-commerce
```java
@AuditLog(level = CRITICAL)
@AuditSecurity(operacion = "PAGO")
@AuditPerformance(umbralMs = 5000)
public void procesarPedido(Pedido pedido) { }
```

### Banca Digital
```java
@AuditSecurity(operacion = "TRANSFERENCIA", registrarParametros = false)
@AuditLog(level = CRITICAL)
public void transferencia(Cuenta origen, Cuenta destino, Monto monto) { }
```

### Analytics
```java
@AuditPerformance(umbralMs = 2000, registrarMemoria = true)
@AuditLog(level = INFO)
public List<Dato> generarReporte(Filtro filtro) { }
```

### Sistemas de Autorización
```java
@AuditSecurity(operacion = "CAMBIO_PERMISOS", recurso = "rol")
@AuditLog(level = CRITICAL)
public void asignarRol(Usuario usuario, Rol rol) { }
```

---

## 🔧 Extensiones Futuras

### 1. Persistencia de Auditoría
```java
@AuditLog
public void operacion() { }
// → Guardar en BD automáticamente
```

### 2. Alertas en Tiempo Real
```java
@AuditPerformance(umbralMs = 1000)
// → Notificar al equipo si se excede
```

### 3. Control de Acceso Basado en Auditoría
```java
@AuditSecurity(operacion = "ADMIN")
// → Denegar si no tiene permisos
```

### 4. Trazabilidad Distribuida
```java
// → Usar OpenTelemetry para rastrear entre servicios
```

---

## ✅ Checklist de Implementación

- [x] Crear anotaciones de vínculo (@AuditMemo, @AuditLog, @AuditSecurity, @AuditPerformance)
- [x] Implementar interceptores (@Interceptor + @AroundInvoke)
- [x] Vincular interceptores a anotaciones
- [x] Crear servicios que usen las anotaciones
- [x] Implementar lógica de interceptación
- [x] Crear clase Main con pruebas
- [x] Documentación completa
- [x] Ejemplos de uso
- [x] Guía de mejores prácticas

---

## 📚 Recursos Adicionales

### Documentación Incluida
- `ANOTACIONES_VINCULOS.md` - Detalles de cada binding
- `IMPLEMENTACION_INTERCEPTORES.md` - Detalles de cada interceptor

### Referencias Externas
- [Jakarta Interceptors Specification](https://jakarta.ee/specifications/interceptors/)
- [CDI Specification](https://jakarta.ee/specifications/cdi/)
- [Quarkus CDI Guide](https://quarkus.io/guides/cdi)

---

## 🎓 Lecciones Aprendidas

1. **Interceptores**: Mecanismo poderoso para auditoría y cross-cutting concerns
2. **Bindings**: Forma declarativa de vincular interceptores a métodos
3. **Cadenas**: Múltiples interceptores pueden procesarse en secuencia
4. **Seguridad**: Proteger datos sensibles en auditoría
5. **Rendimiento**: Monitoreo automático sin código en el método

---

## 🏆 Conclusión

El **Taller 11** demuestra cómo implementar una **cadena de interceptores robusta** en Quarkus/CDI para:
- ✓ Auditar operaciones automáticamente
- ✓ Registrar seguridad sin mezclar lógica
- ✓ Monitorear rendimiento transparentemente
- ✓ Mantener código limpio y mantenible

**¡Taller completado exitosamente!** 🎉
