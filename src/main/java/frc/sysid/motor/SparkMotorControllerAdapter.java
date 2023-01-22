package frc.sysid.motor;

import edu.wpi.first.wpilibj.RobotController;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import frc.sysid.SysIdConfig.MotorConfig;

public class SparkMotorControllerAdapter extends BaseMotorControllerAdapter {

    public SparkMotorControllerAdapter(MotorConfig config) {
        super(new Spark(config.port));
    }

    @Override
    public double getMotorOutputVoltage() {        
        return controller.get() * RobotController.getBatteryVoltage();
    }

    @Override
    public Object unwrapMotorController() {
        return controller;
    }
}
