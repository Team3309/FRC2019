package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.commands.drive.DriveManual;
import org.usfirst.frc.team3309.lib.util.DriveSignal;
import org.usfirst.frc.team4322.commandv2.Subsystem;

/*
 * The Drive subsystem. This is the big one.
 * The drivebase has 6 motor controllers, one solenoid, and 1 navx
 */
public class Drive extends Subsystem {

    private WPI_TalonFX driveLeftMaster, driveRightMaster;

    private Solenoid shifter;

    private AHRS navx;

    public Drive() {

        driveLeftMaster = new WPI_TalonFX(Constants.DRIVE_LEFT_MASTER_FALCON_ID);
        WPI_TalonFX driveLeftSlave = new WPI_TalonFX(Constants.DRIVE_LEFT_SLAVE_FALCON_ID);
        driveRightMaster = new WPI_TalonFX(Constants.DRIVE_RIGHT_MASTER_FALCON_ID);
        WPI_TalonFX driveRightSlave = new WPI_TalonFX(Constants.DRIVE_RIGHT_SLAVE_FALCON_ID);
        shifter = new Solenoid(Constants.DRIVE_SHIFTER_PCM_PORT);
        navx = new AHRS(SPI.Port.kMXP);

        //Configure Left Side of Drive
        configMaster(driveLeftMaster);
        //configSlave(driveLeftSlave, driveLeftMaster);

        //Configure Right Side of Drive
        configMaster(driveRightMaster);
        //configSlave(driveRightSlave, driveRightMaster);
    }

    private void configMaster(WPI_TalonFX talon) {
        talon.configFactoryDefault();
        talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        talon.configClosedloopRamp(Constants.DRIVE_CLOSED_LOOP_RAMP_RATE);
        talon.configOpenloopRamp(Constants.DRIVE_OPEN_LOOP_RAMP_RATE, 10);

        talon.config_kP(0, Constants.kDriveVelocityP, 10);
        talon.config_kD(0, Constants.kDriveVelocityD, 10);
        talon.config_kF(0, Constants.kDriveVelocityF, 10);

        talon.setNeutralMode(NeutralMode.Brake);
        talon.setInverted(true);
        talon.setSensorPhase(false);
    }

    private void setPid(int slot) {
        driveLeftMaster.selectProfileSlot(slot, 0);
        driveRightMaster.selectProfileSlot(slot, 0);
    }

    private void configSlave(WPI_TalonFX slave, WPI_TalonFX master) {
        slave.configFactoryDefault();
        slave.follow(master);
        slave.setNeutralMode(NeutralMode.Brake);
        slave.setInverted(InvertType.FollowMaster);
    }

    public double encoderCountsToInches(double counts) {
        return counts / Constants.DRIVE_ENCODER_COUNTS_PER_REV * (Math.PI * Constants.WHEEL_DIAMETER_INCHES);
    }

    public double encoderVelocityToInchesPerSecond(double encoderVelocity) {
        return encoderCountsToInches(encoderVelocity * 10.0 / 4096.0 * (Math.PI * Constants.WHEEL_DIAMETER_INCHES));
    }

    public double inchesPerSecondToEncoderVelocity(double inchesPerSecond) {
        return inchesToEncoderCounts(inchesPerSecond / 10.0 * 4096.0 / (Math.PI * Constants.WHEEL_DIAMETER_INCHES));
    }

    public double degreesPerSecondToEncoderVelocity(double degreesPerSecond) {
        return degreesPerSecond * Constants.ENCODER_COUNTS_PER_DEGREE;
    }

    public double inchesToEncoderCounts(double inches) {
        return inches * (Constants.DRIVE_ENCODER_COUNTS_PER_REV / (Math.PI * Constants.WHEEL_DIAMETER_INCHES));
    }

    public double radiansPerSecondToTicksPer100ms(double rad_s) {
        return rad_s / (Math.PI * 2.0) * 4096.0 / 10.0;
    }

    public void autoVelocity(double leftEncoderCountsPerSec, double rightEncoderCountsPerSec) {
        driveLeftMaster.set(ControlMode.Velocity, leftEncoderCountsPerSec);
        driveRightMaster.set(ControlMode.Velocity, -rightEncoderCountsPerSec);
    }

    public void reset() {
        driveLeftMaster.clearMotionProfileTrajectories();
        driveRightMaster.clearMotionProfileTrajectories();
        driveLeftMaster.setSelectedSensorPosition(0, 0, 0);
        driveRightMaster.setSelectedSensorPosition(0, 0, 0);
        zeroNavx();
    }

    public void zeroEncoders() {
        driveLeftMaster.setSelectedSensorPosition(0, 0, 0);
        driveRightMaster.setSelectedSensorPosition(0, 0, 0);
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

    public double getVelocityX() { return navx.getVelocityX(); }

    public double getVelocityY() { return navx.getVelocityY(); }

    public void setHighGear() {
        shifter.set(true);
    }

    public void setLowGear() {
        shifter.set(false);
    }

    public boolean inHighGear() {
        return !shifter.get();
    }

    public void setLeftRight(ControlMode mode, double left, double right) {
        driveLeftMaster.set(mode, left);
        driveRightMaster.set(mode, -right);
    }

    public void setArcade(ControlMode mode, double speed, double turn) {
        setLeftRight(mode, speed + turn, speed - turn);
    }

    public void setLeftRight(ControlMode mode, DriveSignal signal) {
        setLeftRight(mode, signal.getLeft(), signal.getRight());
    }

    public void setLeftRight(ControlMode mode, DemandType demandType,
                             double left, double right,
                             double leftFeedforward, double rightFeedforward) {
        setLeft(mode, left, demandType, leftFeedforward);
        setRight(mode, right, demandType, rightFeedforward);
    }

    private void setLeft(ControlMode mode,
                         double left,
                         DemandType demandType,
                         double leftFeedforward) {
        driveLeftMaster.set(mode, left, demandType, leftFeedforward);
    }

    public void setNeutralMode(NeutralMode mode) {
        driveLeftMaster.setNeutralMode(mode);
        driveRightMaster.setNeutralMode(mode);
    }

    private void setRight(ControlMode mode,
                          double right, DemandType demandType,
                          double rightFeedforward) {
        driveRightMaster.set(mode, -right, demandType, -rightFeedforward);
    }

    public void outputToDashboard() {
        SmartDashboard.putNumber("Drive left power get", driveLeftMaster.getMotorOutputPercent());
        SmartDashboard.putNumber("Drive right power get", driveRightMaster.getMotorOutputPercent());
        SmartDashboard.putNumber("Raw angle", getAngularPosition());
        SmartDashboard.putNumber("Raw angular velocity", getAngularVelocity());
        SmartDashboard.putNumber("Raw X-axis velocity", getVelocityX());
        SmartDashboard.putNumber("Raw Y-axis velocity", getVelocityY());
        SmartDashboard.putNumber("Encoder left", getLeftEncoderDistance());
        SmartDashboard.putNumber("Encoder right", getRightEncoderDistance());
        SmartDashboard.putNumber("Left encoder velocity", getLeftEncoderVelocity());
        SmartDashboard.putNumber("Right encoder velocity", getRightEncoderVelocity());
        SmartDashboard.putNumber("Drive left 1 current", Robot.pdp.getCurrent(Constants.kPdpChannelDriveLeft1));
        SmartDashboard.putNumber("Drive left 2 current", Robot.pdp.getCurrent(Constants.kPdpChannelDriveLeft2));
        SmartDashboard.putNumber("Drive left 3 current", Robot.pdp.getCurrent(Constants.kPdpChannelDriveLeft3));
        SmartDashboard.putNumber("Drive right 7 current", Robot.pdp.getCurrent(Constants.kPdpChannelDriveRight7));
        SmartDashboard.putNumber("Drive right 8 current", Robot.pdp.getCurrent(Constants.kPdpChannelDriveRight8));
        SmartDashboard.putNumber("Drive right 9 current", Robot.pdp.getCurrent(Constants.kPdpChannelDriveRight9));
    }

    @Override
    public void initDefaultCommand() {
        setDefaultCommand(new DriveManual());
    }

}
