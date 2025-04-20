public class EncargadoMantenimiento extends Empleado {

    public EncargadoMantenimiento(String nombre, String dni, String email) {
        super(nombre, dni, email);
    }

    @Override
    public String getTipo() {
        return "Mantenimiento";
    }

    // Getters y Setters heredados de Persona
    public String getNombre() {
        return super.getNombre();
    }

    public void setNombre(String nombre) {
        super.setNombre(nombre);
    }

    public String getDni() {
        return super.getDni();
    }

    public void setDni(String dni) {
        super.setDni(dni);
    }

    public String getEmail() {
        return super.getEmail();
    }

    public void setEmail(String email) {
        super.setEmail(email);
    }
}
