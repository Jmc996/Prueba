public class MotoPequena extends Moto {
    public MotoPequena(String id, double bateria) {
        super(id, bateria, 125); // 125cc para moto pequeña
    }

    @Override
    public String getTipo() {
        return "Moto pequeña";
    }

    // Getters y Setters heredados de Moto/Vehiculo

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

    public int getCilindrada() {
        return cilindrada;
    }

    public void setCilindrada(int cilindrada) {
        this.cilindrada = cilindrada;
    }
}
