import jakarta.annotation.Priority;
import jakarta.interceptor.*;

@AuditMemo
@Interceptor
@Priority(2)
public class AuditoriaMemoInterceptor {
    @AroundInvoke
    public Object auditar(InvocationContext context) throws Exception {
        User u = (User) context.getParameters()[0];
        System.out.println("Auditando evento por Memo " + u.getNombre());
        return context.proceed();
    }
}