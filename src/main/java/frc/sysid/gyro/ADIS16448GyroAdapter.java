package frc.sysid.gyro;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.ADIS16448_IMU;
import edu.wpi.first.wpilibj.ADIS16448_IMU.CalibrationTime;
import edu.wpi.first.wpilibj.ADIS16448_IMU.IMUAxis;
import frc.sysid.SysIdConfig;
import frc.sysid.SysId;

public class ADIS16448GyroAdapter implements GyroAdapter {

    private final ADIS16448_IMU gyro;

    public ADIS16448GyroAdapter(SysIdConfig config) {
        var port = SysId.getSPIPort(config.gyroConnector);

        IMUAxis axis = IMUAxis.kZ;
        if ("X".equalsIgnoreCase(config.gyroAxis)) {
            axis = IMUAxis.kX;
        } else if ("Y".equalsIgnoreCase(config.gyroAxis)) {
            axis = IMUAxis.kY;
        }

        this.gyro = new ADIS16448_IMU(axis, port, CalibrationTime._8s);
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
