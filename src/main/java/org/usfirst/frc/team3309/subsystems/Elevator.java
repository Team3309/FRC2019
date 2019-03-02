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

    // private WPI_TalonSRX wristMaster;

    private double carriageGoal;
    private WristFacing wristGoal;

    private boolean stowIntakeAfterMove;

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
        talon.configPeakOutputReverse(-0.7);


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
    public void initDefaultCommand() {
//        setDefaultCommand(new ElevateNudge());
    }

    @Override
    public void periodic() {

        if (getLimitSwitchPressed()) {
            zeroEncoder();
        }

        /*if (stowIntakeAfterMove
                && moveComplete()
                && !Robot.hasCargoInIntakeZone()) {
            new CargoIntakeActuate(CargoIntake.CargoIntakePosition.Stowed).start();
            stowIntakeAfterMove = false;
        }*/
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

    public void setPosition(CarriagePosition position, WristFacing wristFacing) {
        setPosition(position.getCarriagePercentage(), wristFacing);
    }

    public void setPosition(WristFacing wristFacing) {
        setPosition(carriageGoal, wristFacing);
    }

    public void setPosition(CarriagePosition position) {
        setPosition(position, wristGoal);
    }

    public void setPosition(double position) {
        setPosition(position, wristGoal);
    }

    public void setPosition(double carriagePercentage, WristFacing wristFacing) {
        carriageGoal = Util.clamp(carriagePercentage, 0, 1);
        wristGoal = wristFacing;

        // TODO: integrate wrist through collision avoidance
//        boolean withinSafeZone = Util.within(getCarriagePercentage(),
//                Constants.LIFT_BEGIN_SAFE_ZONE,
//                Constants.LIFT_END_SAFE_ZONE);

        /*if (Robot.cargoHolder.hasCargo()
                && Robot.cargoIntake.getPosition() == CargoIntake.CargoIntakePosition.Stowed
                && moveCrossesIntake()) {
            new CargoIntakeActuate(CargoIntake.CargoIntakePosition.Extended).start();
            // TODO: if elevator is faster than intake can extend, put wait here
            stowIntakeAfterMove = true;
        }*/


        double rawLiftGoal = liftGoalToEncoderCounts(carriageGoal);

        if (!DriverStation.getInstance().isDisabled()) {
            liftMaster.set(ControlMode.MotionMagic, rawLiftGoal);
        } else {
            DriverStation.reportWarning("ELEVATOR: Tried to set value while disabled", false);
        }
    }

    // if intake zone is 0 in length, this can return false when it crosses
    private boolean moveCrossesIntake() {
        assert Constants.CARGO_INTAKE_ZONE_MAX > Constants.CARGO_INTAKE_ZONE_MIN;
        return Util.overlap1D(Constants.CARGO_INTAKE_ZONE_MIN, Constants.CARGO_INTAKE_ZONE_MAX,
                getCarriagePercentage(), carriageGoal) > 0;
    }

    public boolean getLimitSwitchPressed() {
        return !limitSwitch.get();
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

    private double wristGoalToEncoderCounts(double wristGoal) {
        return wristGoal * Constants.WRIST_COUNTS_FOR_FULL_ROTATION / 180.0;
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

    // TODO: fill out goal percentages
    // Goal in percentage [0, 1]
    public enum CarriagePosition {

        Home(0.0),
        PanelFeederStation(0.0),
        PanelLow(0.05),
        PanelMiddle(0.40),
        PanelHigh(0.76),
        CargoLow(0.26),
        CargoMiddle(0.64),
        CargoHigh(0.95),
        //        CargoLow(PanelLow.getCarriagePercentage() + Constants.PANEL_CARGO_OFFSET),
//        CargoMiddle(PanelMiddle.getCarriagePercentage() + Constants.PANEL_CARGO_OFFSET),
//        CargoHigh(PanelHigh.getCarriagePercentage() + Constants.PANEL_CARGO_OFFSET),
        CargoShipCargo(0.45),
        PanelClearingPanelIntake(0.0),
        Test(0.0),
        DropATad(0.0);

        private double carriagePercentage;

        CarriagePosition(double carriagePercentage) {
            this.carriagePercentage = carriagePercentage;
        }

        public double getCarriagePercentage() {
            return carriagePercentage;
        }

    }

    public enum WristFacing {

        Front(0.0),
        Back(0.0);

        private double wristPosition;

        WristFacing(double wristPosition) {
            this.wristPosition = wristPosition;
        }

        public double getWristPosition() {
            return wristPosition;
        }
    }

}
