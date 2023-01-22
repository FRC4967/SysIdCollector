import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import frc.sysid.SysIdConfig;

public class SysIfConfigTest {

    @Test
    public void testReadFile() throws IOException {


        var testFileURL = getClass().getResource("SysID_SharkFin.json");
        assertNotNull(testFileURL);
        var testFile = new File(testFileURL.getFile());
        
        assumeTrue(testFile.canRead());

        var config = SysIdConfig.readFile(testFile);
        assertNotNull(config);

        assertEquals(1.0, config.cpr);
        assertEquals("Encoder Port", config.encoderType);
        assertEquals(false, config.isEncoding);
        assertEquals(10.71, config.gearingNumerator);
        assertEquals("ADXRS450", config.gyro);
        assertEquals("SPI (Onboard CS0)", config.gyroConnector);
        assertEquals(true, config.isDrivetrain);
        assertArrayEquals(new String[] {"SPARK MAX (Brushless)"}, config.controllerNames);
        assertEquals(1, config.numSamples);
        assertEquals(false, config.leftEncoderInverted);
        assertArrayEquals(new int[] {0, 1}, config.leftEncoderPorts);
    }

}
