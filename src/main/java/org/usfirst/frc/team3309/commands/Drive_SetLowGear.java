package org.usfirst.frc.team3309.commands;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class Drive_SetLowGear extends Command {

    public Drive_SetLowGear() {
    }

    @Override
    protected void initialize() {
        Robot.driveBase.setLowGear();
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
