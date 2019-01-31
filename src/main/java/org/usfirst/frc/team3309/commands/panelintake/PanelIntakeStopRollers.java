package org.usfirst.frc.team3309.commands.panelintake;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class PanelIntakeStopRollers extends Command {

    public PanelIntakeStopRollers() {
        require(Robot.panelIntake);
    }

    @Override
    public void execute() {
        Robot.panelIntake.setPower(0.0);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
