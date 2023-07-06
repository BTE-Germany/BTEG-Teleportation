import de.btegermany.teleportation.TeleportationBungee.geo.CoordinateFormatConverter;
import org.junit.Assert;
import org.junit.Test;

public class CoordinateFormatConverterTest {

    @Test
    public void test() {
        Assert.assertEquals(CoordinateFormatConverter.toDegrees(52, 7, 30.9), 52.12525, 0);
        Assert.assertEquals(CoordinateFormatConverter.toDegrees(46, 14, 06.70), 46.235197, 0.00001);
        Assert.assertEquals(CoordinateFormatConverter.toDegrees(8, 0, 55.6), 8.015445, 0.00001);

        Assert.assertEquals(CoordinateFormatConverter.toDegrees(52, 7.515), 52.12525, 0);
        Assert.assertEquals(CoordinateFormatConverter.toDegrees(46,  14.1114), 46.235197, 0.00001);
        Assert.assertEquals(CoordinateFormatConverter.toDegrees(8, 0.9264), 8.015445, 0.00001);

        Assert.assertArrayEquals(CoordinateFormatConverter.toDegrees("3°17'22.2\"N 13°07'44.5\"E"), new double[] {3.289499, 13.129016}, 0.1);
        Assert.assertArrayEquals(CoordinateFormatConverter.toDegrees("52°29′41″N 13°21′38″"), new double[] {52.494722, 13.360556}, 0.000001);
        Assert.assertArrayEquals(CoordinateFormatConverter.toDegrees("3.289499 13.129016"), new double[] {3.289499, 13.129016}, 0.000001);
        Assert.assertArrayEquals(CoordinateFormatConverter.toDegrees("3.289499, 13.129016"), new double[] {3.289499, 13.129016}, 0.000001);
        Assert.assertArrayEquals(CoordinateFormatConverter.toDegrees("N49° 0' 50.101\" E8° 22' 16.273\" "), new double[] {49.013917, 8.371187}, 0.1);
    }

}
