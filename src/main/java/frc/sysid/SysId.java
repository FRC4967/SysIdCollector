package frc.sysid;

import java.util.List;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.SparkMaxRelativeEncoder;

import edu.wpi.first.wpilibj.SPI;
import frc.sysid.SysIdConfig.EncoderConfig;
import frc.sysid.SysIdConfig.MotorConfig;
import frc.sysid.encoder.CANCoderEncoderAdapter;
import frc.sysid.encoder.CTREEncoderAdapter;
import frc.sysid.encoder.EncoderAdapter;
import frc.sysid.encoder.RoboRIOEncoderAdapter;
import frc.sysid.encoder.SparkMaxEncoderAdapter;
import frc.sysid.gyro.ADIS16448GyroAdapter;
import frc.sysid.gyro.ADIS16470GyroAdapter;
import frc.sysid.gyro.ADXRS450GyroAdapter;
import frc.sysid.gyro.AnalogGyroAdapter;
import frc.sysid.gyro.GyroAdapter;
import frc.sysid.gyro.PigeonGyroAdapter;
import frc.sysid.motor.CTREMotorControllerAdapter;
import frc.sysid.motor.MotorControllerAdapter;
import frc.sysid.motor.SparkMaxMotorControllerAdapter;
import frc.sysid.motor.SparkMotorControllerAdapter;

public class SysId {

    public static MotorControllerAdapter setupMotorController(MotorConfig motorConfig) {

        MotorControllerAdapter controller = null;

        switch (motorConfig.controllerName) {

            case "TalonSRX":
            case "TalonFX":
            case "VictorSPX":
                controller = new CTREMotorControllerAdapter(motorConfig);
                break;

            case "SPARK MAX (Brushless)":
            case "SPARK MAX (Brushed)":
                controller = new SparkMaxMotorControllerAdapter(motorConfig);
                break;

            case "Venom":
            default:
                controller = new SparkMotorControllerAdapter(motorConfig);
                break;

        }

        System.out.println("Setup " + motorConfig.controllerName);
        return controller;

    }

    public static EncoderAdapter setupEncoder(EncoderConfig config, MotorControllerAdapter controllerAdapter) {

        EncoderAdapter encoderAdapter = null;

        Object controller = controllerAdapter.unwrapMotorController();

        switch (config.encoderType) {

            case "Built-in":
            
                // CTRE controllers
                if (controller instanceof BaseMotorController) {
                    if (controller instanceof WPI_TalonSRX) {
                        System.out.println("Setup Built-in+TalonSRX");
                        encoderAdapter = new CTREEncoderAdapter((BaseMotorController) controller,
                                FeedbackDevice.QuadEncoder);
                    } else {
                        System.out.println("Setup Built-in+TalonFX");
                        encoderAdapter = new CTREEncoderAdapter((BaseMotorController) controller,
                                FeedbackDevice.IntegratedSensor);
                    }
                }
                break;

            case "Encoder Port":

                if (controller instanceof CANSparkMax) {
                    var sparkMaxController = (CANSparkMax) controller;

                    if (sparkMaxController.getMotorType() == MotorType.kBrushless) {
                        System.out.println("Setup SPARK MAX (Brushless) Encoder Port");
                        encoderAdapter = new SparkMaxEncoderAdapter(sparkMaxController,
                                SparkMaxRelativeEncoder.Type.kHallSensor, true, config.cpr);
                    } else {
                        System.out.println("Setup SPARK MAX (Brushed) Encoder Port");
                        encoderAdapter = new SparkMaxEncoderAdapter(sparkMaxController,
                                SparkMaxRelativeEncoder.Type.kQuadrature, true, config.cpr);
                    }

                }
                break;

            case "Data Port":

                if (controller instanceof CANSparkMax) {
                    System.out.println("Setup SPARK MAX Data Port");
                    encoderAdapter = new SparkMaxEncoderAdapter((CANSparkMax) controller,
                            SparkMaxRelativeEncoder.Type.kQuadrature, false, config.cpr);

                }
                break;

            case "Tachometer":

                if (controller instanceof BaseMotorController) {
                    System.out.println("Setup Tachometer");
                    encoderAdapter = new CTREEncoderAdapter((BaseMotorController) controller,
                            FeedbackDevice.Tachometer);
                }
                break;

            case "CANCoder":
                System.out.println("Setup CANCoder");
                encoderAdapter = new CANCoderEncoderAdapter(config.ports[0]);
                break;

            default:
                System.out.println("Setup roboRIO quadrature");
                encoderAdapter = new RoboRIOEncoderAdapter(config.ports[0], config.ports[1], config.inverted,
                        config.isEncoding);

                break;

        }

        if (encoderAdapter == null) {
            throw new RuntimeException(
                    "Unsupported controller type for Encoder Port: " + controller.getClass());

        }

        encoderAdapter.configure(config);

        return encoderAdapter;
    }

    public static GyroAdapter setupGyro(SysIdConfig config,
            List<MotorControllerAdapter> leftControllers,
            List<MotorControllerAdapter> rightControllers) {

        GyroAdapter gyroAdapter = null;

        switch (config.gyro) {

            case "Pigeon":
            case "Pigeon2":
                gyroAdapter = new PigeonGyroAdapter(config, leftControllers, rightControllers);
                break;

            case "ADIS16448":
                gyroAdapter = new ADIS16448GyroAdapter(config);
                break;

            case "ADIS16470":
                gyroAdapter = new ADIS16470GyroAdapter(config);
                break;

            case "ADXRS450":
                gyroAdapter = new ADXRS450GyroAdapter(config);
                break;

            case "Analog Gyro":
                gyroAdapter = new AnalogGyroAdapter(config);
                break;
        }

        gyroAdapter.configure(config);
        return gyroAdapter;
    }

    public static SPI.Port getSPIPort(String spiPortName) {
        System.out.println("Setup " + spiPortName);
        switch (spiPortName) {
            case "SPI (Onboard CS0)":
                return SPI.Port.kOnboardCS0;
            case "SPI (Onboard CS1)":
                return SPI.Port.kOnboardCS1;
            case "SPI (Onboard CS2)":
                return SPI.Port.kOnboardCS2;
            case "SPI (Onboard CS3)":
                return SPI.Port.kOnboardCS3;
            case "SPI (MXP)":
                return SPI.Port.kMXP;
            default:
                throw new RuntimeException(spiPortName + " is not a supported SPI Port");
        }
    }

    public static void setMotorControllers(double motorVoltage, List<MotorControllerAdapter> controllers) {
        for (var controller : controllers) {
            controller.setVoltage(motorVoltage);
        }
    }

}
