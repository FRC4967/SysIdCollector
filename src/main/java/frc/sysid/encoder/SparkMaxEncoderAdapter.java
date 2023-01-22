package frc.sysid.encoder;

import com.revrobotics.CANSparkMax;
import com.revrobotics.RelativeEncoder;
import com.revrobotics.SparkMaxRelativeEncoder;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxAlternateEncoder;

import frc.sysid.SysIdConfig.EncoderConfig;

public class SparkMaxEncoderAdapter implements EncoderAdapter {

    private final CANSparkMax controller;
    private final RelativeEncoder encoder;
    private double gearing = 1;

    public SparkMaxEncoderAdapter(CANSparkMax controller, SparkMaxRelativeEncoder.Type encoderType, boolean internal,
            int cpr) {

        this.controller = controller;
        switch (encoderType) {
            case kHallSensor:
                this.encoder = controller.getEncoder();
                break;
            case kQuadrature:
                if (internal) {
                    this.encoder = controller.getEncoder(encoderType, cpr);
                } else {
                    this.encoder = controller.getAlternateEncoder(SparkMaxAlternateEncoder.Type.kQuadrature, cpr);
                }
                break;
            default:
                throw new RuntimeException("Unsuported encoder type");
        }
    }

    @Override
    public void configure(EncoderConfig config) {

        if (controller.getMotorType() != MotorType.kBrushless) {
            encoder.setInverted(config.inverted);
        }

        try {
            encoder.setMeasurementPeriod(config.period);
        } catch (IllegalArgumentException e) {
            System.out.println("Failed to set measurement period for SparkMax encoder: " + e.getMessage());
        }
        encoder.setAverageDepth(config.numSamples);

        this.gearing = config.gearing;
    }

    @Override
    public double getRate() {
        return encoder.getVelocity() / gearing / 60;
    }

    @Override
    public double getPosition() {
        return encoder.getPosition() / gearing;
    }

}
