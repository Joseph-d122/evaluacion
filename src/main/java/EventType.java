public enum EventType {
    C("Crítico"),
    N("Normal");

    private final String descripcion;
    EventType(String descripcion) { this.descripcion = descripcion; }
    public String getDescripcion() { return descripcion; }
}