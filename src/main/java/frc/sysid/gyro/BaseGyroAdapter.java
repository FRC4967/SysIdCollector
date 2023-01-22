package frc.sysid.gyro;

import edu.wpi.first.math.util.Units;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import frc.sysid.SysIdConfig;

public abstract class BaseGyroAdapter implements GyroAdapter {

    private final Gyro gyro;

    public BaseGyroAdapter(Gyro gyro) {
        this.gyro = gyro;
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
