import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Clase CajeroGUI: maneja ventanas independientes para cada pantalla dependiendo del flujo de las acciones del usuario.
 */
public class CajeroGUI {

    public CajeroGUI() {
        // Muestra la ventana de Bienvenida(Welcome)
        SwingUtilities.invokeLater(() -> new WelcomeFrame());
    }

    // ---------------------- Welcome Frame ----------------------
    private class WelcomeFrame extends JFrame {
        public WelcomeFrame() {
            setTitle("Banco XYZ - Cajero Automático");
            setSize(450, 220);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            JLabel lblBienvenido = new JLabel("Bienvenido al Cajero Automático del Banco XYZ", SwingConstants.CENTER);
            lblBienvenido.setFont(new Font("SansSerif", Font.BOLD, 16));
            add(lblBienvenido, BorderLayout.NORTH);

            JPanel botonesPanel = new JPanel();
            JButton btnIniciar = new JButton("Iniciar Sesión");
            JButton btnRegistrar = new JButton("Registrarse");
            botonesPanel.add(btnIniciar);
            botonesPanel.add(btnRegistrar);
            add(botonesPanel, BorderLayout.CENTER);

            // Eventos(Botones de acción)
            btnRegistrar.addActionListener(e -> {
                new RegisterFrame(this);
                this.setVisible(false);
            });

            btnIniciar.addActionListener(e -> {
                new LoginFrame(this);
                this.setVisible(false);
            });

            setVisible(true);
        }
    }

    // ---------------------- Register Frame ----------------------
    private class RegisterFrame extends JFrame {
        public RegisterFrame(JFrame parent) {
            setTitle("Registro de Nuevo Usuario - Banco XYZ");
            setSize(400, 420);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6,6,6,6);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel lblNombre = new JLabel("Nombre completo:");
            JTextField tfNombre = new JTextField();

            JLabel lblIdentificacion = new JLabel("Número de identificación:");
            JTextField tfIdentificacion = new JTextField();

            JLabel lblPin = new JLabel("PIN (4 dígitos):");
            JPasswordField pfPin = new JPasswordField();

            JLabel lblTipo = new JLabel("Tipo de cuenta:");
            String[] tipos = {"Ahorros", "Corriente"};
            JComboBox<String> cbTipo = new JComboBox<>(tipos);

            JLabel lblSaldo = new JLabel("Saldo inicial:");
            JTextField tfSaldo = new JTextField();

            JButton btnRegistrar = new JButton("Registrar");
            JButton btnCancelar = new JButton("Cancelar");

            gbc.gridx = 0; gbc.gridy = 0; add(lblNombre, gbc);
            gbc.gridx = 1; gbc.gridy = 0; add(tfNombre, gbc);

            gbc.gridx = 0; gbc.gridy = 1; add(lblIdentificacion, gbc);
            gbc.gridx = 1; gbc.gridy = 1; add(tfIdentificacion, gbc);

            gbc.gridx = 0; gbc.gridy = 2; add(lblPin, gbc);
            gbc.gridx = 1; gbc.gridy = 2; add(pfPin, gbc);

            gbc.gridx = 0; gbc.gridy = 3; add(lblTipo, gbc);
            gbc.gridx = 1; gbc.gridy = 3; add(cbTipo, gbc);

            gbc.gridx = 0; gbc.gridy = 4; add(lblSaldo, gbc);
            gbc.gridx = 1; gbc.gridy = 4; add(tfSaldo, gbc);

            JPanel botones = new JPanel();
            botones.add(btnRegistrar);
            botones.add(btnCancelar);
            gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2; add(botones, gbc);

            // Evento del boton registrar(Frame nueva)
            btnRegistrar.addActionListener(ev -> {
                try {
                    String nombre = tfNombre.getText().trim();
                    String identificacion = tfIdentificacion.getText().trim();
                    String pin = new String(pfPin.getPassword()).trim();
                    String tipo = (String) cbTipo.getSelectedItem();
                    String saldoStr = tfSaldo.getText().trim();

                    if (nombre.isEmpty() || identificacion.isEmpty() || pin.isEmpty() || saldoStr.isEmpty()) {
                        throw new EntradaInvalidaException("Todos los campos son obligatorios.");
                    }

                    // Validar nombre: solo letras y espacios
                    if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
                        throw new EntradaInvalidaException("El nombre solo puede contener letras y espacios.");
                    }

                    // Validar identificación: solo números
                    if (!identificacion.matches("\\d+")) {
                        throw new EntradaInvalidaException("El número de identificación solo puede contener dígitos numéricos.");
                    }

                    // Validar formato de PIN (exactamente 4 dígitos)
                    if (!pin.matches("\\d{4}")) {
                        throw new EntradaInvalidaException("El PIN debe tener exactamente 4 dígitos numéricos.");
                    }

                    // Validar si ya existe un usuario con esa identificación
                    if (Banco.existeUsuarioPorIdentificacion(identificacion)) {
                        throw new EntradaInvalidaException("Ya existe un usuario con ese número de identificación. Cada usuario solo puede tener una cuenta.");
                    }

                    double saldoInicial;
                    try {
                        saldoInicial = Double.parseDouble(saldoStr);
                    } catch (NumberFormatException ex) {
                        throw new EntradaInvalidaException("Saldo inicial debe ser un número válido.");
                    }

                    if (saldoInicial < 0) {
                        throw new EntradaInvalidaException("El saldo inicial no puede ser negativo.");
                    }

                    // Generar número de cuenta único de 6 dígitos
                    String numeroCuenta = Banco.generarNumeroCuenta();

                    // Crear cuenta según tipo
                    Cuenta cuenta;
                    if ("Ahorros".equals(tipo)) {
                        cuenta = new CuentaAhorros(numeroCuenta, pin, saldoInicial);
                    } else {
                        cuenta = new CuentaCorriente(numeroCuenta, pin, saldoInicial);
                    }

                    Usuario nuevo = new Usuario(nombre, identificacion, cuenta);
                    Banco.agregarUsuario(nuevo);

                    // Mostrar número de cuenta al usuario
                    JOptionPane.showMessageDialog(this,
                            "Registro exitoso.\nSu número de cuenta asignado es: " + numeroCuenta,
                            "Registro exitoso",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Volver al WelcomeFrame
                    this.dispose();
                    new WelcomeFrame();

                } catch (EntradaInvalidaException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de entrada", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            btnCancelar.addActionListener(ev -> {
                this.dispose();
                new WelcomeFrame();
            });

            setVisible(true);
        }
    }


    // ---------------------- Login Frame ----------------------
    private class LoginFrame extends JFrame {
        public LoginFrame(JFrame parent) {
            setTitle("Iniciar Sesión - Banco XYZ");
            setSize(380, 220);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6,6,6,6);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel lblCuenta = new JLabel("Número de cuenta:");
            JTextField tfCuenta = new JTextField();

            JLabel lblPin = new JLabel("PIN:");
            JPasswordField pfPin = new JPasswordField();

            JButton btnIngresar = new JButton("Ingresar");
            JButton btnCancelar = new JButton("Cancelar");

            gbc.gridx = 0; gbc.gridy = 0; add(lblCuenta, gbc);
            gbc.gridx = 1; gbc.gridy = 0; add(tfCuenta, gbc);

            gbc.gridx = 0; gbc.gridy = 1; add(lblPin, gbc);
            gbc.gridx = 1; gbc.gridy = 1; add(pfPin, gbc);

            JPanel botones = new JPanel();
            botones.add(btnIngresar);
            botones.add(btnCancelar);
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; add(botones, gbc);

            // Eventos(Botones de acción)
            btnIngresar.addActionListener(ev -> {
                try {
                    String numero = tfCuenta.getText().trim();
                    String pin = new String(pfPin.getPassword()).trim();

                    if (numero.isEmpty() || pin.isEmpty()) {
                        throw new EntradaInvalidaException("Debe ingresar número de cuenta y PIN.");
                    }

                    Usuario usuario = Banco.autenticar(numero, pin);
                    // Abrir menu principal pasando el usuario autenticado
                    new MenuFrame(usuario);
                    this.dispose();

                } catch (EntradaInvalidaException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Entrada inválida", JOptionPane.ERROR_MESSAGE);
                } catch (AutenticacionException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de autenticación", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            btnCancelar.addActionListener(ev -> {
                this.dispose();
                new WelcomeFrame();
            });

            setVisible(true);
        }
    }

    // ---------------------- Menu Frame ----------------------
    private class MenuFrame extends JFrame {
        private Usuario usuario;

        public MenuFrame(Usuario usuario) {
            this.usuario = usuario;
            setTitle("Menú Principal - Banco XYZ");
            setSize(500, 350);
            setLocationRelativeTo(null);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new BorderLayout());

            // Panel superior con saludo y tipo de cuenta
            JPanel panelSuperior = new JPanel();
            panelSuperior.setLayout(new GridLayout(3, 1));
            panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel lblHola = new JLabel("Hola, " + usuario.getNombre(), SwingConstants.CENTER);
            lblHola.setFont(new Font("SansSerif", Font.BOLD, 16));

            String tipoCuenta = (usuario.getCuenta() instanceof CuentaAhorros)
                    ? "Cuenta de Ahorros"
                    : "Cuenta Corriente";

            JLabel lblTipoCuenta = new JLabel(
                    "Tipo: " + tipoCuenta + " - " + usuario.getCuenta().getNumeroCuenta(),
                    SwingConstants.CENTER);
            lblTipoCuenta.setFont(new Font("SansSerif", Font.PLAIN, 14));

            JLabel lblOperacion = new JLabel("Seleccione una operación:", SwingConstants.CENTER);
            lblOperacion.setFont(new Font("SansSerif", Font.BOLD, 15));

            panelSuperior.add(lblHola);
            panelSuperior.add(lblTipoCuenta);
            panelSuperior.add(lblOperacion);
            add(panelSuperior, BorderLayout.NORTH);

            // Resto del código del menú (botones, acciones, etc.)
            JPanel botones = new JPanel();
            botones.setLayout(new GridLayout(3, 2, 10, 10));
            JButton btnConsultar = new JButton("Consultar saldo");
            JButton btnDepositar = new JButton("Depositar dinero");
            JButton btnRetirar = new JButton("Retirar dinero");
            JButton btnPagar = new JButton("Pagar servicios");
            JButton btnSalir = new JButton("Cerrar sesión");

            botones.add(btnConsultar);
            botones.add(btnDepositar);
            botones.add(btnRetirar);
            botones.add(btnPagar);
            botones.add(btnSalir);
            add(botones, BorderLayout.CENTER);

            // Eventos de botones
            btnConsultar.addActionListener(ev -> new ConsultaFrame(this, usuario));
            btnDepositar.addActionListener(ev -> new DepositoFrame(this, usuario));
            btnRetirar.addActionListener(ev -> new RetiroFrame(this, usuario));
            btnPagar.addActionListener(ev -> new PagoFrame(this, usuario));

            btnSalir.addActionListener(ev -> {
                JOptionPane.showMessageDialog(this,
                        "Su sesión fue cerrada.\nGracias por utilizar los servicios del Banco XYZ.",
                        "Sesión cerrada",
                        JOptionPane.INFORMATION_MESSAGE);
                this.dispose();
                new WelcomeFrame();
            });

            setVisible(true);
        }
    }

    // ---------------------- Consulta Frame ----------------------
    private class ConsultaFrame extends JFrame {
        public ConsultaFrame(JFrame parent, Usuario usuario) {
            setTitle("Consultar Saldo - Cuenta: " + usuario.getCuenta().getNumeroCuenta());
            setSize(400, 180);
            setLocationRelativeTo(parent);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new BorderLayout(10,10));

            JLabel lblTitulo = new JLabel("Saldo actual", SwingConstants.CENTER);
            lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 14));
            add(lblTitulo, BorderLayout.NORTH);

            JLabel lblSaldo = new JLabel("$ " + usuario.getCuenta().consultarSaldoFormateado(), SwingConstants.CENTER);
            lblSaldo.setFont(new Font("Monospaced", Font.PLAIN, 18));
            add(lblSaldo, BorderLayout.CENTER);

            JButton btnCerrar = new JButton("Cerrar");
            btnCerrar.addActionListener(e -> this.dispose());
            add(btnCerrar, BorderLayout.SOUTH);

            setVisible(true);
        }
    }

    // ---------------------- Deposito Frame ----------------------
    private class DepositoFrame extends JFrame {
        public DepositoFrame(JFrame parent, Usuario usuario) {
            setTitle("Depositar - Cuenta: " + usuario.getCuenta().getNumeroCuenta());
            setSize(400, 200);
            setLocationRelativeTo(parent);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6,6,6,6);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel lblMonto = new JLabel("Monto a depositar:");
            JTextField tfMonto = new JTextField();

            JButton btnAceptar = new JButton("Depositar");
            JButton btnCancelar = new JButton("Cancelar");

            gbc.gridx = 0; gbc.gridy = 0; add(lblMonto, gbc);
            gbc.gridx = 1; gbc.gridy = 0; add(tfMonto, gbc);
            JPanel botones = new JPanel();
            botones.add(btnAceptar);
            botones.add(btnCancelar);
            gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; add(botones, gbc);

            btnAceptar.addActionListener(e -> {
                try {
                    String montoStr = tfMonto.getText().trim();
                    if (montoStr.isEmpty()) throw new EntradaInvalidaException("Debe ingresar un monto.");
                    double monto;
                    try {
                        monto = Double.parseDouble(montoStr);
                    } catch (NumberFormatException ex) {
                        throw new EntradaInvalidaException("Monto inválido.");
                    }

                    usuario.getCuenta().depositar(monto);
                    JOptionPane.showMessageDialog(this,
                            "Depósito exitoso. Saldo actual: $ " + usuario.getCuenta().consultarSaldoFormateado(),
                            "Depósito",
                            JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();

                } catch (EntradaInvalidaException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            btnCancelar.addActionListener(e -> this.dispose());
            setVisible(true);
        }
    }

    // ---------------------- Retiro Frame ----------------------
    private class RetiroFrame extends JFrame {
        public RetiroFrame(JFrame parent, Usuario usuario) {
            setTitle("Retirar - Cuenta: " + usuario.getCuenta().getNumeroCuenta());
            setSize(420, 220);
            setLocationRelativeTo(parent);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6,6,6,6);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel lblMonto = new JLabel("Monto a retirar:");
            JTextField tfMonto = new JTextField();

            JButton btnAceptar = new JButton("Retirar");
            JButton btnCancelar = new JButton("Cancelar");

            gbc.gridx = 0; gbc.gridy = 0; add(lblMonto, gbc);
            gbc.gridx = 1; gbc.gridy = 0; add(tfMonto, gbc);
            JPanel botones = new JPanel();
            botones.add(btnAceptar);
            botones.add(btnCancelar);
            gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; add(botones, gbc);

            btnAceptar.addActionListener(e -> {
                try {
                    String montoStr = tfMonto.getText().trim();
                    if (montoStr.isEmpty()) throw new EntradaInvalidaException("Debe ingresar un monto.");
                    double monto;
                    try {
                        monto = Double.parseDouble(montoStr);
                    } catch (NumberFormatException ex) {
                        throw new EntradaInvalidaException("Monto inválido.");
                    }

                    usuario.getCuenta().retirar(monto);
                    JOptionPane.showMessageDialog(this,
                            "Retiro exitoso. Saldo actual: $ " + usuario.getCuenta().consultarSaldoFormateado(),
                            "Retiro",
                            JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();

                } catch (EntradaInvalidaException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de entrada", JOptionPane.ERROR_MESSAGE);
                } catch (SaldoInsuficienteException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Saldo insuficiente", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            btnCancelar.addActionListener(e -> this.dispose());
            setVisible(true);
        }
    }

    // ---------------------- Pago Frame ----------------------
    private class PagoFrame extends JFrame {
        public PagoFrame(JFrame parent, Usuario usuario) {
            setTitle("Pago de Servicios - Cuenta: " + usuario.getCuenta().getNumeroCuenta());
            setSize(450, 250);
            setLocationRelativeTo(parent);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(6,6,6,6);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            JLabel lblServicio = new JLabel("Servicio:");
            String[] servicios = {"Agua", "Luz", "Internet", "Telefonía"};
            JComboBox<String> cbServicios = new JComboBox<>(servicios);

            JLabel lblMonto = new JLabel("Monto a pagar:");
            JTextField tfMonto = new JTextField();

            JButton btnPagar = new JButton("Pagar");
            JButton btnCancelar = new JButton("Cancelar");

            gbc.gridx = 0; gbc.gridy = 0; add(lblServicio, gbc);
            gbc.gridx = 1; gbc.gridy = 0; add(cbServicios, gbc);

            gbc.gridx = 0; gbc.gridy = 1; add(lblMonto, gbc);
            gbc.gridx = 1; gbc.gridy = 1; add(tfMonto, gbc);

            JPanel botones = new JPanel();
            botones.add(btnPagar);
            botones.add(btnCancelar);
            gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; add(botones, gbc);

            btnPagar.addActionListener(e -> {
                try {
                    String servicio = (String) cbServicios.getSelectedItem();
                    String montoStr = tfMonto.getText().trim();
                    if (montoStr.isEmpty()) throw new EntradaInvalidaException("Debe ingresar un monto.");
                    double monto;
                    try {
                        monto = Double.parseDouble(montoStr);
                    } catch (NumberFormatException ex) {
                        throw new EntradaInvalidaException("Monto inválido.");
                    }

                    usuario.getCuenta().pagarServicio(monto, servicio);
                    JOptionPane.showMessageDialog(this,
                            "Pago de " + servicio + " realizado.\nSaldo actual: $ " + usuario.getCuenta().consultarSaldoFormateado(),
                            "Pago exitoso",
                            JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();

                } catch (EntradaInvalidaException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error de entrada", JOptionPane.ERROR_MESSAGE);
                } catch (SaldoInsuficienteException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Saldo insuficiente", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error inesperado: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            btnCancelar.addActionListener(e -> this.dispose());
            setVisible(true);
        }
    }
}
