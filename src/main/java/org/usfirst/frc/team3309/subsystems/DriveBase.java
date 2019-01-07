package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import com.kauailabs.navx.frc.AHRS;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.commands.DriveBase_DriveManual;
import org.usfirst.frc.team4322.commandv2.Subsystem;
import org.usfirst.frc.team4322.motion.RobotPositionIntegrator;

/*
 * The DriveBase subsystem. This is the big one.
 * The drivebase has 6 motor controllers, one solenoid, and 1 navx
 */
public class DriveBase extends Subsystem {

    private WPI_TalonSRX driveLeftMaster,driveRightMaster;
    private WPI_VictorSPX driveLeftSlave1,driveRightSlave1;
    private WPI_VictorSPX driveLeftSlave2,driveRightSlave2;
    private Solenoid shifter;
    private AHRS navx;

    public DriveBase() {
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
        driveLeftMaster.config_kP(0, Constants.DRIVEBASE_P, 10);
        driveLeftMaster.config_kD(0, Constants.DRIVEBASE_I, 10);
        driveLeftMaster.config_kF(0, Constants.DRIVEBASE_D, 10);
        driveLeftMaster.setNeutralMode(NeutralMode.Brake);

        driveLeftSlave1.follow(driveLeftMaster);
        driveLeftSlave2.follow(driveLeftMaster);

        //Configure Right Side of Drive
        driveRightMaster.configFactoryDefault();
        driveRightMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
        driveRightMaster.config_kP(0, Constants.DRIVEBASE_P, 10);
        driveRightMaster.config_kD(0, Constants.DRIVEBASE_I, 10);
        driveRightMaster.config_kF(0, Constants.DRIVEBASE_D, 10);
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

    public double inchesToEncoderCounts(double inches) {
        return inches * (Constants.DRIVE_ENCODER_COUNTS_PER_REV / (Math.PI * Constants.WHEEL_DIAMETER_INCHES));
    }


    public void reset() {
        driveLeftMaster.clearMotionProfileTrajectories();
        driveRightMaster.clearMotionProfileTrajectories();
        driveLeftMaster.setSelectedSensorPosition(0, 0,0);
        driveRightMaster.setSelectedSensorPosition(0, 0,0);
        navx.zeroYaw();
    }


    public void zeroNavx() {
        navx.zeroYaw();
    }

    public double getPosition() {
        return (getLeftPosition() + getRightPosition()) / 2.0;
    }

    public double getLeftPosition() {
        return driveLeftMaster.getSelectedSensorPosition(0);
    }

    public double getRightPosition() {
        return -driveRightMaster.getSelectedSensorPosition(0);
    }

    public double getVelocity() {
        return (getLeftVelocity() + getRightVelocity()) / 2.0;
    }

    public int getLeftVelocity() {
        return driveLeftMaster.getSelectedSensorVelocity(0);
    }

    public double getRightVelocity() {
        return -driveRightMaster.getSelectedSensorVelocity(0);
    }

    public double getAngularPosition() {
        return -navx.getAngle();
    }

    public double getAngularVelocity() {
        return navx.getRate();
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
        driveLeftMaster.set(mode,left);
        driveRightMaster.set(mode,-right);
    }

    public void initDefaultCommand() {
        setDefaultCommand(new DriveBase_DriveManual());
    }

    @Override
    public void periodic() {
        RobotPositionIntegrator.update(Timer.getFPGATimestamp(),getLeftPosition()/12,getRightPosition()/12,getAngularPosition());
    }

}
