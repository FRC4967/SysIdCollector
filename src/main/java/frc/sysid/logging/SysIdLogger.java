package frc.sysid.logging;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.Threads;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.sysid.motor.MotorControllerAdapter;

public class SysIdLogger {

    /**
     * The initial size of the data collection vectors, set to be large enough so
     * that we avoid resizing the vector during data collection. Determined by: 20
     * seconds of test data * 200 samples/second * 9 doubles/sample(320kB of
     * reserved data).
     */
    protected static final int kDataVectorSize = 36000;

    /**
     * The commanded motor voltage. Either as a rate (V/s) for the quasistatic
     * test or as a voltage (V) for the dynamic test.
     */
    protected double m_voltageCommand = 0.0;

    /**
     * The voltage that the motors should be set to.
     */
    protected double m_motorVoltage = 0.0;

    /**
     * Keeps track of the current timestamp for data collection purposes.
     */
    protected double m_timestamp = 0.0;

    /**
     * The timestamp of when the test starts. Mainly used to keep track of the
     * test running for too long.
     */
    protected double m_startTime = 0.0;

    /**
     * Determines for Drivetrain tests if the robot should be spinning (value sent
     * via NT).
     */
    protected boolean m_rotate = false;

    /**
     * The test that is running (e.g. Quasistatic or Dynamic).
     */
    protected String m_testType;

    /**
     * The mechanism that is being characterized (sent via NT).
     */
    protected String m_mechanism;

    /**
     * Stores all of the collected data.
     */
    protected List<Double> m_data;

    protected int m_ackNum = 0;

    private static final int kThreadPriority = 15;
    private static final int kHALThreadPriority = 40;

    /**
     * Creates the SysId logger, disables live view telemetry, sets up the
     * following NT Entries: "SysIdAutoSpeed", "SysIdRotate", "SysIdTelemetry"
     */
    public SysIdLogger() {
        System.out.println("Initializing logger");
        m_data = new ArrayList<>(kDataVectorSize);

        LiveWindow.disableAllTelemetry();

        SmartDashboard.putNumber("SysIdVoltageCommand", 0.0);
        SmartDashboard.putString("SysIdTestType", "");
        SmartDashboard.putString("SysIdTest", "");
        SmartDashboard.putBoolean("SysIdRotate", false);
        SmartDashboard.putBoolean("SysIdOverflow", false);
        SmartDashboard.putBoolean("SysIdWrongMech", false);
        SmartDashboard.putNumber("SysIdAckNumber", m_ackNum);
    }

    /**
     * Code that should be run to initialize the logging routine. Should be called
     * in `AutonomousInit()`.
     */
    public void initLogging() {

        m_mechanism = SmartDashboard.getString("SysIdTest", "");

        if (isWrongMechanism()) {
            SmartDashboard.putBoolean("SysIdWrongMech", true);
        }

        m_testType = SmartDashboard.getString("SysIdTestType", "");
        m_rotate = SmartDashboard.getBoolean("SysIdRotate", false);
        m_voltageCommand = SmartDashboard.getNumber("SysIdVoltageCommand", 0.0);
        m_startTime = Timer.getFPGATimestamp();
        m_data.clear();
        SmartDashboard.putString("SysIdTelemetry", "");
        m_ackNum = (int) SmartDashboard.getNumber("SysIdAckNumber", 0);

    }

    /**
     * Sends data after logging is complete.
     *
     * Called in DisabledInit().
     */
    public void sendData() {
        System.out.println(String.format("Collected: {} data points.", m_data.size()));

        SmartDashboard.putBoolean("SysIdOverflow", m_data.size() >= kDataVectorSize);

        StringBuilder ss = new StringBuilder();

        for (int i = 0; i < m_data.size(); ++i) {
            ss.append(m_data.get(i).toString());

            if (i < m_data.size() - 1) {
                ss.append(",");
            }
        }

        String type = "Dynamic".equals(m_testType) ? "fast" : "slow";
        String direction = m_voltageCommand > 0 ? "forward" : "backward";
        String test = String.format("%s-%s", type, direction);

        SmartDashboard.putString("SysIdTelemetry",
                String.format("%s;%s", test, ss.toString()));
        SmartDashboard.putNumber("SysIdAckNumber", ++m_ackNum);

        reset();
    }

    /**
     * Clears the data entry when sysid logger acknowledges that it received data.
     */
    public void clearWhenReceived() {
        if (SmartDashboard.getNumber("SysIdAckNumber", 0.0) > m_ackNum) {
            SmartDashboard.putString("SysIdTelemetry", "");
            m_ackNum = (int) SmartDashboard.getNumber("SysIdAckNumber", 0.0);
        }
    }

    /**
     * Makes the current execution thread of the logger a real-time thread which
     * will make it scheduled more consistently.
     */
    public static void updateThreadPriority() {
        if (!RobotBase.isSimulation()) {
            if (!Notifier.setHALThreadPriority(true, kHALThreadPriority) ||
                    !Threads.setCurrentThreadPriority(true, kThreadPriority)) {
                throw new RuntimeException("Setting the RT Priority failed\n");
            }
        }
    }

    /**
     * Utility function for getting motor controller voltage
     *
     * @param controllers     A set of motor controllers powering a mechanism.
     * @param controllerNames The names of the motor controllers.
     * @return The average of the measured voltages of the motor controllers.
     */
    public double measureVoltage(List<MotorControllerAdapter> controllers) {
        double sum = 0.0;

        for (MotorControllerAdapter controller : controllers) {
            sum += controller.getMotorOutputVoltage();
        }

        return sum / controllers.size();
    }

    /**
     * Updates the autospeed and robotVoltage
     */
    protected void updateData() {
        m_timestamp = Timer.getFPGATimestamp();

        // Don't let robot move if it's characterizing the wrong mechanism
        if (isWrongMechanism()) {
            m_motorVoltage = 0;
            System.out.println("Wrong mechanism");
            return;
        }

        SmartDashboard.putString("m_testType", m_testType);
        if ("Quasistatic".equals(m_testType)) {
            SmartDashboard.putNumber("m_voltageCommand", m_voltageCommand);
            SmartDashboard.putNumber("time", (m_timestamp - m_startTime));
            m_motorVoltage = m_voltageCommand * (m_timestamp - m_startTime);
        } else if ("Dynamic".equals(m_testType)) {
            m_motorVoltage = m_voltageCommand;
        } else {
            m_motorVoltage = 0.0;
        }

        SmartDashboard.putNumber("SysIdMotorVoltage", m_motorVoltage);
    }

    /**
     * Reset data before next test.
     */
    protected void reset() {
        m_motorVoltage = 0.0;
        m_timestamp = 0.0;
        m_startTime = 0.0;
        m_data.clear();
    }

    /**
     * Determines if the logger is collecting data for an unsupported mechanism.
     *
     * @returns True if the logger is characterizing an unsupported mechanism
     *          type.
     */
    protected boolean isWrongMechanism() {
        return false;
    }

}
