package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.commands.Drive_DriveManual;
import org.usfirst.frc.team3309.commands.Drive_RobotStateEstimator;
import org.usfirst.frc.team3309.lib.geometry.Pose2d;
import org.usfirst.frc.team4322.commandv2.Subsystem;

/*
 * The Drive subsystem. This is the big one.
 * The drivebase has 6 motor controllers, one solenoid, and 1 navx
 */
public class Drive extends Subsystem {

    private WPI_TalonSRX driveLeftMaster, driveRightMaster;
    private WPI_VictorSPX driveLeftSlave1, driveRightSlave1;
    private WPI_VictorSPX driveLeftSlave2, driveRightSlave2;

    private Solenoid shifter;

    private AHRS navx;

    private Drive_RobotStateEstimator driveRobotStateEstimator;

    public Drive() {
        driveRobotStateEstimator = new Drive_RobotStateEstimator();

        driveLeftMaster = new WPI_TalonSRX(Constants.DRIVE_LEFT_MASTER_TALON_ID);
        driveLeftSlave1 = new WPI_VictorSPX(Constants.DRIVE_LEFT_SLAVE_VICTOR_1_ID);
        driveLeftSlave2 = new WPI_VictorSPX(Constants.DRIVE_LEFT_SLAVE_VICTOR_2_ID);
        driveRightMaster = new WPI_TalonSRX(Constants.DRIVE_RIGHT_MASTER_TALON_ID);
        driveRightSlave1 = new WPI_VictorSPX(Constants.DRIVE_RIGHT_SLAVE_VICTOR_1_ID);
        driveRightSlave2 = new WPI_VictorSPX(Constants.DRIVE_RIGHT_SLAVE_VICTOR_2_ID);
        shifter = new Solenoid(Constants.DRIVE_SHIFTER_PCM_PORT);
        navx = new AHRS(SPI.Port.kMXP);

        //Configure Left Side of Drive
        driveRightMaster.configFactoryDefault();
        driveLeftMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        driveLeftMaster.configClosedloopRamp(Constants.DRIVE_CLOSED_LOOP_RAMP_RATE, 10);
        driveLeftMaster.config_kP(0, Constants.DRIVE_P, 10);
        driveLeftMaster.config_kD(0, Constants.DRIVE_D, 10);
        driveLeftMaster.config_kF(0, Constants.DRIVE_F, 10);
        driveLeftMaster.setNeutralMode(NeutralMode.Brake);

        driveLeftSlave1.follow(driveLeftMaster);
        driveLeftSlave2.follow(driveLeftMaster);

        //Configure Right Side of Drive
        driveRightMaster.configFactoryDefault();
        driveRightMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        driveRightMaster.configClosedloopRamp(Constants.DRIVE_CLOSED_LOOP_RAMP_RATE, 10);
        driveRightMaster.config_kP(0, Constants.DRIVE_P, 10);
        driveRightMaster.config_kD(0, Constants.DRIVE_D, 10);
        driveRightMaster.config_kF(0, Constants.DRIVE_F, 10);
        driveRightMaster.setNeutralMode(NeutralMode.Brake);

        driveRightSlave1.follow(driveRightMaster);
        driveRightSlave2.follow(driveRightMaster);

        addChild(driveLeftMaster);
        addChild(driveRightMaster);
        addChild(shifter);
        addChild(navx);
    }


    public double encoderCountsToInches(double counts) {
        return counts / Constants.DRIVE_ENCODER_COUNTS_PER_REV * (Math.PI * Constants.WHEEL_DIAMETER_INCHES);
    }

    public double encoderVelocityToInchesPerSecond(double encoderVelocity) {
        return encoderCountsToInches(encoderVelocity * 10.0 / 4096.0 * (Math.PI * Constants.WHEEL_DIAMETER_INCHES));
    }

    public double inchesToEncoderCounts(double inches) {
        return inches * (Constants.DRIVE_ENCODER_COUNTS_PER_REV / (Math.PI * Constants.WHEEL_DIAMETER_INCHES));
    }

    public double radiansPerSecondToTicksPer100ms(double rad_s) {
        return rad_s / (Math.PI * 2.0) * 4096.0 / 10.0;
    }


    public void reset() {
        driveLeftMaster.clearMotionProfileTrajectories();
        driveRightMaster.clearMotionProfileTrajectories();
        driveLeftMaster.setSelectedSensorPosition(0, 0, 0);
        driveRightMaster.setSelectedSensorPosition(0, 0, 0);
        zeroNavx();
        driveRobotStateEstimator.reset(Timer.getFPGATimestamp(), new Pose2d());
        driveRobotStateEstimator.start();
    }

    public void zeroNavx() {
        navx.zeroYaw();
    }

    public double getEncoderDistance() {
        return (getLeftEncoderDistance() + getRightEncoderDistance()) / 2.0;
    }

    public double getLeftEncoderDistance() {
        return driveLeftMaster.getSelectedSensorPosition(0);
    }

    public double getRightEncoderDistance() {
        return -driveRightMaster.getSelectedSensorPosition(0);
    }

    public double getEncoderVelocity() {
        return (getLeftEncoderVelocity() + getRightEncoderVelocity()) / 2.0;
    }

    public int getLeftEncoderVelocity() {
        return driveLeftMaster.getSelectedSensorVelocity(0);
    }

    public double getRightEncoderVelocity() {
        return -driveRightMaster.getSelectedSensorVelocity(0);
    }

    public double getAngularPosition() {
        return -navx.getAngle();
    }

    public double getAngularVelocity() {
        return navx.getRate();
    }

    public Drive_RobotStateEstimator getRobotStateEstimator() {
        return driveRobotStateEstimator;
    }

    public void setHighGear() {
        shifter.set(false);
    }

    public void setLowGear() {
        shifter.set(true);
    }

    public boolean inHighGear() {
        return !shifter.get();
    }

    public void setLeftRight(ControlMode mode, double left, double right) {
        if (mode == ControlMode.Velocity) {
            left *= 2.0;
            right *= 2.0;
        }
        driveLeftMaster.set(mode, -left);
        driveRightMaster.set(mode, right);
    }

    public void setLeftRight(ControlMode mode, DemandType demandType,
                             double left, double right,
                             double leftFeedforward, double rightFeedforward) {
        if (mode == ControlMode.Velocity) {
            left *= 2.0;
            right *= 2.0;
        }
        setLeft(mode, left, demandType, leftFeedforward);
        setRight(mode, right, demandType, rightFeedforward);
    }

    public void setLeft(ControlMode mode,
                        double left,
                        DemandType demandType,
                        double leftFeedforward) {
        driveLeftMaster.set(mode, left, demandType, leftFeedforward);

    }

    public void setNeutralMode(NeutralMode mode) {
        driveLeftMaster.setNeutralMode(mode);
        driveRightMaster.setNeutralMode(mode);
    }

    public void setRight(ControlMode mode,
                         double right, DemandType demandType,
                         double rightFeedforward) {
        driveRightMaster.set(mode, -right, demandType, -rightFeedforward);
    }

    public void outputToSmartdashboard() {
        driveRobotStateEstimator.outputToSmartDashboard();
        SmartDashboard.putNumber("Raw angle", getAngularPosition());
        SmartDashboard.putNumber("Encoder left", getLeftEncoderDistance());
        SmartDashboard.putNumber("Encoder right", getRightEncoderDistance());
        SmartDashboard.putNumber("Left encoder velocity", getLeftEncoderVelocity());
        SmartDashboard.putNumber("Right encoder velocity", getRightEncoderVelocity());
    }

    @Override
    public void initDefaultCommand() {
        setDefaultCommand(new Drive_DriveManual());
    }

}
