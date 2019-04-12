package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Pathfinder;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.commands.drive.DriveManual;
import org.usfirst.frc.team3309.lib.RobotPos;
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

    //Zero Offsets:
    private double mLeftZeroOffset = 0;
    private double mRightZeroOffset = 0;
    private double mGyroOffset = 0;

    //Odometery variables:
    private double mLastPos, mCurrentPos, mDeltaPos;
    double x, y, theta;
    private Notifier odometery;
    private static final double kOdometeryFix = 0.973;

    private double kFriction = 0; //0.1

    public Drive() {

        driveLeftMaster = new WPI_TalonSRX(Constants.DRIVE_LEFT_MASTER_TALON_ID);
        driveLeftSlave1 = new WPI_VictorSPX(Constants.DRIVE_LEFT_SLAVE_VICTOR_1_ID);
        driveLeftSlave2 = new WPI_VictorSPX(Constants.DRIVE_LEFT_SLAVE_VICTOR_2_ID);
        driveRightMaster = new WPI_TalonSRX(Constants.DRIVE_RIGHT_MASTER_TALON_ID);
        driveRightSlave1 = new WPI_VictorSPX(Constants.DRIVE_RIGHT_SLAVE_VICTOR_1_ID);
        driveRightSlave2 = new WPI_VictorSPX(Constants.DRIVE_RIGHT_SLAVE_VICTOR_2_ID);
        shifter = new Solenoid(Constants.DRIVE_SHIFTER_PCM_PORT);
        navx = new AHRS(SPI.Port.kMXP);

        //Configure Left Side of Drive
        configMaster(driveLeftMaster);
        configSlave(driveLeftSlave1, driveLeftMaster);
        configSlave(driveLeftSlave2, driveLeftMaster);

        //Configure Right Side of Drive
        configMaster(driveRightMaster);
        configSlave(driveRightSlave1, driveRightMaster);
        configSlave(driveRightSlave2, driveRightMaster);

        // auto output for NetworkTables
        addChild(driveLeftMaster);
        addChild(driveRightMaster);
        addChild(shifter);
        addChild(navx);

        //Zero Odometery:
        x = 0;
        y = 0;
        theta = 0;

        odometery = new Notifier(() -> {
            mCurrentPos = (getLeftSensorPosition() + getRightSensorPosition()) / 2.0;
            mDeltaPos = mCurrentPos - mLastPos;
            theta = getGyroAngle();
            x += kOdometeryFix * Math.cos(Pathfinder.d2r((theta))) * mDeltaPos;
            y += kOdometeryFix * Math.sin(Pathfinder.d2r((theta))) * mDeltaPos;
            mLastPos = mCurrentPos;
        });
    }

    private void configMaster(WPI_TalonSRX talon) {
        talon.configFactoryDefault();
        talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        talon.configClosedloopRamp(Constants.DRIVE_CLOSED_LOOP_RAMP_RATE, 10);
        talon.configOpenloopRamp(Constants.DRIVE_OPEN_LOOP_RAMP_RATE, 10);

        talon.config_kP(0, Constants.DRIVE_P, Constants.kCTREtimeout);
        talon.config_kI(0, Constants.DRIVE_I, Constants.kCTREtimeout);
        talon.config_kD(0, Constants.DRIVE_D, Constants.kCTREtimeout);
        talon.config_kF(0, Constants.DRIVE_F, Constants.kCTREtimeout);


        talon.setNeutralMode(NeutralMode.Brake);
        talon.setInverted(true);
        talon.setSensorPhase(true);
        addChild(talon);
    }

    private void configSlave(WPI_VictorSPX slave, WPI_TalonSRX master) {
        slave.configFactoryDefault();
        slave.follow(master);
        slave.setNeutralMode(NeutralMode.Brake);
        slave.setInverted(InvertType.FollowMaster);
        addChild(slave);
    }

    /**
     * Set Raw Speed Method
     *
     * @param left  Percent Output
     * @param right Percent Output
     */
    public void setRawSpeed(double left, double right) {
        driveLeftMaster.set(ControlMode.PercentOutput, left);
        driveRightMaster.set(ControlMode.PercentOutput, right);
    }

    /**
     * Set Velocity Method
     *
     * @param left  Feet per Second
     * @param right Feet per Second
     */
    public void setVelocity(double left, double right) {
        driveLeftMaster.set(ControlMode.Velocity, left * Constants.kFPS2NativeU, DemandType.ArbitraryFeedForward, kFriction);
        driveRightMaster.set(ControlMode.Velocity, right * Constants.kFPS2NativeU, DemandType.ArbitraryFeedForward, kFriction);
    }

    /**
     * Zero Sensor
     *
     * <p> Zeroes all sensors (encoders + gyro) and odometery information </p>
     */
    public void zeroSensor() {
        mLeftZeroOffset = driveLeftMaster.getSelectedSensorPosition(Constants.kCTREpidIDX);
        mRightZeroOffset = driveRightMaster.getSelectedSensorPosition(Constants.kCTREpidIDX);
        mGyroOffset = navx.getFusedHeading();

        x = 0;
        y = 0;
        theta = 0;

        mCurrentPos = 0;
        mDeltaPos = 0;
        mLastPos = 0;
    }

    public void setOdometery(RobotPos pos) {
        x = pos.getX();
        y = pos.getY();
        theta = pos.getHeading();
        // mGyroOffset = (mGyro.getFusedHeading() + pos.getHeading());
        mGyroOffset = (navx.getFusedHeading() - pos.getHeading());
    }

    /**
     * Start Odometery Method
     *
     * <p> Starts tracking the robot position </p>
     *
     * @param period timestep to update at.
     */
    public void startOdometery(double period) {
        odometery.startPeriodic(period);
    }

    /**
     * Stop Odometery Method
     *
     * <p> Stops tracking the robot position </p>
     */
    public void stopOdometery() {
        odometery.stop();
    }

    /**
     * Get Robot Position Method.
     *
     * @return The position of the robot.
     */
    public RobotPos getRobotPos() {
        return new RobotPos(x, y, theta);
    }

    /**
     * @return Left Sensor Position in Feet
     */
    public double getLeftSensorPosition() {
        return Constants.kTicks2Feet * (driveLeftMaster.getSelectedSensorPosition(Constants.kCTREpidIDX) - mLeftZeroOffset);
    }

    /**
     * @return Right Sensor Position in Feet
     */
    public double getRightSensorPosition() {
        return Constants.kTicks2Feet * (driveRightMaster.getSelectedSensorPosition(Constants.kCTREpidIDX) - mRightZeroOffset);
    }

    /**
     * @return Left Sensor Velocity in Feet per Second
     */
    public double getLeftSensorVelocity() {
        return Constants.kNativeU2FPS * driveLeftMaster.getSelectedSensorVelocity(Constants.kCTREpidIDX);
    }

    /**
     * @return Left Sensor Velocity in Feet per Second
     */
    public double getRightSensorVelocity() {
        return Constants.kNativeU2FPS * driveRightMaster.getSelectedSensorVelocity(Constants.kCTREpidIDX);
    }


    public void reset() {
        driveLeftMaster.clearMotionProfileTrajectories();
        driveRightMaster.clearMotionProfileTrajectories();
        driveLeftMaster.setSelectedSensorPosition(0, 0, 0);
        driveRightMaster.setSelectedSensorPosition(0, 0, 0);
    }

    /**
     * Get Gyro Angle.
     *
     * @return The fused heading from the sensors.
     */
    public double getGyroAngle() {
        return navx.getFusedHeading() - mGyroOffset;
    }

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
        driveLeftMaster.set(mode, -left);
        driveRightMaster.set(mode, right);
    }

    public void setLeftRight(ControlMode mode, DemandType demandType,
                             double left, double right,
                             double leftFeedforward, double rightFeedforward) {
        setLeft(mode, left, demandType, leftFeedforward);
        setRight(mode, right, demandType, rightFeedforward);
    }

    public void setLeft(ControlMode mode,
                        double left,
                        DemandType demandType,
                        double leftFeedforward) {
        driveLeftMaster.set(mode, left, demandType, leftFeedforward);

    }

    public void setRight(ControlMode mode,
                         double right, DemandType demandType,
                         double rightFeedforward) {
        driveRightMaster.set(mode, -right, demandType, -rightFeedforward);
    }

    public void outputToDashboard() {
        SmartDashboard.putNumber("Drive left power get", driveLeftMaster.getMotorOutputPercent());
        SmartDashboard.putNumber("Drive right power get", driveRightMaster.getMotorOutputPercent());
        SmartDashboard.putNumber("Raw angle", getGyroAngle());
    }

    @Override
    public void initDefaultCommand() {
        setDefaultCommand(new DriveManual());
    }

}
