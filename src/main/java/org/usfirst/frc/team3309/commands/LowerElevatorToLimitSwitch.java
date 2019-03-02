package org.usfirst.frc.team3309.commands;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class LowerElevatorToLimitSwitch extends Command {

    public LowerElevatorToLimitSwitch() {
        super(1.0);
        require(Robot.elevator);
    }

    @Override
    protected void execute() {
        Robot.elevator.setPower(-0.25);
    }

    @Override
    protected boolean isFinished() {
        return Robot.elevator.getLimitSwitchPressed();
    }

    @Override
    protected void end() {
        Robot.elevator.setPower(0.0);
    }

}
