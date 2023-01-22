package frc.sysid.gyro;

import edu.wpi.first.wpilibj.interfaces.Gyro;
import frc.sysid.SysIdConfig;

public interface GyroAdapter extends Gyro {    
    public void configure(SysIdConfig config);
    public double getRate();

}
