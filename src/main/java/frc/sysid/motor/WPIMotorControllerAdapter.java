package frc.sysid.motor;

import java.util.List;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import frc.sysid.SysIdConfig.MotorConfig;

public abstract class WPIMotorControllerAdapter implements MotorControllerAdapter {
    
    protected final MotorController controller;

    public WPIMotorControllerAdapter(MotorController controller) {
        this.controller = controller;
    }

    @Override
    public void configure(MotorConfig config, List<MotorControllerAdapter> otherMotors) {
        
    }
}
