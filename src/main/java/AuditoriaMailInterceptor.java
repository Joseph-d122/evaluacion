import jakarta.annotation.Priority;
import jakarta.interceptor.*;

@AuditMail
@Interceptor
@Priority(1)
public class AuditoriaMailInterceptor {
    @AroundInvoke
    public Object auditar(InvocationContext context) throws Exception {
        User u = (User) context.getParameters()[0];
        System.out.println("Auditando evento por Mail " + u.getNombre());
        return context.proceed();
    }
}
