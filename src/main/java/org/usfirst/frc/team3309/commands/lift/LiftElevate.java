package org.usfirst.frc.team3309.commands.lift;

import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team4322.commandv2.Command;

public class LiftElevate extends Command {

    private double goalHeight;
    private double error;

    public LiftElevate(double goalHeight) {
        require(Robot.lift);
        this.goalHeight = goalHeight;
    }

    @Override
    protected void initialize() {
        Robot.lift.setGoalPosition(goalHeight);
    }

    @Override
    protected void execute() {
        error = Robot.lift.getClosedLoopError();
    }

    @Override
    protected boolean isFinished() {
        return Util.withinTolerance(error, goalHeight, Constants.LIFT_TOLERANCE);
    }
}
