package org.usfirst.frc.team3309.commands;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class TogglePipeline extends Command {

    @Override
    protected void execute() {
        Robot.vision.setPipeline(1 - Robot.vision.getPipeline());
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}

