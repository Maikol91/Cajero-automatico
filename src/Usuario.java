/**
 * Clase Usuario: representa un cliente con nombre y una cuenta asociada.
 */
public class Usuario {
    private String nombre;
    private String identificacion;
    private Cuenta cuenta;

    public Usuario(String nombre, String identificacion, Cuenta cuenta) {
        this.nombre = nombre;
        this.identificacion = identificacion;
        this.cuenta = cuenta;
    }

    public String getNombre() {
        return nombre;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public Cuenta getCuenta() {
        return cuenta;
    }
}

