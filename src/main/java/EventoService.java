import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped 
public class EventoService {

    @AuditMail
    @AuditSMS
    @AuditMemo
    public void registrarEvento(User usuario, Event evento) {
        System.out.println(">>> LÓGICA DE NEGOCIO: Registrando evento en la base de datos...");
        System.out.println("Usuario: " + usuario.getNombre() + " - Detalle: " + evento.getDetalle());
    }
}
