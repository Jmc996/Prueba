import java.io.*;
import java.util.Scanner;

public class GestorArchivo {

    // Verifica si un ID ya existe en una columna específica del archivo
    public static boolean idExisteEnArchivo(String archivo, String id, int columnaID) {
        try (Scanner sc = new Scanner(new File(archivo))) {
            while (sc.hasNextLine()) {
                String[] partes = sc.nextLine().split(";");
                if (partes.length > columnaID && partes[columnaID].equalsIgnoreCase(id)) {
                    return true;  // ID encontrado
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer " + archivo + ": " + e.getMessage());
        }
        return false;  // ID no encontrado
    }

    // Añade una línea al final del archivo
    public static void escribirEnArchivo(String archivo, String linea) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))) {
            bw.write(linea);
            bw.newLine();  // Salto de línea
        } catch (IOException e) {
            System.out.println("Error al guardar en " + archivo + ": " + e.getMessage());
        }
    }
}
