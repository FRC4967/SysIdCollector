package frc.sysid.motor;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import frc.sysid.SysIdConfig.MotorConfig;

public class SparkMaxMotorControllerAdapter extends BaseMotorControllerAdapter {

    public SparkMaxMotorControllerAdapter(MotorConfig config) {
        super(createMotorController(config));
    }

    private static CANSparkMax createMotorController(MotorConfig config) {
        if ("SPARK MAX (Brushless)".equals(config.controllerName)) {
            return new CANSparkMax(config.port, MotorType.kBrushless);
        } else {
            return new CANSparkMax(config.port, MotorType.kBrushed);
        }
    }



    @Override
    public void configure(MotorConfig config) {
        CANSparkMax sparkMax = (CANSparkMax) controller;
        sparkMax.restoreFactoryDefaults();
        sparkMax.setIdleMode(IdleMode.kBrake);
        super.configure(config);
    }

    @Override
    public double getMotorOutputVoltage() {
        CANSparkMax sparkMax = (CANSparkMax) controller;
        return sparkMax.getBusVoltage() * sparkMax.getAppliedOutput();
    }

    @Override
    public Object unwrapMotorController() {
        return controller;
    }
    
}
