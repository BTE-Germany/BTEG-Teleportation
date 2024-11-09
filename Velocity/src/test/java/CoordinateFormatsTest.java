import de.btegermany.teleportation.TeleportationVelocity.geo.CoordinateFormats;
import org.junit.Assert;
import org.junit.Test;

public class CoordinateFormatsTest {

    @Test
    public void test() {
        Assert.assertTrue(CoordinateFormats.isDegrees("3.289499 13.129016"));
        Assert.assertTrue(CoordinateFormats.isDegrees("3.289499° 13.129016°"));
        Assert.assertTrue(CoordinateFormats.isDegreesMinutesSeconds("3°17'22.2\"N 13°07'44.5\"E"));
        Assert.assertTrue(CoordinateFormats.isDegreesMinutes("3°17.1' 13°07.2'"));
        Assert.assertFalse(CoordinateFormats.isDegreesMinutesSeconds("3.289499 13.129016"));
        Assert.assertFalse(CoordinateFormats.isDegreesMinutes("3.289499 13.129016"));
    }

}
