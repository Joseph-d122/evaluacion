public class Event {
    private EventType tipo;
    private String detalle; 

    public Event() {
    }

    public Event(EventType tipo, String detalle) {
        this.tipo = tipo;
        this.detalle = detalle;
    }

    public EventType getTipo() {
        return tipo;
    }

    public void setTipo(EventType tipo) {
        this.tipo = tipo;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    @Override
    public String toString() {
        return "Event{tipo='" + tipo + "', detalle='" + detalle + "'}";
    }
}
