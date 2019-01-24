package org.usfirst.frc.team3309.commands.panelintake;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class PanelIntakeActuate extends Command {

    private boolean on;

    public PanelIntakeActuate(boolean on) {
        this.on = on;
    }

    @Override
    protected void initialize() {
        require(Robot.panelIntake);
        Robot.panelIntake.setSolenoid(on);
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
