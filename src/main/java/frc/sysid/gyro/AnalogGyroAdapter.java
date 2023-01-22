package frc.sysid.gyro;

import edu.wpi.first.wpilibj.AnalogGyro;
import frc.sysid.SysIdConfig;

public class AnalogGyroAdapter extends BaseGyroAdapter {

    public AnalogGyroAdapter(SysIdConfig config) {
        super(new AnalogGyro(Integer.parseInt(config.gyroConnector)));
    }
    
}
