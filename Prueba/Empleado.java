public abstract class Empleado extends Persona {

    // Constructor
    public Empleado(String nombre, String dni, String email) {
        super(nombre, dni, email);
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

    // Método abstracto heredado de Persona
    @Override
    public abstract String getTipo();
}
