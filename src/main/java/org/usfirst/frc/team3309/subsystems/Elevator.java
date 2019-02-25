package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.commands.cargointake.CargoIntakeActuate;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team4322.commandv2.Subsystem;

public class Elevator extends Subsystem {

    private WPI_TalonSRX liftMaster;
    private WPI_VictorSPX liftSlave;

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

    }

    private void configTalon(WPI_TalonSRX talon) {
        talon.configFactoryDefault();

        talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);

        talon.config_kP(0, Constants.ELEVATOR_P);
        talon.config_kI(0, Constants.ELEVATOR_I);
        talon.config_kD(0, Constants.ELEVATOR_D);

        talon.configPeakOutputForward(0.7);
        talon.configPeakOutputReverse(-0.2);

        if (Constants.Robot.PRACTICE == Constants.currentRobot) {
            talon.setSensorPhase(true);
        } else {
            talon.setSensorPhase(false);
        }

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
        if (stowIntakeAfterMove
                && moveComplete()
                && !Robot.hasCargoInIntakeZone()) {
            new CargoIntakeActuate(CargoIntake.CargoIntakePosition.Stowed).start();
            stowIntakeAfterMove = false;
        }
    }

    private boolean moveComplete() {
        return Util.withinTolerance(getCarriagePercentage(), carriageGoal, 0.05);
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

        if (Robot.cargoHolder.hasCargo()
                && Robot.cargoIntake.getPosition() == CargoIntake.CargoIntakePosition.Stowed
                && moveCrossesIntake()) {
            new CargoIntakeActuate(CargoIntake.CargoIntakePosition.Extended).start();
            // TODO: if elevator is faster than intake can extend, put wait here
            stowIntakeAfterMove = true;
        }

        double rawLiftGoal = liftGoalToEncoderCounts(carriageGoal);

        liftMaster.set(ControlMode.Position, rawLiftGoal);
    }

    // if intake zone is 0 in length, this can return false when it crosses
    private boolean moveCrossesIntake() {
        assert Constants.CARGO_INTAKE_ZONE_MAX > Constants.CARGO_INTAKE_ZONE_MIN;
        return Util.overlap1D(Constants.CARGO_INTAKE_ZONE_MIN, Constants.CARGO_INTAKE_ZONE_MAX,
                getCarriagePercentage(), carriageGoal) > 0;
    }

    public double getCarriagePercentage() {
        return encoderCountsToNormalizedLift(liftMaster.getSelectedSensorPosition());
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
        SmartDashboard.putNumber("Carriage position goal", carriageGoal);
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

        PanelLow(0.0),
        PanelMiddle(0.0),
        PanelHigh(0.0),
        CargoLow(PanelLow.getCarriagePercentage() + Constants.PANEL_CARGO_OFFSET),
        CargoMiddle(PanelMiddle.getCarriagePercentage() + Constants.PANEL_CARGO_OFFSET),
        CargoHigh(PanelHigh.getCarriagePercentage() + Constants.PANEL_CARGO_OFFSET),
        CargoShipCargo(0.0),
        PanelClearingPanelIntake(0.0),
        Home(0.0);

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
