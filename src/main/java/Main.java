import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;

@QuarkusMain
public class Main {
    public static void main(String[] args) {
        Quarkus.run(App.class, args);
    }

    public static class App implements QuarkusApplication {
        @Inject
        EventoService eventoService; 
        @Override
        public int run(String... args) {
            User usuario = new User("Joseph Condor", "Rol: auditor");
            Event evento = new Event(EventType.C, "Acceso no autorizado detectado");
            eventoService.registrarEvento(usuario, evento);

            return 0;
        }
    }
}
