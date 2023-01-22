package frc.sysid.encoder;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.sensors.SensorVelocityMeasPeriod;

import frc.sysid.SysIdConfig.EncoderConfig;

public class CTREEncoderAdapter implements EncoderAdapter {

    private final BaseMotorController controller;
    private final FeedbackDevice feedbackDevice;

    private double cpr = 1;

    public CTREEncoderAdapter(BaseMotorController controller, FeedbackDevice feedbackDevice) {
        this.controller = controller;
        this.feedbackDevice = feedbackDevice;
    }


    @Override
    public void configure(EncoderConfig config) {

        controller.configSelectedFeedbackSensor(feedbackDevice);
        controller.setSensorPhase(config.inverted);
        controller.configVelocityMeasurementWindow(config.numSamples);

        var period = getCTREVelocityPeriod(config.period);
        controller.configVelocityMeasurementPeriod(period);

        this.cpr = config.cpr;
    }

    @Override
    public double getRate() {
        return controller.getSelectedSensorVelocity() / cpr / 0.1; // Conversion factor from 100 ms to seconds
    }

    @Override
    public double getPosition() {
        return controller.getSelectedSensorPosition() / cpr;
    }

    public static SensorVelocityMeasPeriod getCTREVelocityPeriod(int period) {
        switch (period) {
          case 1:
            return SensorVelocityMeasPeriod.Period_1Ms;
          case 2:
            return SensorVelocityMeasPeriod.Period_2Ms;
          case 5:
            return SensorVelocityMeasPeriod.Period_5Ms;
          case 10:
            return SensorVelocityMeasPeriod.Period_10Ms;
          case 25:
            return SensorVelocityMeasPeriod.Period_25Ms;
          case 50:
            return SensorVelocityMeasPeriod.Period_50Ms;
          default:
            return SensorVelocityMeasPeriod.Period_100Ms;
        }
      }


}
