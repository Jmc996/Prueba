import java.util.*;
import java.io.*;

public class MovilidadGestion {
    private ArrayList<Usuario> usuarios;
    private ArrayList<Vehiculo> vehiculos;

    public MovilidadGestion() {
        usuarios = new ArrayList<>();
        vehiculos = new ArrayList<>();
        cargarUsuariosDesdeArchivo("usuarios.txt");
        cargarVehiculosDesdeArchivo("vehiculos.txt");
    }

    public void crearUsuario(String nombre, String dni, String email, double saldo) {
        Usuario u = new Usuario(nombre, dni, email, saldo);
        usuarios.add(u);
        guardarUsuario(u, "usuarios.txt");
    }

    public void crearVehiculo(Vehiculo v) {
        vehiculos.add(v);
        guardarVehiculo(v, "vehiculos.txt");
    }

    private void guardarUsuario(Usuario u, String archivo) {
        try (FileWriter fw = new FileWriter(archivo, true);
        BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(u.nombre + ";" + u.dni + ";" + u.email + ";" + u.saldo + "\n");
        } catch (IOException e) {
            System.out.println("Error guardando usuario: " + e.getMessage());
        }
    }

    private void guardarVehiculo(Vehiculo v, String archivo) {
        try (FileWriter fw = new FileWriter(archivo, true);
        BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write(v.getTipo() + ";" + v.id + ";" + v.bateria + ";" + v.averiado + "\n");
        } catch (IOException e) {
            System.out.println("Error guardando vehículo: " + e.getMessage());
        }
    }

    private void cargarUsuariosDesdeArchivo(String archivo) {
        try (Scanner sc = new Scanner(new File(archivo))) {
            while (sc.hasNextLine()) {
                String[] datos = sc.nextLine().split(";");
                if (datos.length >= 4) {
                    String nombre = datos[0];
                    String dni = datos[1];
                    String email = datos[2];
                    double saldo = Double.parseDouble(datos[3]);
                    usuarios.add(new Usuario(nombre, dni, email, saldo));
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo de usuarios no encontrado, se creará al guardar.");
        }
    }

    private void cargarVehiculosDesdeArchivo(String archivo) {
        try (Scanner sc = new Scanner(new File(archivo))) {
            while (sc.hasNextLine()) {
                String[] datos = sc.nextLine().split(";");
                if (datos.length >= 4) {
                    String tipo = datos[0];
                    String id = datos[1];
                    double bateria = Double.parseDouble(datos[2]);
                    boolean averiado = Boolean.parseBoolean(datos[3]);

                    Vehiculo v = null;
                    switch (tipo) {
                        case "Bicicleta":
                            v = new Bicicleta(id, bateria);
                            break;
                        case "Patinete":
                            v = new Patinete(id, bateria);
                            break;
                        case "Moto pequeña":
                            v = new MotoPequena(id, bateria);
                            break;
                        case "Moto grande":
                            v = new MotoGrande(id, bateria);
                            break;
                    }

                    if (v != null) {
                        v.averiado = averiado;
                        vehiculos.add(v);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Archivo no encontrado, se generará uno nuevo");
        }
    }
}