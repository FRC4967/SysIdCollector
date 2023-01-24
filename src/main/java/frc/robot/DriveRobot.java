// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.interfaces.Gyro;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.sysid.SysId;
import frc.sysid.SysIdConfig;
import frc.sysid.SysIdConfig.MotorConfig;
import frc.sysid.encoder.EncoderAdapter;
import frc.sysid.logging.SysIdDrivetrainLogger;
import frc.sysid.motor.MotorControllerAdapter;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the
 * name of this class or
 * the package after creating this project, you must also update the
 * build.gradle file in the
 * project.
 */
public class DriveRobot extends TimedRobot {

  private List<MotorControllerAdapter> leftControllers = new ArrayList<>();
  private List<MotorControllerAdapter> rightControllers = new ArrayList<>();

  private final SysIdConfig config;

  private EncoderAdapter leftEncoder;
  private EncoderAdapter rightEncoder;

  private Gyro gyro;

  private SysIdDrivetrainLogger m_logger = new SysIdDrivetrainLogger();

  public DriveRobot() {
    super(0.005);

    System.out.println("Read JSON");

    try {
      config = SysIdConfig.getDeployedConfig();

      System.out.println("Setup motors");
      for (int i = 0; i < config.leftPorts.length; i++) {

        MotorConfig leftMotorConfig = config.getLeftMotorConfig(i);
        MotorControllerAdapter leftMotorController = SysId.setupMotorController(leftMotorConfig);
        leftControllers.add(leftMotorController);
        leftMotorController.configure(leftMotorConfig, leftControllers);



        MotorConfig rightMotorConfig = config.getRightMotorConfig(i);
        MotorControllerAdapter rightMotorController = SysId.setupMotorController(rightMotorConfig);
        rightControllers.add(rightMotorController);
        rightMotorController.configure(rightMotorConfig, rightControllers);

      }

      System.out.println("Setup encoders");
      leftEncoder = SysId.setupEncoder(config.getLeftEncoderConfig(), leftControllers.get(0));
      rightEncoder = SysId.setupEncoder(config.getRightEncoderConfig(), rightControllers.get(0));

      System.out.println("Setup gyro");
      gyro = SysId.setupGyro(config, leftControllers, rightControllers);

      SysIdDrivetrainLogger.updateThreadPriority();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * This function is run when the robot is first started up and should be used
   * for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    setNetworkTablesFlushEnabled(true);
    SmartDashboard.putString("SysIdTest", "Drivetrain");
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items
   * like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different
   * autonomous modes using the dashboard. The sendable chooser code works with
   * the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the
   * chooser code and
   * uncomment the getString line to get the auto name from the text box below the
   * Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure
   * below with additional strings. If using the SendableChooser make sure to add
   * them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_logger.initLogging();
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    m_logger.log(
        m_logger.measureVoltage(leftControllers),
        m_logger.measureVoltage(rightControllers),
        leftEncoder.getPosition(),
        rightEncoder.getPosition(),
        leftEncoder.getRate(),
        rightEncoder.getRate(),
        gyro.getAngle(), gyro.getRate());

    SysId.setMotorControllers(m_logger.getLeftMotorVoltage(), leftControllers);
    SysId.setMotorControllers(m_logger.getRightMotorVoltage(), rightControllers);
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
    pushNTDiagnostics();
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {
    SysId.setMotorControllers(0, leftControllers);
    SysId.setMotorControllers(0, rightControllers);
    System.out.println("Robot disabled");
    m_logger.sendData();
  }

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {
    pushNTDiagnostics();
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
    pushNTDiagnostics();
  }

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {
  }

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {
  }

  private void pushNTDiagnostics() {
    SmartDashboard.putNumber("Left Voltage", m_logger.measureVoltage(leftControllers));
    SmartDashboard.putNumber("Right Voltage", m_logger.measureVoltage(rightControllers));

    SmartDashboard.putNumber("Left Position", leftEncoder.getPosition());
    SmartDashboard.putNumber("Right Position", rightEncoder.getPosition());
    SmartDashboard.putNumber("Left Velocity", leftEncoder.getRate());
    SmartDashboard.putNumber("Right Velocity", rightEncoder.getRate());

    SmartDashboard.putNumber("Gyro Reading", gyro.getAngle());
    SmartDashboard.putNumber("Gyro Rate", gyro.getRate());
  }

}
