package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.*;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team4322.commandv2.Subsystem;

public class Elevator extends Subsystem {

    private WPI_TalonSRX liftMaster;
    private WPI_VictorSPX liftSlave;

    private WPI_TalonSRX wristMaster;

    private double carriageGoal;
    private WristFacing wristGoal;

    public Elevator() {
        // enable limit switch
        liftMaster = createMaster(
                Constants.LIFT_MASTER_TALON_ID,
                Constants.LIFT_P,
                Constants.LIFT_I,
                Constants.LIFT_D);
        liftMaster.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
        liftMaster.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen);
        liftSlave = new WPI_VictorSPX(Constants.LIFT_SLAVE_VICTOR_ID);
        wristMaster = createMaster(
                Constants.WRIST_TALON_ID,
                Constants.WRIST_P,
                Constants.WRIST_I,
                Constants.WRIST_D);
        liftSlave.follow(liftMaster);
    }

    private WPI_TalonSRX createMaster(int id, double p, double i, double d) {
        WPI_TalonSRX talon = new WPI_TalonSRX(id);
        talon.configFactoryDefault();
        talon.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 0);
        talon.config_kP(0, p);
        talon.config_kI(0, i);
        talon.config_kD(0, d);
        talon.setNeutralMode(NeutralMode.Brake);
        addChild(talon);
        return talon;
    }

    public void setPosition(double carriagePosition, WristFacing wristFacing) {
        carriageGoal = Util.clamp(carriagePosition, 0, 1);
        wristGoal = wristFacing;

        // TODO: integrate wrist with collision avoidance
        boolean withinSafeZone = Util.within(getLiftPosition(),
                Constants.LIFT_BEGIN_SAFE_ZONE,
                Constants.LIFT_END_SAFE_ZONE);

        double rawLiftGoal = liftGoalToEncoderCounts(carriageGoal);

        liftMaster.set(ControlMode.Position, rawLiftGoal);
    }

    public void setPosition(CarriagePosition position, WristFacing wristFacing) {
        setPosition(position.getLiftPosition(), wristFacing);
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


    public double getLiftPosition() {
        return encoderCountsToNormalizedLift(liftMaster.getSelectedSensorPosition());
    }

    private double encoderCountsToNormalizedLift(double encoderCounts) {
        return encoderCounts / Constants.LIFT_COUNTS_FOR_LENGTH;
    }

    // valid with winch?
    private double liftGoalToEncoderCounts(double liftGoal) {
        return liftGoal * Constants.LIFT_COUNTS_FOR_LENGTH;
    }

    private double wristGoalToEncoderCounts(double wristGoal) {
        return wristGoal * Constants.WRIST_COUNTS_FOR_FULL_ROTATION / 180.0;
    }

    public enum CarriagePosition {

        PanelLow(0.0),
        PanelMiddle(0.0),
        PanelHigh(0.0),
        CargoLow(PanelLow.getLiftPosition() + Constants.PANEL_CARGO_OFFSET),
        CargoMiddle(PanelMiddle.getLiftPosition() + Constants.PANEL_CARGO_OFFSET),
        CargoHigh(PanelHigh.getLiftPosition() + Constants.PANEL_CARGO_OFFSET),
        Home(0.0);

        private double liftPosition;

        CarriagePosition(double liftPosition) {
            this.liftPosition = liftPosition;
        }

        public double getLiftPosition() {
            return liftPosition;
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
