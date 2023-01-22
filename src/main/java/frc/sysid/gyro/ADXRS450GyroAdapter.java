package frc.sysid.gyro;

import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import frc.sysid.SysIdConfig;
import frc.sysid.SysId;

public class ADXRS450GyroAdapter extends BaseGyroAdapter {

    public ADXRS450GyroAdapter(SysIdConfig config) {
        super(createGyro(config));
    }    

    private static Gyro createGyro(SysIdConfig config) {
        var port = SysId.getSPIPort(config.gyroConnector);
        return new ADXRS450_Gyro(port);
    }

}
