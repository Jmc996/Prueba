public class Bicicleta extends Vehiculo {
    public Bicicleta(String id, double bateria) {
        super(id, bateria);
    }

    @Override
    public String getTipo() {
        return "Bicicleta";
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
