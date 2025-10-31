import java.text.DecimalFormat;

/**
 * Clase abstracta Cuenta: atributos y métodos comunes.
 */
public abstract class Cuenta {
    protected String numeroCuenta;
    protected String pin;
    protected double saldo;

    public Cuenta(String numeroCuenta, String pin, double saldoInicial) {
        this.numeroCuenta = numeroCuenta;
        this.pin = pin;
        this.saldo = saldoInicial;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public String getPin() {
        return pin;
    }

    public double getSaldo() {
        return saldo;
    }

    // Consultar saldo (formateado)
    public String consultarSaldoFormateado() {
        DecimalFormat df = new DecimalFormat("#,##0.00");
        return df.format(saldo);
    }

    // Depositar
    public void depositar(double monto) throws EntradaInvalidaException {
        if (monto <= 0) {
            throw new EntradaInvalidaException("El monto a depositar debe ser mayor que 0.");
        }
        saldo += monto;
    }

    // Retirar (puede lanzar SaldoInsuficienteException)
    public void retirar(double monto) throws SaldoInsuficienteException, EntradaInvalidaException {
        if (monto <= 0) {
            throw new EntradaInvalidaException("El monto a retirar debe ser mayor que 0.");
        }
        if (monto > saldo) {
            throw new SaldoInsuficienteException("Saldo insuficiente. Saldo actual: " + consultarSaldoFormateado());
        }
        saldo -= monto;
    }

    // Pagar servicio (misma validación que retirar)
    public void pagarServicio(double monto, String servicio) throws SaldoInsuficienteException, EntradaInvalidaException {
        if (monto <= 0) {
            throw new EntradaInvalidaException("El monto del pago debe ser mayor que 0.");
        }
        if (monto > saldo) {
            throw new SaldoInsuficienteException("Saldo insuficiente para pagar " + servicio + ". Saldo actual: " + consultarSaldoFormateado());
        }
        saldo -= monto;
    }
}
