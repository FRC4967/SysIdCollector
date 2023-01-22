package frc.sysid;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.wpi.first.wpilibj.Filesystem;

public class SysIdConfig {

    public class MotorConfig {
        public int port;
        public String controllerName;
        public boolean inverted;

        public MotorConfig(int port, String controllerName, boolean inverted) {
            this.port = port;
            this.controllerName = controllerName;
            this.inverted = inverted;
        }

    }

    public class EncoderConfig {

        public String encoderType;
        public int[] ports;
        public boolean isEncoding;
        public int period;
        public int cpr;
        public double gearing;
        public int numSamples;
        public boolean inverted;

        public EncoderConfig(String encoderType, int[] ports, boolean isEncoding, int period, int cpr, double gearing, int numSamples, boolean inverted) {
            this.encoderType = encoderType;
            this.ports = ports;
            this.isEncoding = isEncoding;
            this.period = period;
            this.cpr = cpr;
            this.gearing = gearing;
            this.numSamples = numSamples;
            this.inverted = inverted;
        }

    }

    public static SysIdConfig readFile(File inputFile) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(inputFile, SysIdConfig.class);
    }

    public static SysIdConfig getDeployedConfig() throws IOException {
        final File path = new File(Filesystem.getDeployDirectory(), "config.json");

        if (!path.canRead()) {
            throw new IOException("Cannot read from " + path);
        }

        return readFile(path);
    }

    @JsonProperty("counts per rotation")
    public int cpr = 1;

    @JsonProperty("encoder type")
    public String encoderType = "None";

    @JsonProperty("encoding")
    public boolean isEncoding = false;

    @JsonProperty("gearing denominator")
    public double gearingDenominator = 1;

    @JsonProperty("gearing numerator")
    public double gearingNumerator = 1;

    @JsonProperty("gyro")
    public String gyro = "";

    @JsonProperty("gyro ctor")
    public String gyroConnector = "";

    @JsonProperty("is drivetrain")
    public boolean isDrivetrain = true;

    @JsonProperty("motor controllers")
    public String[] controllerNames = {};

    @JsonProperty("number of samples per average")
    public int numSamples = 1;

    @JsonProperty("primary encoder inverted")
    public boolean leftEncoderInverted = false;

    @JsonProperty("primary encoder ports")
    public int[] leftEncoderPorts = {};

    @JsonProperty("primary motor ports")
    public int[] leftPorts = {};

    @JsonProperty("primary motors inverted")
    public boolean[] leftMotorsInverted = {};

    @JsonProperty("secondary encoder inverted")
    public boolean rightEncoderInverted = false;

    @JsonProperty("secondary encoder ports")
    public int[] rightEncoderPorts = {};

    @JsonProperty("secondary motor ports")
    public int[] rightPorts = {};

    @JsonProperty("secondary motors inverted")
    public boolean[] rightMotorsInverted = {};

    @JsonProperty("velocity measurement period")
    public int period = 100;

    public MotorConfig getLeftMotorConfig(int index) {
        return new MotorConfig(leftPorts[index],
                controllerNames[index],
                leftMotorsInverted[index]);
    }

    public MotorConfig getRightMotorConfig(int index) {
        return new MotorConfig(rightPorts[index],
                controllerNames[index],
                rightMotorsInverted[index]);
    }

    public EncoderConfig getLeftEncoderConfig() {
        System.out.println("leftEncoderInverted" + leftEncoderInverted);
        return new EncoderConfig(encoderType, leftEncoderPorts, isEncoding, period, cpr, getGearing(), numSamples, leftEncoderInverted);
    }

    public double getGearing() {
        return gearingNumerator / gearingDenominator;
    }

    public EncoderConfig getRightEncoderConfig() {
        System.out.println("rightEncoderInverted" + rightEncoderInverted);
        return new EncoderConfig(encoderType, rightEncoderPorts, isEncoding, period, cpr, getGearing(), numSamples, rightEncoderInverted);
    }

}
