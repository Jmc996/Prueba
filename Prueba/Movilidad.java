import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class Movilidad {

    private String[] usuarioActivo = null;

    public Movilidad() {
        crearArchivosSiNoExisten();
        crearEstacionesSiNoExisten();
    }

    private double[] generarCoordenadasZaragoza(double radioKm) {
        double latCentro = 41.6561;
        double lonCentro = -0.8773;

        double radioLat = radioKm / 111.32;
        double radioLon = radioKm / 85.12;

        Random rand = new Random();
        double latOffset = (rand.nextDouble() * 2 - 1) * radioLat;
        double lonOffset = (rand.nextDouble() * 2 - 1) * radioLon;

        return new double[]{latCentro + latOffset, lonCentro + lonOffset};
    }

    private void crearArchivosSiNoExisten() {
        try {
            new File("usuarios.txt").createNewFile();
            new File("vehiculos.txt").createNewFile();
            new File("trabajadores.txt").createNewFile();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error creando archivos: " + e.getMessage());
        }
    }

    private void crearEstacionesSiNoExisten() {
        File estacionesFile = new File("estaciones.txt");
        if (!estacionesFile.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(estacionesFile))) {
                for (int i = 1; i <= 10; i++) {
                    double[] coords = generarCoordenadasZaragoza(10.0);
                    bw.write("Estacion" + i + ";" + coords[0] + ";" + coords[1]);
                    bw.newLine();
                }
                JOptionPane.showMessageDialog(null, "Se han creado 10 estaciones aleatorias en Zaragoza.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error creando estaciones: " + e.getMessage());
            }
        }
    }

    private String[][] cargarEstaciones() {
        ArrayList<String[]> estaciones = new ArrayList<>();

        try (Scanner sc = new Scanner(new File("estaciones.txt"))) {
            while (sc.hasNextLine()) {
                String[] linea = sc.nextLine().split(";");
                if (linea.length == 3) { // Aseguramos que la línea tenga los elementos esperados
                    estaciones.add(linea);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error cargando estaciones: " + e.getMessage());
        }

        return estaciones.toArray(new String[0][0]);
    }

    public void crearMenuAdministrador() {
        JFrame frame = new JFrame("Sistema de Movilidad Sostenible");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);

        JPanel panel = new JPanel(new GridLayout(8, 1));

        JButton btnCrearUsuario = new JButton("Crear Usuario");
        JButton btnCrearVehiculo = new JButton("Crear Vehículo");
        JButton btnCrearTrabajador = new JButton("Crear Trabajador");
        JButton btnVerUsuarios = new JButton("Ver Usuarios");
        JButton btnVerVehiculos = new JButton("Ver Vehículos");
        JButton btnVerTrabajadores = new JButton("Ver Trabajadores");
        JButton btnVerEstaciones = new JButton("Ver Estaciones");

        btnCrearUsuario.addActionListener(e -> crearUsuario());
        btnCrearVehiculo.addActionListener(e -> crearVehiculo());
        btnCrearTrabajador.addActionListener(e -> crearTrabajador());
        btnVerUsuarios.addActionListener(e -> mostrarYEditarTabla("usuarios.txt", new String[]{"Nombre", "DNI", "Email", "Saldo", "Tipo"}));
        btnVerVehiculos.addActionListener(e -> mostrarYEditarTabla("vehiculos.txt", new String[]{"Tipo", "ID", "Batería", "Averiado", "Latitud", "Longitud"}));
        btnVerTrabajadores.addActionListener(e -> mostrarYEditarTabla("trabajadores.txt", new String[]{"Nombre", "DNI", "Email", "Tipo"}));
        btnVerEstaciones.addActionListener(e -> mostrarEstacionesDisponibles());

        panel.add(new JLabel("Bienvenido al sistema de movilidad"));
        panel.add(btnCrearUsuario);
        panel.add(btnCrearVehiculo);
        panel.add(btnCrearTrabajador);
        panel.add(btnVerUsuarios);
        panel.add(btnVerVehiculos);
        panel.add(btnVerTrabajadores);
        panel.add(btnVerEstaciones);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    public void crearMenuCliente() {
        JFrame frame = new JFrame("Menú Cliente");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel(new GridLayout(4, 1)); // Ahora 4 filas

        JButton btnVerVehiculos = new JButton("Ver Vehículos Disponibles");
        JButton btnVerEstaciones = new JButton("Ver Estaciones Disponibles");
        JButton btnGenerarViaje = new JButton("Generar Viaje");
        JButton btnIngresarDinero = new JButton("Ingresar dinero");
        JButton btnHistorial = new JButton("Historial");

        btnVerVehiculos.addActionListener(e -> mostrarVehiculosDisponibles());
        btnVerEstaciones.addActionListener(e -> mostrarEstacionesDisponibles());
        btnGenerarViaje.addActionListener(e -> generarViaje());
        btnIngresarDinero.addActionListener(e -> mostrarDialogoIngresoDinero());
        btnHistorial.addActionListener(e -> mostrarHistorial());

        panel.add(new JLabel("Bienvenido, Cliente"));
        panel.add(btnVerVehiculos);
        panel.add(btnVerEstaciones);
        panel.add(btnGenerarViaje);
        panel.add(btnIngresarDinero);
        panel.add(btnHistorial);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    private void mostrarDialogoIngresoDinero() {
        if (usuarioActivo == null) {
            JOptionPane.showMessageDialog(null, "No hay un usuario activo.");
            return;
        }

        String input = JOptionPane.showInputDialog("Ingrese el monto a añadir (€):");
        if (input == null) return;

        try {
            double cantidad = Double.parseDouble(input);
            if (cantidad <= 0) {
                JOptionPane.showMessageDialog(null, "La cantidad debe ser mayor a 0.");
                return;
            }

            double saldoAntes = Double.parseDouble(usuarioActivo[3]);

            aumentarSaldoUsuario(cantidad);

            double saldoDespues = Double.parseDouble(usuarioActivo[3]);

            JOptionPane.showMessageDialog(null,
                "Ingreso realizado con éxito.\n" +
                "Saldo anterior: " + String.format("%.2f", saldoAntes) + " €\n" +
                "Cantidad ingresada: " + String.format("%.2f", cantidad) + " €\n" +
                "Saldo actual: " + String.format("%.2f", saldoDespues) + " €"
            );
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Por favor ingresa un número válido.");
        }
    }

    private void descontarSaldoUsuario(double precio) {
        if (usuarioActivo == null) {
            JOptionPane.showMessageDialog(null, "No hay usuario seleccionado para cobrar.");
            return;
        }

        double saldoActual = Double.parseDouble(usuarioActivo[3]);
        if (saldoActual < precio) {
            JOptionPane.showMessageDialog(null, "Saldo insuficiente para realizar el viaje.");
            return;
        }

        // Descontar el saldo
        saldoActual -= precio;
        usuarioActivo[3] = String.valueOf(saldoActual);

        // Actualizar el archivo
        ArrayList<String[]> todosUsuarios = new ArrayList<>();

        // Leer todos los usuarios
        try (Scanner sc = new Scanner(new File("usuarios.txt"))) {
            while (sc.hasNextLine()) {
                String[] linea = sc.nextLine().split(";");
                if (linea.length == 5) {
                    todosUsuarios.add(linea);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error leyendo usuarios: " + e.getMessage());
            return;
        }

        // Actualizar el saldo del usuario activo en la lista
        for (String[] usuario : todosUsuarios) {
            if (usuario[1].equals(usuarioActivo[1])) { // Comparar por DNI
                usuario[3] = usuarioActivo[3]; // Actualizar saldo
                break;
            }
        }

        // Escribir todos los usuarios de nuevo
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("usuarios.txt", false))) {
            for (String[] usuario : todosUsuarios) {
                bw.write(String.join(";", usuario));
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error actualizando usuarios: " + e.getMessage());
        }
    }

    private void aumentarSaldoUsuario(double cantidad) {
        if (usuarioActivo == null) {
            JOptionPane.showMessageDialog(null, "No hay usuario seleccionado para acreditar saldo.");
            return;
        }

        double saldoActual = Double.parseDouble(usuarioActivo[3]);
        saldoActual += cantidad;
        usuarioActivo[3] = String.valueOf(saldoActual);

        ArrayList<String[]> todosUsuarios = new ArrayList<>();

        try (Scanner sc = new Scanner(new File("usuarios.txt"))) {
            while (sc.hasNextLine()) {
                String[] linea = sc.nextLine().split(";");
                if (linea.length == 5) {
                    todosUsuarios.add(linea);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error leyendo usuarios: " + e.getMessage());
            return;
        }

        for (String[] usuario : todosUsuarios) {
            if (usuario[1].equals(usuarioActivo[1])) {
                usuario[3] = usuarioActivo[3];
                break;
            }
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("usuarios.txt", false))) {
            for (String[] usuario : todosUsuarios) {
                bw.write(String.join(";", usuario));
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error actualizando usuarios: " + e.getMessage());
        }
    }

    private int contarBicisOPatinetesEnEstacion(String estacion, String[][] estaciones) {
        int contador = 0;
        try (Scanner sc = new Scanner(new File("vehiculos.txt"))) {
            while (sc.hasNextLine()) {
                String[] datos = sc.nextLine().split(";");
                if (datos.length >= 6) {
                    String tipoVehiculo = datos[0];
                    double latVeh = Double.parseDouble(datos[4]);
                    double lonVeh = Double.parseDouble(datos[5]);

                    for (String[] est : estaciones) {
                        if (est[0].equals(estacion)) {
                            double latEst = Double.parseDouble(est[1]);
                            double lonEst = Double.parseDouble(est[2]);
                            if (coordenadasDentroDelMargen(latVeh, lonVeh, latEst, lonEst, 0.0005)) {
                                if (tipoVehiculo.equals("Bicicleta") || tipoVehiculo.equals("Patinete")) {
                                    contador++;
                                }
                            }
                            break; // Ya hemos encontrado la estación, no necesitamos seguir
                        }
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error leyendo vehículos: " + e.getMessage());
        }
        return contador;
    }

    private boolean coordenadasDentroDelMargen(double lat1, double lon1, double lat2, double lon2, double margen) {
        return Math.abs(lat1 - lat2) <= margen && Math.abs(lon1 - lon2) <= margen;
    }

    private void generarViaje() {
        ArrayList<String[]> vehiculosDisponibles = new ArrayList<>();

        try (Scanner sc = new Scanner(new File("vehiculos.txt"))) {
            while (sc.hasNextLine()) {
                String[] linea = sc.nextLine().split(";");
                if (linea.length == 6) {
                    if (!linea[3].equalsIgnoreCase("true") && Double.parseDouble(linea[2]) > 20) {
                        vehiculosDisponibles.add(linea);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Línea ignorada (formato incorrecto): " + Arrays.toString(linea));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error leyendo vehículos: " + e.getMessage());
            return;
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Error general: " + ex.getMessage());
            return;
        }

        if (vehiculosDisponibles.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay vehículos disponibles para viaje.");
            return;
        }

        // Aquí seguiría el código para crear el ComboBox y el JDialog
        // Crear el comboBox de vehículos disponibles
        String[] opciones = new String[vehiculosDisponibles.size()];
        for (int i = 0; i < vehiculosDisponibles.size(); i++) {
            String[] v = vehiculosDisponibles.get(i);
            opciones[i] = v[0] + " ID: " + v[1] + " (Batería: " + v[2] + "%)";
        }

        JComboBox<String> comboVehiculos = new JComboBox<>(opciones);

        // Crear el panel del dialogo
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("Selecciona un vehículo:"), BorderLayout.NORTH);
        panel.add(comboVehiculos, BorderLayout.CENTER);

        int resultado = JOptionPane.showConfirmDialog(null, panel, "Generar Viaje", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (resultado == JOptionPane.OK_OPTION) {
            int seleccionado = comboVehiculos.getSelectedIndex();
            String[] vehiculoElegido = vehiculosDisponibles.get(seleccionado);

            if (vehiculoElegido[0].equalsIgnoreCase("Bicicleta") || vehiculoElegido[0].equalsIgnoreCase("Patinete")) {
                moverVehiculoAEstacion(vehiculoElegido);
            } else if (vehiculoElegido[0].equalsIgnoreCase("Moto pequeña")) {
                hacerViajeMoto(vehiculoElegido);
            }else if (vehiculoElegido[0].equalsIgnoreCase("Moto grande")) {
                hacerViajeMoto(vehiculoElegido);
            }
        }

    }

    private void hacerViajeMoto(String[] vehiculo) {
        String tipoVehiculo = vehiculo[0]; // "MotoPequeña" o "MotoGrande"
        String idVehiculo = vehiculo[1];

        String kmStr = JOptionPane.showInputDialog(null, "¿Cuántos km deseas recorrer?", "Introducir kilómetros", JOptionPane.QUESTION_MESSAGE);

        if (kmStr == null || kmStr.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Operación cancelada.");
            return;
        }

        double km;
        try {
            km = Double.parseDouble(kmStr);
            if (km <= 0) {
                JOptionPane.showMessageDialog(null, "Introduce una cantidad válida de kilómetros.");
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Número inválido.");
            return;
        }

        double precioPorKm;
        double bateriaPorKm;

        if (tipoVehiculo.equalsIgnoreCase("Moto Pequeña")) {
            precioPorKm = 1.0;
            bateriaPorKm = 2.0;
        } else if (tipoVehiculo.equalsIgnoreCase("Moto Grande")) {
            precioPorKm = 2.0;
            bateriaPorKm = 1.0;
        } else {
            JOptionPane.showMessageDialog(null, "Tipo de moto no reconocido.");
            return;
        }

        double total = km * precioPorKm;
        descontarSaldoUsuario(total);

        // Aquí recuperas el saldo actualizado
        double saldoActual = obtenerSaldoDesdeArchivo();

        // Calcular nueva batería
        double bateriaActual = Double.parseDouble(vehiculo[2]);
        double bateriaGastada = km * bateriaPorKm;
        double bateriaNueva = bateriaActual - bateriaGastada;
        if (bateriaNueva < 0) {
            bateriaNueva = 0; // No puede ser negativa
        }
        vehiculo[2] = String.valueOf(bateriaNueva); // Actualizamos batería en el array

        JOptionPane.showMessageDialog(null, 
            "Has recorrido " + km + " km.\n" +
            "Total a pagar: " + total + " €.\n" +
            "Nueva batería: " + bateriaNueva + "%\n" +
            "Saldo restante: " + saldoActual + " €"
        );

        // Ahora actualizamos vehiculos.txt
        actualizarVehiculo(vehiculo);

        guardarHistorialViaje(idVehiculo, km, total,
            Double.parseDouble(vehiculo[4]), Double.parseDouble(vehiculo[5]), // origen
            Double.parseDouble(vehiculo[4]), Double.parseDouble(vehiculo[5])); // mismo destino si no cambian

    }

    private double obtenerSaldoDesdeArchivo() {
        if (usuarioActivo == null) {
            JOptionPane.showMessageDialog(null, "No hay usuario activo seleccionado.");
            return -1;
        }

        String dniBuscado = usuarioActivo[1]; // El DNI es el segundo campo

        File archivo = new File("usuarios.txt");

        try (Scanner sc = new Scanner(archivo)) {
            while (sc.hasNextLine()) {
                String linea = sc.nextLine();
                String[] partes = linea.split(";");

                if (partes.length == 5) {
                    String dni = partes[1];
                    if (dni.equals(dniBuscado)) {
                        double saldo = Double.parseDouble(partes[3]);
                        return saldo;
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error leyendo usuarios.txt: " + e.getMessage());
        }

        return -1; // No encontrado o error
    }

    private void moverVehiculoAEstacion(String[] vehiculo) {
        ArrayList<String[]> estacionesList = new ArrayList<>();

        try (Scanner sc = new Scanner(new File("estaciones.txt"))) {
            while (sc.hasNextLine()) {
                String[] linea = sc.nextLine().split(";");
                if (linea.length == 3) {
                    estacionesList.add(linea);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error leyendo estaciones: " + e.getMessage());
            return;
        }

        if (estacionesList.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay estaciones disponibles.");
            return;
        }

        String[][] estaciones = estacionesList.toArray(new String[0][]);

        String estacionOrigen = "No asignada";
        if (vehiculo.length >= 6) {
            estacionOrigen = buscarEstacion(estacionesList, vehiculo[4], vehiculo[5]);
        }

        JComboBox<String> comboEstaciones = new JComboBox<>();
        for (String[] est : estacionesList) {
            if (!est[0].equals(estacionOrigen)) {
                comboEstaciones.addItem(est[0]);
            }
        }

        int opcion = JOptionPane.showConfirmDialog(null, comboEstaciones, "Selecciona una estación destino", JOptionPane.OK_CANCEL_OPTION);
        if (opcion != JOptionPane.OK_OPTION) return;

        String nombreEstacion = (String) comboEstaciones.getSelectedItem();
        if (nombreEstacion == null) {
            JOptionPane.showMessageDialog(null, "No se ha seleccionado una estación válida.");
            return;
        }

        String[] estacionSeleccionada = Arrays.stream(estaciones)
            .filter(e -> e[0].equals(nombreEstacion))
            .findFirst().orElse(null);

        if (estacionSeleccionada == null) {
            JOptionPane.showMessageDialog(null, "Error al seleccionar la estación.");
            return;
        }

        int contador = contarBicisOPatinetesEnEstacion(estacionSeleccionada[0], estaciones);
        String tipoVehiculo = vehiculo[0];
        if ((tipoVehiculo.equals("Bicicleta") || tipoVehiculo.equals("Patinete")) && contador >= 10) {
            JOptionPane.showMessageDialog(null, "La estación ya tiene 10 bicicletas/patinetes. No se puede mover el vehículo allí.");
            return;
        }

        try {
            double latActual = Double.parseDouble(vehiculo[4]);
            double lonActual = Double.parseDouble(vehiculo[5]);

            double latDestino = Double.parseDouble(estacionSeleccionada[1]);
            double lonDestino = Double.parseDouble(estacionSeleccionada[2]);

            double distancia = calcularDistancia(latActual, lonActual, latDestino, lonDestino);
            double precioPorKm = 1.0;
            double total = distancia * precioPorKm;

            double saldoActual = obtenerSaldoDesdeArchivo();
            double bateriaActual = Double.parseDouble(vehiculo[2]);
            double bateriaGastada = distancia * 1.0;

            if (bateriaActual < bateriaGastada) {
                JOptionPane.showMessageDialog(null, "No hay suficiente batería para realizar el viaje.\n" +
                    "Batería disponible: " + bateriaActual + "%\n" +
                    "Batería necesaria: " + bateriaGastada + "%");
                return;
            }

            if (saldoActual < total) {
                JOptionPane.showMessageDialog(null, "No hay saldo suficiente para realizar el viaje.\n" +
                    "Saldo disponible: " + saldoActual + " €\n" +
                    "Saldo necesario: " + total + " €");
                return;
            }

            descontarSaldoUsuario(total);
            double nuevaBateria = bateriaActual - bateriaGastada;
            vehiculo[2] = String.valueOf(nuevaBateria);
            vehiculo[4] = estacionSeleccionada[1];
            vehiculo[5] = estacionSeleccionada[2];

            actualizarVehiculo(vehiculo);
            double saldoRestante = obtenerSaldoDesdeArchivo();

            JOptionPane.showMessageDialog(null,
                "Vehículo movido a " + estacionSeleccionada[0] + "\n" +
                "Distancia recorrida: " + String.format("%.2f", distancia) + " km\n" +
                "Total pagado: " + String.format("%.2f", total) + " €\n" +
                "Nueva batería: " + String.format("%.2f", nuevaBateria) + "%\n" +
                "Saldo restante: " + String.format("%.2f", saldoRestante) + " €"
            );

            guardarHistorialViaje(vehiculo[1], distancia, total,
                latActual, lonActual,
                latDestino, lonDestino);

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error en las coordenadas del vehículo o estación.");
        }

    }
    // Método para actualizar el archivo de estaciones con la nueva información de los vehículos
    private void actualizarArchivoEstaciones(ArrayList<String[]> estaciones, HashMap<String, Integer> vehiculosPorEstacion) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("estaciones.txt"))) {
            for (String[] estacion : estaciones) {
                String nombreEstacion = estacion[0];
                int cantidadVehiculos = vehiculosPorEstacion.getOrDefault(nombreEstacion, 0);
                writer.write(nombreEstacion + ";" + estacion[1] + ";" + estacion[2] + ";" + cantidadVehiculos);
                writer.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar el archivo de estaciones: " + e.getMessage());
        }
    }

    private void guardarHistorialViaje(String idVehiculo, double km, double precio, double latInicio, double lonInicio, double latFin, double lonFin) {
        if (usuarioActivo == null) return;

        String nombreArchivo = "historial_" + usuarioActivo[1] + ".txt"; // DNI como identificador
        String linea = "ID: " + idVehiculo +
            " | Km: " + String.format("%.2f", km) +
            " | Precio: " + String.format("%.2f", precio) + " €" +
            " | Inicio: (" + latInicio + ", " + lonInicio + ")" +
            " | Fin: (" + latFin + ", " + lonFin + ")";

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivo, true))) {
            bw.write(linea);
            bw.newLine();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar el historial: " + e.getMessage());
        }
    }

    private void mostrarHistorial() {
        if (usuarioActivo == null) {
            JOptionPane.showMessageDialog(null, "No hay usuario logueado.");
            return;
        }

        String usuarioDni = usuarioActivo[1]; // DNI del usuario logueado
        File historialFile = new File("historial_" + usuarioDni + ".txt");

        if (!historialFile.exists()) {
            JOptionPane.showMessageDialog(null, "No se encontraron registros de historial.");
            return;
        }

        String[] columnas = {"ID Vehículo", "Km Recorridos", "Precio (€)", "Inicio", "Fin"};
        DefaultTableModel modelo = new DefaultTableModel(columnas, 0);

        try (Scanner scanner = new Scanner(historialFile)) {
            int lineCount = 0;  // Contador de líneas leídas
            while (scanner.hasNextLine()) {
                lineCount++;
                String linea = scanner.nextLine().trim();  // Eliminar espacios en blanco alrededor

                // Usamos una expresión regular para extraer los datos
                String regex = "ID: (\\d+) \\| Km: ([\\d,]+) \\| Precio: ([\\d,]+) € \\| Inicio: \\(([^,]+), ([^)]+)\\) \\| Fin: \\(([^,]+), ([^)]+)\\)";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(linea);

                if (matcher.find()) {
                    String idVehiculo = matcher.group(1);
                    String kmRecorridos = matcher.group(2);
                    String precio = matcher.group(3);
                    String latInicio = matcher.group(4);
                    String lonInicio = matcher.group(5);
                    String latFin = matcher.group(6);
                    String lonFin = matcher.group(7);

                    // Crear una fila con los datos extraídos
                    Object[] fila = {idVehiculo, kmRecorridos, precio, "(" + latInicio + ", " + lonInicio + ")", "(" + latFin + ", " + lonFin + ")"};
                    modelo.addRow(fila);
                } else {
                    System.out.println("Línea ignorada debido a formato incorrecto: " + linea);  // Depuración
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error leyendo el historial: " + e.getMessage());
        }

        // Crear la tabla con el modelo
        JTable tablaHistorial = new JTable(modelo);
        JScrollPane scrollPane = new JScrollPane(tablaHistorial);

        // Mostrar la tabla en un cuadro de diálogo
        JOptionPane.showMessageDialog(null, scrollPane, "Historial de Viajes", JOptionPane.INFORMATION_MESSAGE);
    }

    private double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la Tierra en km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private void realizarViajeMoto(String[] vehiculo) {
        String kmStr = JOptionPane.showInputDialog("Introduce la cantidad de km a recorrer:");
        if (kmStr == null) {
            return;
        }

        try {
            int km = Integer.parseInt(kmStr);
            if (km <= 0) {
                JOptionPane.showMessageDialog(null, "Los km deben ser mayores que 0.");
                return;
            }

            double coste = km * 1.0; // 1 euro por km

            // Reducir batería proporcionalmente (ejemplo: 1% por km)
            int bateriaActual = Integer.parseInt(vehiculo[2]);
            int bateriaReducida = bateriaActual - km;
            if (bateriaReducida < 0) bateriaReducida = 0;
            vehiculo[2] = String.valueOf(bateriaReducida);

            actualizarVehiculo(vehiculo);

            JOptionPane.showMessageDialog(null, "Viaje realizado. Coste: " + coste + "€");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Número inválido.");
        }
    }

    private void actualizarVehiculo(String[] vehiculoActualizado) {
        File original = new File("vehiculos.txt");
        File temporal = new File("vehiculos_temp.txt");

        try (Scanner sc = new Scanner(original);
        PrintWriter pw = new PrintWriter(new FileWriter(temporal))) {

            while (sc.hasNextLine()) {
                String linea = sc.nextLine();
                String[] datos = linea.split(";");
                if (datos.length == 6 && datos[1].equals(vehiculoActualizado[1])) {
                    // Es el vehículo que hemos modificado
                    pw.println(String.join(";", vehiculoActualizado));
                } else {
                    // Cualquier otro vehículo se escribe igual
                    pw.println(linea);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error actualizando vehículo: " + e.getMessage());
            return;
        }

        // Reemplazar el archivo original
        if (original.delete()) {
            temporal.renameTo(original);
        } else {
            JOptionPane.showMessageDialog(null, "Error al reemplazar archivos.");
        }
    }

    public void crearMenuClientePremium() {
        JFrame frame = new JFrame("Menú Cliente");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 300);

        JPanel panel = new JPanel(new GridLayout(2, 1));

        JButton btnVerVehiculos = new JButton("Ver Vehículos Disponibles");
        JButton btnVerEstaciones = new JButton("Ver Estaciones Disponibles");

        btnVerVehiculos.addActionListener(e -> mostrarVehiculosDisponiblesPremium());
        btnVerEstaciones.addActionListener(e -> mostrarEstacionesDisponibles());

        panel.add(new JLabel("Bienvenido, Cliente"));
        panel.add(btnVerVehiculos);
        panel.add(btnVerEstaciones);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    private void crearMenuMecanico() {
        JFrame frame = new JFrame("Menú del Mecánico");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new GridLayout(2, 1, 10, 10));

        JButton btnVerAveriados = new JButton("Ver vehículos averiados");

        btnVerAveriados.addActionListener(e -> mostrarVehiculosAveriados());

        frame.add(btnVerAveriados);
        frame.setVisible(true);
    }

    private void crearMenuEncargado() {
        JFrame frame = new JFrame("Menú del Encargado");
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new GridLayout(2, 1, 10, 10));

        JButton btnVerCriticos = new JButton("Ver vehículos averiados con batería < 20%");
        btnVerCriticos.addActionListener(e -> mostrarVehiculosMantenimiento());

        JButton btnSalir = new JButton("Salir");
        btnSalir.addActionListener(e -> frame.dispose());

        frame.add(btnVerCriticos);
        frame.add(btnSalir);

        frame.setVisible(true);
    }

    private void mostrarVehiculosFiltrados(String titulo, java.util.function.Predicate<String[]> filtro) {
        ArrayList<String[]> datos = new ArrayList<>();
        ArrayList<String[]> estaciones = new ArrayList<>();

        // Leer las estaciones
        try (Scanner scEstaciones = new Scanner(new File("estaciones.txt"))) {
            while (scEstaciones.hasNextLine()) {
                String[] linea = scEstaciones.nextLine().split(";");
                if (linea.length == 3) {
                    estaciones.add(linea); // [nombre, latitud, longitud]
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al leer estaciones: " + e.getMessage());
            return;
        }

        // Leer los vehículos
        try (Scanner sc = new Scanner(new File("vehiculos.txt"))) {
            while (sc.hasNextLine()) {
                String[] linea = sc.nextLine().split(";");
                if (linea.length == 6 && filtro.test(linea)) {
                    // Buscar estación
                    String estacionEncontrada = buscarEstacion(estaciones, linea[4], linea[5]);
                    // Crear nueva línea con columna extra
                    String[] lineaConEstacion = new String[7];
                    System.arraycopy(linea, 0, lineaConEstacion, 0, 6);
                    lineaConEstacion[6] = estacionEncontrada;
                    datos.add(lineaConEstacion);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al leer vehículos: " + e.getMessage());
            return;
        }

        if (datos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay vehículos que cumplan el criterio.");
            return;
        }

        String[][] datosArray = new String[datos.size()][7];
        datos.toArray(datosArray);

        // Añadir nueva columna
        String[] columnas = {"Tipo", "ID", "Batería", "Averiado", "Latitud", "Longitud", "Estación"};

        DefaultTableModel model = new DefaultTableModel(datosArray, columnas) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

        JTable tabla = new JTable(model);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JDialog dialogo = new JDialog();
        dialogo.setTitle(titulo);
        dialogo.setModal(true);
        dialogo.setSize(700, 400);
        dialogo.setLocationRelativeTo(null);
        dialogo.getContentPane().add(panel);
        dialogo.setVisible(true);
    }

    // Método auxiliar para buscar estación cercana
    private String buscarEstacion(ArrayList<String[]> estaciones, String latStr, String lonStr) {
        double latVehiculo = Double.parseDouble(latStr);
        double lonVehiculo = Double.parseDouble(lonStr);
        double margen = 0.0005; // margen de error pequeño (~50 metros)

        for (String[] estacion : estaciones) {
            double latEstacion = Double.parseDouble(estacion[1]);
            double lonEstacion = Double.parseDouble(estacion[2]);
            if (Math.abs(latVehiculo - latEstacion) <= margen && Math.abs(lonVehiculo - lonEstacion) <= margen) {
                return estacion[0]; // Nombre de la estación
            }
        }
        return "No asignada"; // Si no encuentra ninguna cercana
    }

    private void mostrarVehiculosAveriados() {
        mostrarVehiculosFiltrados("Vehículos Averiados", linea -> Boolean.parseBoolean(linea[3]));
    }

    private void mostrarVehiculosDisponibles() {
        mostrarVehiculosFiltrados("Vehículos Disponibles", linea -> {
                    double bateria = Double.parseDouble(linea[2]);
                    boolean averiado = Boolean.parseBoolean(linea[3]);
                    return bateria > 20 && !averiado;
            });
    }

    private void mostrarVehiculosDisponiblesPremium() {
        mostrarVehiculosFiltrados("Vehículos Disponibles", linea -> {
                    double bateria = Double.parseDouble(linea[2]);
                    boolean averiado = Boolean.parseBoolean(linea[3]);
                    return bateria > 10 && !averiado;
            });
    }

    private void mostrarVehiculosMantenimiento() {
        mostrarVehiculosFiltrados("Vehículos para Mantenimiento", linea -> {
                    double bateria = Double.parseDouble(linea[2]);
                    boolean averiado = Boolean.parseBoolean(linea[3]);
                    return bateria < 20 || averiado;
            });
    }

    private void mostrarEstacionesDisponibles() {
        String[][] estacionesData = cargarEstaciones();

        if (estacionesData.length == 0) {
            JOptionPane.showMessageDialog(null, "No hay estaciones disponibles.");
            return;
        }

        String[] columnas = {"Nombre", "Latitud", "Longitud"};

        DefaultTableModel model = new DefaultTableModel(estacionesData, columnas) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;  // Deshabilitamos la edición de la tabla
                }
            };

        JTable tabla = new JTable(model);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JDialog dialogo = new JDialog();
        dialogo.setTitle("Estaciones Disponibles");
        dialogo.setModal(true);
        dialogo.setSize(600, 400);
        dialogo.setLocationRelativeTo(null);
        dialogo.getContentPane().add(panel);

        dialogo.setVisible(true);
    }

    private void crearUsuario() {
        String nombre = JOptionPane.showInputDialog("Nombre:");
        String dni = JOptionPane.showInputDialog("DNI:");
        String email = JOptionPane.showInputDialog("Email:");
        double saldo = Double.parseDouble(JOptionPane.showInputDialog("Saldo inicial:"));

        if (GestorArchivo.idExisteEnArchivo("usuarios.txt", dni, 1)) {
            JOptionPane.showMessageDialog(null, "Ya existe un usuario con ese DNI.");
            return;
        }

        String[] tipos = {"Estándar", "Premium"};
        String tipo = (String) JOptionPane.showInputDialog(null, "Tipo de usuario:", "Seleccionar tipo", JOptionPane.QUESTION_MESSAGE, null, tipos, tipos[0]);

        String usuarioString = nombre + ";" + dni + ";" + email + ";" + saldo + ";" + tipo;
        GestorArchivo.escribirEnArchivo("usuarios.txt", usuarioString);
        JOptionPane.showMessageDialog(null, "Usuario creado con éxito.");
    }

    private boolean idExisteEnArchivo(String archivo, String id, int columnaID) {
        try (Scanner sc = new Scanner(new File(archivo))) {
            while (sc.hasNextLine()) {
                String[] partes = sc.nextLine().split(";");
                if (partes.length > columnaID && partes[columnaID].equalsIgnoreCase(id)) {
                    return true;
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al leer " + archivo + ": " + e.getMessage());
        }
        return false;
    }

    private void crearVehiculo() {
        String[] opciones = {"Bicicleta", "Patinete", "Moto pequeña", "Moto grande"};
        String tipo = (String) JOptionPane.showInputDialog(null, "Tipo de vehículo:", "Seleccionar", JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        if (tipo == null) return;

        String id = JOptionPane.showInputDialog("ID del vehículo:");
        if (id == null || GestorArchivo.idExisteEnArchivo("vehiculos.txt", id, 1)) {
            JOptionPane.showMessageDialog(null, "Ya existe un vehículo con ese ID.");
            return;
        }

        double bateria;
        do {
            String input = JOptionPane.showInputDialog("Nivel de batería (%): (0 - 100)");
            if (input == null) return;
            bateria = Double.parseDouble(input);
            if (bateria < 0 || bateria > 100) {
                JOptionPane.showMessageDialog(null, "La batería debe estar entre 0 y 100.");
            }
        } while (bateria < 0 || bateria > 100);

        double lat = 0.0, lon = 0.0;

        if (tipo.equals("Bicicleta") || tipo.equals("Patinete")) {
            String[][] estaciones = cargarEstaciones();
            if (estaciones.length == 0) {
                JOptionPane.showMessageDialog(null, "No hay estaciones disponibles. No se puede crear el vehículo.");
                return;
            }

            String[] nombresEstaciones = Arrays.stream(estaciones).map(e -> e[0]).toArray(String[]::new);
            String estacionSeleccionada = (String) JOptionPane.showInputDialog(null, "Selecciona una estación:", "Ubicación del vehículo", JOptionPane.QUESTION_MESSAGE, null, nombresEstaciones, nombresEstaciones[0]);
            if (estacionSeleccionada == null) return;

            int contador = contarBicisOPatinetesEnEstacion(estacionSeleccionada, estaciones);
            if (contador >= 10) {
                JOptionPane.showMessageDialog(null, "La estación ya tiene 10 bicicletas/patinetes. No se puede añadir más.");
                return;
            }

            for (String[] estacion : estaciones) {
                if (estacion[0].equals(estacionSeleccionada)) {
                    lat = Double.parseDouble(estacion[1]);
                    lon = Double.parseDouble(estacion[2]);
                    break;
                }
            }
        } else {
            double[] coordenadas = generarCoordenadasZaragoza(10.0);
            lat = coordenadas[0];
            lon = coordenadas[1];
        }

        Vehiculo v = null;
        switch (tipo) {
            case "Bicicleta": v = new Bicicleta(id, bateria); break;
            case "Patinete": v = new Patinete(id, bateria); break;
            case "Moto pequeña": v = new MotoPequena(id, bateria); break;
            case "Moto grande": v = new MotoGrande(id, bateria); break;
        }

        if (v != null) {
            String vehiculoString = tipo + ";" + id + ";" + bateria + ";false;" + lat + ";" + lon;
            GestorArchivo.escribirEnArchivo("vehiculos.txt", vehiculoString);
            JOptionPane.showMessageDialog(null, "Vehículo creado con éxito.");
        }
    }

    private void crearTrabajador() {
        String nombre = JOptionPane.showInputDialog("Nombre del trabajador:");
        String dni = JOptionPane.showInputDialog("DNI del trabajador:");
        String email = JOptionPane.showInputDialog("Email del trabajador:");

        // Verificamos que el DNI no se repita en el archivo de trabajadores
        if (GestorArchivo.idExisteEnArchivo("trabajadores.txt", dni, 1)) {
            JOptionPane.showMessageDialog(null, "Ya existe un trabajador con ese DNI.");
            return;
        }

        String[] tipos = {"Administrador", "Mecánico", "Encargado de mantenimiento"};
        String tipo = (String) JOptionPane.showInputDialog(null, "Tipo de trabajador:", "Seleccionar tipo", JOptionPane.QUESTION_MESSAGE, null, tipos, tipos[0]);

        // Crear la cadena con los datos del trabajador
        String trabajadorString = nombre + ";" + dni + ";" + email + ";" + tipo;

        // Guardamos los datos del trabajador en el archivo
        GestorArchivo.escribirEnArchivo("trabajadores.txt", trabajadorString);

        // Mostrar mensaje de éxito
        JOptionPane.showMessageDialog(null, "Trabajador creado con éxito.");
    }

    private void mostrarYEditarTabla(String archivo, String[] columnas) {
        ArrayList<String[]> datos = new ArrayList<>();

        try (Scanner sc = new Scanner(new File(archivo))) {
            while (sc.hasNextLine()) {
                String[] linea = sc.nextLine().split(";");
                if (linea.length == columnas.length) {
                    datos.add(linea);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al leer " + archivo + ": " + e.getMessage());
            return;
        }

        if (datos.isEmpty()) {
            JOptionPane.showMessageDialog(null, "No hay datos disponibles en " + archivo);
            return;
        }

        String[][] datosArray = new String[datos.size()][columnas.length];
        datos.toArray(datosArray);

        DefaultTableModel model = new DefaultTableModel(datosArray, columnas) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return true;
                }
            };

        JTable tabla = new JTable(model);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);

        JButton btnGuardar = new JButton("Guardar Cambios");
        JPanel botonesPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botonesPanel.add(btnGuardar);

        JButton btnEliminar = new JButton("Eliminar Seleccionado");
        botonesPanel.add(btnEliminar);

        panel.add(botonesPanel, BorderLayout.SOUTH);

        JDialog dialogo = new JDialog();
        dialogo.setTitle("Editar " + archivo);
        dialogo.setModal(true);
        dialogo.setSize(600, 400);
        dialogo.setLocationRelativeTo(null);
        dialogo.getContentPane().add(panel);

        btnGuardar.addActionListener(e -> {
                    if (tabla.isEditing()) {
                        tabla.getCellEditor().stopCellEditing();
                    }
                    try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
                        for (int i = 0; i < model.getRowCount(); i++) {
                            // Validar batería si el archivo es de vehículos
                            if (archivo.equals("vehiculos.txt")) {
                                try {
                                    double bateria = Double.parseDouble(model.getValueAt(i, 2).toString());
                                    if (bateria < 0 || bateria > 100) {
                                        JOptionPane.showMessageDialog(dialogo, "Error: La batería de la fila " + (i+1) + " debe estar entre 0 y 100.");
                                        return;
                                    }
                                } catch (NumberFormatException nfe) {
                                    JOptionPane.showMessageDialog(dialogo, "Error: Valor de batería inválido en la fila " + (i+1));
                                    return;
                                }
                            }

                            StringBuilder sb = new StringBuilder();
                            for (int j = 0; j < model.getColumnCount(); j++) {
                                sb.append(model.getValueAt(i, j));
                                if (j < model.getColumnCount() - 1)
                                    sb.append(";");
                            }
                            bw.write(sb.toString());
                            bw.newLine();
                        }
                        JOptionPane.showMessageDialog(dialogo, "Cambios guardados correctamente.");
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(dialogo, "Error al guardar en " + archivo + ": " + ex.getMessage());
                    }
            });

        btnEliminar.addActionListener(e -> {
                    int fila = tabla.getSelectedRow();
                    if (fila >= 0) {
                        model.removeRow(fila);
                    } else {
                        JOptionPane.showMessageDialog(dialogo, "Selecciona una fila para eliminar.");
                    }
            });

        dialogo.setVisible(true);
    }

    public static void main(String[] args) {
        //SwingUtilities.invokeLater(): Se utiliza para asegurar que las operaciones que modifican la interfaz gráfica se realicen en el hilo de la interfaz gráfica
        SwingUtilities.invokeLater(() -> mostrarLogin());
    }

    private static void mostrarLogin() {
        String[] roles = {"Administrador", "Mecánico", "Encargado", "Usuario", "Usuario premium"};
        String rol = (String) JOptionPane.showInputDialog(null, "Selecciona tu rol:", "Inicio de sesión", JOptionPane.QUESTION_MESSAGE, null, roles, roles[0]);

        if (rol == null) return;

        Movilidad app = new Movilidad(); // Crear instancia de tu clase principal

        switch (rol) {
            case "Administrador":
                SwingUtilities.invokeLater(() -> app.crearMenuAdministrador());
                break;
            case "Mecánico":
                SwingUtilities.invokeLater(() -> app.crearMenuMecanico());
                break;
            case "Encargado":
                SwingUtilities.invokeLater(() -> app.crearMenuEncargado());
                break;
            case "Usuario":
                app.seleccionarUsuarioEstandar(); // <<< Pedimos primero seleccionar usuario estándar
                if (app.usuarioActivo != null) {   // <<< Solo si eligió usuario seguimos
                    SwingUtilities.invokeLater(() -> app.crearMenuCliente());
                }
                break;
            case "Usuario premium":
                SwingUtilities.invokeLater(() -> app.crearMenuClientePremium());
                break;
        }
    }

    private void seleccionarUsuarioEstandar() {
        ArrayList<String[]> usuariosEstandar = new ArrayList<>();

        // Leer usuarios del archivo
        try (Scanner sc = new Scanner(new File("usuarios.txt"))) {
            while (sc.hasNextLine()) {
                String[] linea = sc.nextLine().split(";");
                if (linea.length == 5 && linea[4].equalsIgnoreCase("Estándar")) {
                    usuariosEstandar.add(linea);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error leyendo usuarios: " + e.getMessage());
            return;
        }

        // Si no hay usuarios estándar
        if (usuariosEstandar.isEmpty()) {
            int crearNuevo = JOptionPane.showConfirmDialog(null, "No hay usuarios Estándar. ¿Quieres crear uno nuevo?", "Crear Usuario", JOptionPane.YES_NO_OPTION);
            if (crearNuevo == JOptionPane.YES_OPTION) {
                crearNuevoUsuarioEstandar();
            } else {
                usuarioActivo = null;
            }
            return;
        }

        // Crear lista de nombres para el ComboBox
        String[] nombresUsuarios = new String[usuariosEstandar.size()];
        for (int i = 0; i < usuariosEstandar.size(); i++) {
            nombresUsuarios[i] = usuariosEstandar.get(i)[0] + " (" + usuariosEstandar.get(i)[1] + ")";
        }

        // Mostrar selección de usuario
        String seleccionado = (String) JOptionPane.showInputDialog(null, "Selecciona un usuario Estándar:", "Seleccionar Usuario", JOptionPane.QUESTION_MESSAGE, null, nombresUsuarios, nombresUsuarios[0]);

        if (seleccionado != null) {
            int index = Arrays.asList(nombresUsuarios).indexOf(seleccionado);
            usuarioActivo = usuariosEstandar.get(index); // Guardamos el usuario elegido
        } else {
            usuarioActivo = null;
        }
    }

    private void crearNuevoUsuarioEstandar() {
        String nombre = JOptionPane.showInputDialog("Introduce el nombre:");
        if (nombre == null) return;

        String dni = JOptionPane.showInputDialog("Introduce el DNI:");
        if (dni == null) return;

        String correo = JOptionPane.showInputDialog("Introduce el correo:");
        if (correo == null) return;

        String saldoStr = JOptionPane.showInputDialog("Introduce el saldo inicial:");
        if (saldoStr == null) return;

        double saldo;
        try {
            saldo = Double.parseDouble(saldoStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Saldo inválido.");
            return;
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("usuarios.txt", true))) {
            bw.write(nombre + ";" + dni + ";" + correo + ";" + saldo + ";Estándar");
            bw.newLine();
            JOptionPane.showMessageDialog(null, "Usuario creado exitosamente.");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar el usuario: " + e.getMessage());
        }
    }

} 