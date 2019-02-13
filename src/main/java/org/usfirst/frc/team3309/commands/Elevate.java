package org.usfirst.frc.team3309.commands;

import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team3309.subsystems.Elevator;
import org.usfirst.frc.team4322.commandv2.Command;

public class Elevate extends Command {

    private double prevTime;
    private Level level;

    private Elevator.CarriagePosition carriagePosition;
    private Elevator.WristFacing wristFacing;

    public Elevate(Level level) {
        require(Robot.elevator);
        this.level = level;
    }

    public Elevate(Elevator.CarriagePosition carriagePosition, Elevator.WristFacing wristFacing) {
        require(Robot.elevator);
        this.carriagePosition = carriagePosition;
        this.wristFacing = wristFacing;
    }

    public Elevate(Elevator.CarriagePosition carriagePosition) {
        this(carriagePosition, null);
    }

    @Override
    protected void initialize() {
        prevTime = Timer.getFPGATimestamp();
        if (level != null) {
            switch (level) {
                case Home:
                    carriagePosition = Elevator.CarriagePosition.Home;
                    break;
                case CargoOnShip:
                    carriagePosition = Elevator.CarriagePosition.CargoOnShip;
                    break;
                case FeederStation:
                    carriagePosition = Elevator.CarriagePosition.FeederStation;
                    break;
                case Low:
                    if (Robot.hasCargo()) {
                        carriagePosition = Elevator.CarriagePosition.CargoLow;
                    } else if (Robot.hasPanel()) {
                        carriagePosition = Elevator.CarriagePosition.PanelLow;
                    }
                    break;
                case Middle:
                    if (Robot.hasCargo()) {
                        carriagePosition = Elevator.CarriagePosition.CargoMiddle;
                    } else if (Robot.hasPanel()) {
                        carriagePosition = Elevator.CarriagePosition.PanelMiddle;
                    }
                    break;
                case High:
                    if (Robot.hasCargo()) {
                        carriagePosition = Elevator.CarriagePosition.CargoHigh;
                    } else if (Robot.hasPanel()) {
                        carriagePosition = Elevator.CarriagePosition.PanelHigh;
                    }
                    break;
            }
        }
    }

    @Override
    protected void execute() {
        double curTime = Timer.getFPGATimestamp();
        double deltaTime = curTime - prevTime;

        if (carriagePosition != null) {
            double offset = Constants.LIFT_NUDGE_SPEED * deltaTime *
                    OI.INSTANCE.getOperatorController().getLeftStick().y();
            double goalPosition = Util.clamp(carriagePosition.getLiftPosition() + offset,
                    0.0, 0.8);
            Robot.elevator.setPosition(goalPosition, wristFacing);
        }
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

    public enum Level {
        Low,
        Middle,
        High,
        CargoOnShip,
        FeederStation,
        Home,
    }

}
