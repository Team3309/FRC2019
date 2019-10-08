package org.usfirst.frc.team3309.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team3309.subsystems.CargoIntake;
import org.usfirst.frc.team3309.subsystems.Elevator;
import org.usfirst.frc.team4322.commandv2.Command;

public class Elevate extends Command {

    private Level level;

    private Elevator.CarriagePosition carriageGoalPosition;

    public Elevate(Level level) {
        super(Constants.ELEVATOR_TIMEOUT);
        require(Robot.elevator);
        this.level = level;
    }

    public Elevate(Elevator.CarriagePosition carriageGoalPosition) {
        super(Constants.ELEVATOR_TIMEOUT);
        require(Robot.elevator);
        this.carriageGoalPosition = carriageGoalPosition;
    }

    @Override
    protected void initialize() {
        if (level != null) {
            boolean hasCargo = Robot.cargoHolder.hasCargo();
//            boolean hasPanel = Robot.panelIntake.hasPanel();

            switch (level) {
                case Home:
                    if (Robot.hasCargoInIntakeZone()
                            && Robot.cargoIntake.getPosition() == CargoIntake.CargoIntakePosition.Stowed) {
                        carriageGoalPosition = Elevator.CarriagePosition.Home;
                    } else if (!Robot.hasCargoInIntakeZone()
                            && Robot.cargoHolder.hasCargo()
                            && Robot.cargoIntake.getPosition() == CargoIntake.CargoIntakePosition.Stowed) {
                        carriageGoalPosition = Elevator.CarriagePosition.CargoLow;
                    } else {
                        carriageGoalPosition = Elevator.CarriagePosition.Home;
                    }
                    break;
                case CargoShipCargo:
                    carriageGoalPosition = Elevator.CarriagePosition.CargoShipCargo;
                    break;
                case Low:
                    if (hasCargo) {
                        carriageGoalPosition = Elevator.CarriagePosition.CargoLow;
                    } else {
                        carriageGoalPosition = Elevator.CarriagePosition.PanelLow;
                    }
                    break;
                case Middle:
                    if (hasCargo) {
                        carriageGoalPosition = Elevator.CarriagePosition.CargoMiddle;
                    } else {
                        carriageGoalPosition = Elevator.CarriagePosition.PanelMiddle;
                    }
                    break;
                case High:
                    if (hasCargo) {
                        carriageGoalPosition = Elevator.CarriagePosition.CargoHigh;
                    } else {
                        carriageGoalPosition = Elevator.CarriagePosition.PanelHigh;
                    }
                    break;
                case Test:
                    carriageGoalPosition = Elevator.CarriagePosition.Test;
                    break;
                case PanelClearingPanelIntake:
                    carriageGoalPosition = Elevator.CarriagePosition.PanelClearingPanelIntake;
                    break;
            }
        }
    }

    @Override
    protected void execute() {
        Robot.elevator.setPosition(carriageGoalPosition.getCarriagePercentage());
    }

    @Override
    protected boolean isFinished() {

        // Absolute carriage positioning error is the same for all raised positions.
        // There is no need to use a percentage tolerance.
        // Positioning error is less when returning to the bottom.

        double tolerance = 0.04;  // for raised positions, reduce if elevator is better tuned

        if (carriageGoalPosition.getCarriagePercentage() == 0.0) {
            // Get closer to the bottom before cutting power.
            // Could also use the limit switch, but there is some question as to its reliability.
            tolerance = 0.015;
        }
        boolean withinTolerance = Math.abs(Robot.elevator.getCarriagePercentage() -
                carriageGoalPosition.getCarriagePercentage()) <= tolerance;
        Robot.elevator.setWithinTolerance(withinTolerance);
        return (withinTolerance);
    }

    @Override
    protected void end() {
        if (carriageGoalPosition.getCarriagePercentage() == 0.0) {
            // Don't overheat the motors holding the elevator 1mm above the home position while we
            // wait for the PID controller i value to finally hit zero.
            Robot.elevator.setPower(0.0);
        }
    }

    public enum Level {
        Low,
        Middle,
        High,
        PanelClearingPanelIntake,
        CargoShipCargo,
        Home,
        Test,
    }

}
