package org.usfirst.frc.team3309.commands;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.subsystems.Climber;
import org.usfirst.frc.team4322.commandv2.Command;

public class ReleaseLatch extends Command {

    private Climber.ClimberLatchPosition latchPosition;

    public ReleaseLatch(Climber.ClimberLatchPosition latchPosition) {
        require(Robot.climber);
        this.latchPosition = latchPosition;
    }

    @Override
    protected void execute() {
        Robot.climber.setPosition(Climber.ClimberLatchPosition.Released);
    }

    @Override
    protected boolean isFinished() {
        return true;
    }

}
