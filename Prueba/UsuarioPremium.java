public class UsuarioPremium extends Usuario {

    public UsuarioPremium(String nombre, String dni, String email, double saldo) {
        super(nombre, dni, email, saldo);
    }

    @Override
    public String getTipo() {
        return "Usuario Premium";
    }

    // Getters y Setters heredados

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

    public double getSaldo() {
        return super.getSaldo();
    }

    public void setSaldo(double saldo) {
        super.setSaldo(saldo);
    }
}
