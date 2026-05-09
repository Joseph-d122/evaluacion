public class User {
    private String nombre;
    private String atributoAdicional;

    public User(String nombre, String atributoAdicional) {
        this.nombre = nombre;
        this.atributoAdicional = atributoAdicional;
    }
    public String getNombre() { return nombre; }
    @Override
    public String toString() { return "nombre='" + nombre + "', rol='" + atributoAdicional + "'"; }
}