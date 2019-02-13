package org.usfirst.frc.team3309.commands;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.subsystems.Climber;
import org.usfirst.frc.team4322.commandv2.Command;

public class WinchClimber extends Command {

    public Climber.ClimberAngle angle;

    public WinchClimber(Climber.ClimberAngle angle) {
        require(Robot.climber);
        this.angle = angle;
    }

    @Override
    protected void execute() {
        Robot.climber.setAngle(angle);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

}
