package org.usfirst.frc.team3309.commands.drive;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class DriveSetHighGear extends Command {

    public DriveSetHighGear() {
    }

    @Override
    protected void initialize() {
        Robot.drive.setHighGear();
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
