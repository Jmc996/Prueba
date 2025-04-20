public class MotoGrande extends Moto {
    public MotoGrande(String id, double bateria) {
        super(id, bateria, 500); // 500cc para moto grande
    }

    @Override
    public String getTipo() {
        return "Moto grande";
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
