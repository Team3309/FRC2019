package org.usfirst.frc.team3309.commands.panelintake;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class PanelIntakeActuate extends Command {

    private boolean down;

    public PanelIntakeActuate(boolean down) {
        this.down = down;
    }

    @Override
    protected void initialize() {
        require(Robot.panelIntake);
        Robot.panelIntake.setSolenoid(down);
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
