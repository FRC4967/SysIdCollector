package frc.sysid.gyro;

import java.util.List;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.sensors.BasePigeon;
import com.ctre.phoenix.sensors.Pigeon2;
import com.ctre.phoenix.sensors.PigeonIMU;

import edu.wpi.first.math.util.Units;
import frc.sysid.SysIdConfig;
import frc.sysid.motor.MotorControllerAdapter;

public class PigeonGyroAdapter implements GyroAdapter {

    private final BasePigeon gyro;

    public PigeonGyroAdapter(SysIdConfig config,
            List<MotorControllerAdapter> leftControllers,
            List<MotorControllerAdapter> rightControllers) {

        String portStr = config.gyroConnector;
        boolean isConnectedToTalon = portStr.contains("WPI_TalonSRX");

        if (isConnectedToTalon) {
            portStr = config.gyroConnector.split("-")[1];
        }

        int srxPort = Integer.parseInt(portStr);

        if (isConnectedToTalon) {

            WPI_TalonSRX talon = findTalon(config.leftPorts, srxPort, leftControllers);
            if (talon == null) {
                talon = findTalon(config.rightPorts, srxPort, rightControllers);
                portStr = String.format("%s (plugged to drive motorcontroller)", portStr);
            }

            if (talon == null) {
                // If it isn't tied to an existing Talon, create a new object
                talon = new WPI_TalonSRX(srxPort);
                portStr = String.format("%s (plugged to other motorcontroller)", portStr);
            }

            gyro = new PigeonIMU(talon);
            System.out.println("Setup Pigeon, " + portStr);

        } else {

            portStr = String.format("%s (CAN)", portStr);
            if ("Pigeon".equals(config.gyro)) {
                gyro = new PigeonIMU(srxPort);
            } else {
                gyro = new Pigeon2(srxPort);
            }

        }

    }

    @Override
    public void configure(SysIdConfig config) {

    }

    private WPI_TalonSRX findTalon(int[] motorPorts, int srxPort, List<MotorControllerAdapter> controllers) {
        for (int i = 0; i < motorPorts.length; i++) {
            if (motorPorts[i] == srxPort) {
                return (WPI_TalonSRX) controllers.get(i).unwrapMotorController();
            }
        }
        return null;
    }

    @Override
    public double getAngle() {
        double[] xyz = new double[3];
        gyro.getAccumGyro(xyz);
        return Units.degreesToRadians(xyz[2]);
    }

    @Override
    public double getRate() {
        double[] xyz_dps = new double[3];
        gyro.getRawGyro(xyz_dps);
        return Units.degreesToRadians(xyz_dps[2]);
    }

    @Override
    public void calibrate() {

    }

    @Override
    public void reset() {

    }

    @Override
    public void close() throws Exception {

    }
}
