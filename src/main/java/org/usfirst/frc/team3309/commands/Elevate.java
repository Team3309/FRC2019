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
        double goalPosition = carriageGoalPosition.getCarriagePercentage();
        Robot.elevator.setPosition(goalPosition);
        SmartDashboard.putBoolean("Within tolerance", isFinished());
    }

    @Override
    protected boolean isFinished() {
        return Util.withinTolerance(Robot.elevator.getCarriagePercentage(),
                carriageGoalPosition.getCarriagePercentage(), 0.06);
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
