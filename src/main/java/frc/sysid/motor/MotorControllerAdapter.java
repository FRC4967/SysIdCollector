package frc.sysid.motor;

import java.util.List;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import frc.sysid.SysIdConfig.MotorConfig;

public interface MotorControllerAdapter extends MotorController {
    public Object unwrapMotorController();
    public void configure(MotorConfig config, List<MotorControllerAdapter> otherMotors);
    public double getMotorOutputVoltage();
}
