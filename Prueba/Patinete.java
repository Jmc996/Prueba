public class Patinete extends Vehiculo {
    public Patinete(String id, double bateria) {
        super(id, bateria);
    }

    @Override
    public String getTipo() {
        return "Patinete";
    }

    // Getters y Setters heredados de Vehiculo

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
}
