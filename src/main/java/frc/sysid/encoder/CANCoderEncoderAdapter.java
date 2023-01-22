package frc.sysid.encoder;

import com.ctre.phoenix.sensors.CANCoder;

import frc.sysid.SysIdConfig.EncoderConfig;

public class CANCoderEncoderAdapter implements EncoderAdapter {

    private final CANCoder encoder;

    private double combinedCPR = 1;

    public CANCoderEncoderAdapter(int port) {
        this.encoder = new CANCoder(port);
    }

    @Override
    public void configure(EncoderConfig config) {
        encoder.configSensorDirection(config.inverted);

        var period = CTREEncoderAdapter.getCTREVelocityPeriod(config.period);
        encoder.configVelocityMeasurementPeriod(period);
        encoder.configVelocityMeasurementWindow(config.numSamples);

        combinedCPR = config.cpr * config.gearing;
    }

    @Override
    public double getRate() {
        return encoder.getVelocity() / combinedCPR;
    }

    @Override
    public double getPosition() {
        return encoder.getPosition() / combinedCPR;
    }

}
