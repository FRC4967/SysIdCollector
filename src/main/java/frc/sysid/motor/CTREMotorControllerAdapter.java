package frc.sysid.motor;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import frc.sysid.SysIdConfig.MotorConfig;

public class CTREMotorControllerAdapter extends BaseMotorControllerAdapter {

    public CTREMotorControllerAdapter(MotorConfig config) {
        super(createMotorController(config));
    }

    private static MotorController createMotorController(MotorConfig config) {
        switch (config.controllerName) {
            case "TalonSRX":
                return new WPI_TalonSRX(config.port);
            case "TalonFX":
                return new WPI_TalonFX(config.port);
            default:
                return new WPI_VictorSPX(config.port);
        }
    }

    @Override
    public void configure(MotorConfig config) {
        BaseMotorController ctreController = (BaseMotorController) controller;
        ctreController.configFactoryDefault();
        ctreController.setNeutralMode(NeutralMode.Brake);
        super.configure(config);
    }

    @Override
    public double getMotorOutputVoltage() {
        BaseMotorController ctreController = (BaseMotorController) controller;
        return ctreController.getMotorOutputVoltage();
    }

    @Override
    public Object unwrapMotorController() {
        return controller;
    }

}
