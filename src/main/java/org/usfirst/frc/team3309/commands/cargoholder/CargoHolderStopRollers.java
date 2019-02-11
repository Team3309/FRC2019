package org.usfirst.frc.team3309.commands.cargoholder;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class CargoHolderStopRollers extends Command {

    public CargoHolderStopRollers() {
        require(Robot.cargoHolder);
    }

    @Override
    protected void execute() {
        Robot.cargoHolder.setPower(0.0);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

}
