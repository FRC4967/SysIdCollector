package frc.sysid.motor;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import frc.sysid.SysIdConfig.MotorConfig;

public abstract class BaseMotorControllerAdapter implements MotorControllerAdapter {

    protected final MotorController controller;

    public BaseMotorControllerAdapter(MotorController controller) {
        this.controller = controller;
    }

    public void configure(MotorConfig config) {
        controller.setInverted(config.inverted);
    }
    
    @Override
    public void set(double speed) {
        controller.set(speed);
    }

    @Override
    public double get() {
        return controller.get();
    }

    @Override
    public void setInverted(boolean isInverted) {
        controller.setInverted(isInverted);
    }

    @Override
    public boolean getInverted() {
        return controller.getInverted();
    }

    @Override
    public void disable() {
        controller.disable();
    }

    @Override
    public void stopMotor() {
        controller.stopMotor();
    }
}
