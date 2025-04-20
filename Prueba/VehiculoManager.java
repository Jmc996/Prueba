import javax.swing.*;
import java.io.*;

public class VehiculoManager {
    public static void crearVehiculo() {
        String[] opciones = {"Bicicleta", "Patinete", "Moto pequeña", "Moto grande"};
        String tipo = (String) JOptionPane.showInputDialog(
                null,
                "Tipo de vehículo:",
                "Seleccionar",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]);
        String id = JOptionPane.showInputDialog("ID del vehículo:");
        double bateria = Double.parseDouble(JOptionPane.showInputDialog("Nivel de batería (%):"));

        double[] coordenadas = GeoUtils.generarCoordenadasZaragoza(10.0);
        double lat = coordenadas[0];
        double lon = coordenadas[1];

        Vehiculo v = null;
        switch (tipo) {
            case "Bicicleta": v = new Bicicleta(id, bateria); break;
            case "Patinete": v = new Patinete(id, bateria); break;
            case "Moto pequeña": v = new MotoPequena(id, bateria); break;
            case "Moto grande": v = new MotoGrande(id, bateria); break;
        }
        if (v != null) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter("vehiculos.txt", true))) {
                bw.write(tipo + ";" + id + ";" + bateria + ";false;" + lat + ";" + lon);
                bw.newLine();
                JOptionPane.showMessageDialog(null, "Vehículo creado con éxito en ubicación aleatoria de Zaragoza.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error guardando vehículo: " + e.getMessage());
            }
        }
    }
}
