public abstract class Vehiculo {
    protected String id;
    protected double bateria; // porcentaje
    protected boolean averiado;

    // Constructor
    public Vehiculo(String id, double bateria) {
        this.id = id;
        this.bateria = bateria;
        this.averiado = false;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getBateria() {
        return bateria;
    }

    public void setBateria(double bateria) {
        this.bateria = bateria;
    }

    public boolean isAveriado() {
        return averiado;
    }

    public void setAveriado(boolean averiado) {
        this.averiado = averiado;
    }

    // Método abstracto
    public abstract String getTipo();
}
