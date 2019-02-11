package org.usfirst.frc.team3309.commands.cargointake;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class CargoIntakeStopRollers extends Command {

    public CargoIntakeStopRollers() {
        require(Robot.cargoIntake);
    }

    @Override
    protected void execute() {
        Robot.cargoIntake.setPower(0.0);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
