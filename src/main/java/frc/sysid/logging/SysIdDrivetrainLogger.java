package frc.sysid.logging;

import java.util.Arrays;
import java.util.List;

public class SysIdDrivetrainLogger extends SysIdLogger {

    private double m_primaryMotorVoltage = 0;
    private double m_secondaryMotorVoltage = 0;

    /**
     * The users should their left motors to what this returns AFTER calling log.
     *
     * @returns The voltage that the left motor(s) should be set to.
     */
    public double getLeftMotorVoltage() {
        return m_primaryMotorVoltage;
    }

    /**
     * The users should set their right motors to what this returns AFTER calling
     * log.
     *
     * @returns The voltage that the right motor(s) should be set to.
     */
    public double getRightMotorVoltage() {
        return m_secondaryMotorVoltage;
    }

    /**
     * Logs data for a drivetrain mechanism.
     *
     * When SendData() is called it outputs data in the form: timestamp, l
     * voltage, r voltage, l position, r position, l velocity, r velocity, angle,
     * angular rate
     *
     * @param leftVoltage   the recorded voltage of the left motors
     * @param rightVoltage  the recorded voltage of the right motors
     * @param leftPosition  the recorded rotations of the left shaft
     * @param rightPosition the recorded rotations of the right shaft
     * @param leftVelocity  the recorded rotations per second of the left shaft
     * @param rightVelocity the recorded rotations per second or the right shaft
     * @param measuredAngle the recorded angle of they gyro
     * @param angularRate   the recorded angular rate of the gyro in radians per
     *                      second
     */
    public void log(double leftVoltage, double rightVoltage, double leftPosition, double rightPosition,
            double leftVelocity, double rightVelocity, double measuredAngle, double angularRate) {


        updateData();
        if (m_data.size() < kDataVectorSize) {

            List<Double> arr = Arrays.asList(m_timestamp, leftVoltage, rightVoltage,
                    leftPosition, rightPosition, leftVelocity,
                    rightVelocity, measuredAngle, angularRate);
            m_data.addAll(arr);
        }

        m_primaryMotorVoltage = (m_rotate ? -1 : 1) * m_motorVoltage;
        m_secondaryMotorVoltage = m_motorVoltage;
    }

    @Override
    protected void reset() {
        super.reset();
        m_primaryMotorVoltage = 0;
        m_secondaryMotorVoltage = 0;
    }

    @Override
    protected boolean isWrongMechanism() {
        if ("Drivetrain".equals(m_mechanism) || "Drivetrain (Angular)".equals(m_mechanism)) {
            return false;
        }
        return true;
    }

}
