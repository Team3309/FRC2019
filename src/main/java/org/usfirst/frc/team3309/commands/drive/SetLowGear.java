package org.usfirst.frc.team3309.commands.drive;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class SetLowGear extends Command {

    public SetLowGear() {
    }

    @Override
    protected void initialize() {
        Robot.drive.setLowGear();
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
