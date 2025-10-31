import java.util.ArrayList;
import java.util.Random;

/**
 * Clase Banco: almacena en un ArrayList<> el usuario para hacer la simulacion de una base de datos, registro y autenticación, tambien adicional se utiliza la libreria ramdom, para generar un numero aleatorio para el numero de cuenta su maximo es de 6 digitos de longuitud.
 */
public class Banco {
    private static ArrayList<Usuario> listaUsuarios = new ArrayList<>();
    private static Random random = new Random();

    // Agrega un usuario
    public static void agregarUsuario(Usuario u) {
        listaUsuarios.add(u);
    }

    // Autenticar por número de cuenta y PIN: devuelve el Usuario o lanza AutenticacionException
    public static Usuario autenticar(String numeroCuenta, String pin) throws AutenticacionException {
        for (Usuario u : listaUsuarios) {
            if (u.getCuenta().getNumeroCuenta().equals(numeroCuenta) && u.getCuenta().getPin().equals(pin)) {
                return u;
            }
        }
        throw new AutenticacionException("Número de cuenta o PIN incorrecto.");
    }

    // Buscar usuario por número de cuenta retorna null si no exite el usuario
    public static Usuario buscarPorNumeroCuenta(String numeroCuenta) {
        for (Usuario u : listaUsuarios) {
            if (u.getCuenta().getNumeroCuenta().equals(numeroCuenta)) {
                return u;
            }
        }
        return null;
    }

    // Genera un número de cuenta aleatorio de 6 dígitos y asegura que no exista ya
    public static String generarNumeroCuenta() {
        long inicio = 10_000_0L; // 6 dígitos mínimo (100,000)
        long fin = 99_999_9L;   // máximo 6 dígitos
        String candidato;
        do {
            long numero = inicio + (Math.abs(random.nextLong()) % (fin - inicio + 1));
            candidato = String.valueOf(numero);
        } while (buscarPorNumeroCuenta(candidato) != null);
        return candidato;
    }

    // Método utilitario para saber si ya hay usuarios
    public static boolean hayUsuarios() {
        return !listaUsuarios.isEmpty();
    }

    // Verifica si ya existe un usuario con el mismo número de identificación
    public static boolean existeUsuarioPorIdentificacion(String identificacion) {
        for (Usuario u : listaUsuarios) {
            if (u.getIdentificacion().equals(identificacion)) {
                return true;
            }
        }
        return false;
    }

}
