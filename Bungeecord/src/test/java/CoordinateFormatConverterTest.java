import de.btegermany.teleportation.TeleportationBungee.geo.CoordinateFormatConverter;
import org.junit.Assert;
import org.junit.Test;

public class CoordinateFormatConverterTest {

    @Test
    public void test() {
        Assert.assertEquals(52.12525, CoordinateFormatConverter.toDegrees(52, 7, 30.9), 0);
        Assert.assertEquals(46.235197, CoordinateFormatConverter.toDegrees(46, 14, 06.70), 0.1);
        Assert.assertEquals(8.015445, CoordinateFormatConverter.toDegrees(8, 0, 55.6), 0.1);

        Assert.assertEquals(52.12525, CoordinateFormatConverter.toDegrees(52, 7.515), 0);
        Assert.assertEquals(46.235197, CoordinateFormatConverter.toDegrees(46,  14.1114), 0.1);
        Assert.assertEquals(8.015445, CoordinateFormatConverter.toDegrees(8, 0.9264), 0.1);

        Assert.assertArrayEquals(new double[] {3.289499, 13.129016}, CoordinateFormatConverter.toDegrees("3°17'22.2\"N 13°07'44.5\"E"), 0.1);
        Assert.assertArrayEquals(new double[] {52.494722, 13.360556}, CoordinateFormatConverter.toDegrees("52°29′41″N 13°21′38″"), 0.1);
        Assert.assertArrayEquals(new double[] {3.289499, 13.129016}, CoordinateFormatConverter.toDegrees("3.289499 13.129016"), 0.1);
        Assert.assertArrayEquals(new double[] {3.289499, 13.129016}, CoordinateFormatConverter.toDegrees("3.289499, 13.129016"), 0.1);
        Assert.assertArrayEquals(new double[] {49.013917, 8.371187}, CoordinateFormatConverter.toDegrees("N49° 0' 50.101\" E8° 22' 16.273\" "), 0.1);
    }

}
