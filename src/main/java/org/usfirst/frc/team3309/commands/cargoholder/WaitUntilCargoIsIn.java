package org.usfirst.frc.team3309.commands.cargoholder;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class WaitUntilCargoIsIn extends Command {

    @Override
    protected boolean isFinished() {
        return Robot.cargoHolder.hasCargo();
    }

}
