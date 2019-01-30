package org.usfirst.frc.team3309.commands.cargoholder;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class CargoHolderStopManual extends Command {

    public CargoHolderStopManual() {
        require(Robot.cargoHolder);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

}
