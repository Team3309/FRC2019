package org.usfirst.frc.team3309.commands;

import org.usfirst.frc.team4322.commandv2.Command;

public class WaitCommand extends Command {

    public WaitCommand(double timeToWait) {
        super(timeToWait);
    }

    @Override
    public boolean isFinished() {
        return false;
    }

}
