package org.usfirst.frc.team3309.commands.arm;

import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team4322.commandv2.Command;

public class ArmMoveTo extends Command {

    private double goalPosition;
    private double error;

    public ArmMoveTo(double goalPosition) {
        this.goalPosition = goalPosition;
    }

    @Override
    protected void initialize() {
        Robot.lift.setGoalPosition(goalPosition);
    }

    @Override
    protected void execute() {
        error = Robot.lift.getClosedLoopError();
    }

    @Override
    protected boolean isFinished() {
        return Util.withinTolerance(error, goalPosition, Constants.ARM_TOLERANCE);
    }

}
