import jakarta.annotation.Priority;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.Interceptor;
import jakarta.interceptor.InvocationContext;

@AuditSMS
@Interceptor
@Priority(3)
public class AuditoriaSmsInterceptor {
    @AroundInvoke
    public Object auditar(InvocationContext context) throws Exception {
        User u = (User) context.getParameters()[0];
        System.out.println("Auditando evento por SMS " + u.getNombre());
        return context.proceed();
    }
}
