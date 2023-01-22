package frc.sysid.encoder;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import frc.sysid.SysIdConfig.EncoderConfig;

public class RoboRIOEncoderAdapter implements EncoderAdapter {

    private final Encoder encoder;

    public RoboRIOEncoderAdapter(int sourcePortA, int sourcePortB, boolean inverted, boolean isEncoding) {
        if (isEncoding) {
            this.encoder = new Encoder(sourcePortA, sourcePortB, inverted, EncodingType.k1X);
        } else {
            this.encoder = new Encoder(sourcePortA, sourcePortB, inverted);
        }
    }

    @Override
    public void configure(EncoderConfig config) {
        double combinedCPR = config.cpr * config.gearing;
        encoder.setDistancePerPulse(1 / combinedCPR);
        encoder.setReverseDirection(config.inverted);
        encoder.setSamplesToAverage(config.numSamples);
    }

    @Override
    public double getRate() {
        return encoder.getRate();
    }

    @Override
    public double getPosition() {
        return encoder.getDistance();
    }

}
