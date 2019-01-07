package org.usfirst.frc.team3309.commands;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class Drive_SetHighGear extends Command {

    public Drive_SetHighGear() {
    }

    @Override
    protected void initialize() {
        Robot.driveBase.setHighGear();
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
