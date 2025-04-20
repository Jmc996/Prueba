public class Usuario extends Persona {
    protected double saldo;

    public Usuario(String nombre, String dni, String email, double saldo) {
        super(nombre, dni, email);
        this.saldo = saldo;
    }

    @Override
    public String getTipo() {
        return "Usuario Estándar";
    }

    // Getter y Setter de saldo
    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    // Getters y Setters heredados de Persona

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
