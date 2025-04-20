import java.util.Random;

public class GeoUtils {
    public static double[] generarCoordenadasZaragoza(double radioKm) {
        double latCentro = 41.6561;
        double lonCentro = -0.8773;

        double radioLat = radioKm / 111.32;
        double radioLon = radioKm / 85.12;

        Random rand = new Random();
        double latOffset = (rand.nextDouble() * 2 - 1) * radioLat;
        double lonOffset = (rand.nextDouble() * 2 - 1) * radioLon;

        return new double[]{latCentro + latOffset, lonCentro + lonOffset};
    }
}
