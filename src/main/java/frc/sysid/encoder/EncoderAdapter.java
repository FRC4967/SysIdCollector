package frc.sysid.encoder;

import frc.sysid.SysIdConfig.EncoderConfig;

public interface EncoderAdapter {
    public void configure(EncoderConfig config);
    public double getRate();
    public double getPosition();
}
