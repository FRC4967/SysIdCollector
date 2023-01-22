package frc.sysid.gyro;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.ADIS16470_IMU;
import edu.wpi.first.wpilibj.ADIS16470_IMU.CalibrationTime;
import edu.wpi.first.wpilibj.ADIS16470_IMU.IMUAxis;
import frc.sysid.SysIdConfig;
import frc.sysid.SysId;

public class ADIS16470GyroAdapter implements GyroAdapter {

    private final ADIS16470_IMU gyro;

    public ADIS16470GyroAdapter(SysIdConfig config) {
        var port = SysId.getSPIPort(config.gyroConnector);
        this.gyro = new ADIS16470_IMU(IMUAxis.kZ, port, CalibrationTime._8s);
    }

    @Override
    public void configure(SysIdConfig config) {

    }

    @Override
    public double getRate() {
        return Units.degreesToRadians(gyro.getRate());
    }

    @Override
    public void calibrate() {
        gyro.calibrate();
        
    }

    @Override
    public void reset() {
        gyro.reset();        
    }

    @Override
    public double getAngle() {
        return Units.degreesToRadians(gyro.getAngle());
    }

    @Override
    public void close() throws Exception {
        gyro.close();        
    }

}