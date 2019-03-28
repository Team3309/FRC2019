package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team4322.commandv2.Subsystem;

public class Elevator extends Subsystem {

    private WPI_TalonSRX liftMaster;
    private WPI_VictorSPX liftSlave;

    private DigitalInput limitSwitch;

    private double carriageGoal;

    public Elevator() {
        liftMaster = new WPI_TalonSRX(Constants.ELEVATOR_MASTER_TALON_ID);
        liftSlave = new WPI_VictorSPX(Constants.ELEVATOR_SLAVE_VICTOR_ID);
        configTalon(liftMaster);

        liftSlave.configFactoryDefault();
        liftSlave.follow(liftMaster);
        liftSlave.setNeutralMode(NeutralMode.Brake);
        liftSlave.setInverted(InvertType.FollowMaster);

        limitSwitch = new DigitalInput(2);
        addChild(limitSwitch);
    }

    private void configTalon(WPI_TalonSRX talon) {
        talon.configFactoryDefault();

        talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);

        talon.config_kP(0, Constants.ELEVATOR_P);
        talon.config_kI(0, Constants.ELEVATOR_I);
        talon.config_kD(0, Constants.ELEVATOR_D);

        talon.configMotionCruiseVelocity(300000);
        talon.configMotionAcceleration(180000);

        talon.configPeakOutputForward(1.0);
        talon.configPeakOutputReverse(-0.42);


        talon.setSensorPhase(false);
        talon.setNeutralMode(NeutralMode.Brake);
        // talon.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
        // talon.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
        addChild(talon);
    }

    public void zeroEncoder() {
        liftMaster.setSelectedSensorPosition(0);
    }

    @Override
    public void periodic() {

        if (getLimitSwitchPressed()) {
            zeroEncoder();
            DriverStation.reportError("Zeroed lift", false);
        }
    }

    private boolean moveComplete() {
        return Util.withinTolerance(getCarriagePercentage(), carriageGoal, 0.05);
    }

    public void changeSlot(Slot slot) {
        if (slot == Slot.Tad) {
            liftMaster.config_kP(0, Constants.ELEVATOR_P);
            liftMaster.config_kI(0, Constants.ELEVATOR_I);
            liftMaster.config_kD(0, Constants.ELEVATOR_D);
        } else if (slot == Slot.BigMovement) {
            liftMaster.config_kF(1, 0.1);
            liftMaster.config_kI(1, 5.96e-6);
            liftMaster.config_kP(1, 0.45);
        }
    }

    public enum Slot {
        Tad,
        BigMovement
    }

    public void setPosition(CarriagePosition position) {
        setPosition(position);
    }

    public void setPosition(double carriagePercentage) {
        double prevCarriageGoal = carriageGoal;
        carriageGoal = Util.clamp(carriagePercentage, 0, 1);

        double rawLiftGoal = liftGoalToEncoderCounts(carriageGoal);

        if (!DriverStation.getInstance().isDisabled()) {
//            double kF = 0.0;
//            if (Util.within(carriageGoal - prevCarriageGoal, 0.0 ,0.1)) {
//                kF = 0.28;
//            }
//            System.out.println("Kf" + kF);
//            liftMaster.config_kF(0, kF);
            liftMaster.set(ControlMode.MotionMagic, rawLiftGoal);
        } else {
            DriverStation.reportWarning("ELEVATOR: Tried to set value while disabled", false);
        }
    }

    public boolean getLimitSwitchPressed() {
        if (Constants.currentRobot == Constants.Robot.COMPETITION) {
            return !limitSwitch.get();
        } else if (Constants.currentRobot == Constants.Robot.PRACTICE) {
            return limitSwitch.get();
        }
        return false;
    }

    public double getCarriagePercentage() {
        return encoderCountsToNormalizedLift(liftMaster.getSelectedSensorPosition());
    }

    public double getEncoderVelocity() {
        return liftMaster.getSelectedSensorVelocity();
    }

    private double encoderCountsToNormalizedLift(double encoderCounts) {
        return encoderCounts / Constants.ELEVATOR_ENCODER_COUNTS_FOR_MAX_HEIGHT;
    }

    private double liftGoalToEncoderCounts(double liftGoal) {
        return liftGoal * Constants.ELEVATOR_ENCODER_COUNTS_FOR_MAX_HEIGHT;
    }

    public void setPower(double power) {
        liftMaster.set(ControlMode.PercentOutput, power);
    }

    public void outputToDashboard() {
        SmartDashboard.putBoolean("Carriage limit switch", getLimitSwitchPressed());
        SmartDashboard.putNumber("Carriage position goal", carriageGoal);
        SmartDashboard.putNumber("Carriage position goal raw", liftGoalToEncoderCounts(carriageGoal));
        SmartDashboard.putNumber("Carriage velocity raw", getEncoderVelocity());
        SmartDashboard.putNumber("Carriage position actual", getCarriagePercentage());
        SmartDashboard.putNumber("Carriage position raw", liftMaster.getSelectedSensorPosition());
        SmartDashboard.putNumber("Carriage position goal raw", liftGoalToEncoderCounts(carriageGoal));
        SmartDashboard.putNumber("Carriage closed loop error", liftMaster.getClosedLoopError());
        SmartDashboard.putNumber("Elevator power", liftMaster.getMotorOutputPercent());
        SmartDashboard.putNumber("Elevator voltage", liftMaster.getMotorOutputVoltage());
    }

    // Goal in percentage [0, 1]
    public enum CarriagePosition {

        Home(0.0),
        PanelFeederStation(0.0),
        PanelLow(0.0),
        PanelMiddle(0.40),
        PanelHigh(0.76),
        PanelClearingPanelIntake(0.1),
        CargoLow(0.26),
        CargoMiddle(0.64),
        CargoHigh(0.95),
        CargoShipCargo(0.45),
        Test(0.0);

        private double carriagePercentage;

        CarriagePosition(double carriagePercentage) {
            this.carriagePercentage = carriagePercentage;
        }

        public double getCarriagePercentage() {
            return carriagePercentage;
        }

    }

}
