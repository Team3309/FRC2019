package org.usfirst.frc.team3309.commands;

import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team3309.subsystems.Elevator;
import org.usfirst.frc.team4322.commandv2.Command;

public class Elevate extends Command {

    private Level level;

    private Elevator.CarriagePosition carriageGoalPosition;
    private Elevator.WristFacing wristFacing;

    public Elevate(Level level) {
        require(Robot.elevator);
        this.level = level;
    }

    public Elevate(Elevator.CarriagePosition carriageGoalPosition, Elevator.WristFacing wristFacing) {
        require(Robot.elevator);
        this.carriageGoalPosition = carriageGoalPosition;
        this.wristFacing = wristFacing;
    }

    public Elevate(Elevator.CarriagePosition carriageGoalPosition) {
        this(carriageGoalPosition, null);
    }

    @Override
    protected void initialize() {
        if (level != null) {
            boolean hasCargo = Robot.cargoHolder.hasCargo();
            boolean hasPanel = Robot.panelIntake.hasPanel();

            switch (level) {
                case Home:
                    carriageGoalPosition = Elevator.CarriagePosition.Home;
                    break;
                case CargoShipCargo:
                    carriageGoalPosition = Elevator.CarriagePosition.CargoShipCargo;
                    break;
                case Low:
                    if (hasCargo) {
                        carriageGoalPosition = Elevator.CarriagePosition.CargoLow;
                    } else if (hasPanel) {
                        carriageGoalPosition = Elevator.CarriagePosition.PanelLow;
                    }
                    break;
                case Middle:
                    if (hasCargo) {
                        carriageGoalPosition = Elevator.CarriagePosition.CargoMiddle;
                    } else if (hasPanel) {
                        carriageGoalPosition = Elevator.CarriagePosition.PanelMiddle;
                    }
                    break;
                case High:
                    if (hasCargo) {
                        carriageGoalPosition = Elevator.CarriagePosition.CargoHigh;
                    } else if (hasPanel) {
                        carriageGoalPosition = Elevator.CarriagePosition.PanelHigh;
                    }
                    break;
            }
        }
    }

    @Override
    protected void execute() {
        Robot.elevator.setPosition(carriageGoalPosition.getCarriagePercentage(), wristFacing);
    }

    @Override
    protected boolean isFinished() {
        return Util.withinTolerance(Robot.elevator.getCarriagePercentage(),
                carriageGoalPosition.getCarriagePercentage(), 0.05);
    }

    public enum Level {
        Low,
        Middle,
        High,
        CargoShipCargo,
        Home,
    }

}
