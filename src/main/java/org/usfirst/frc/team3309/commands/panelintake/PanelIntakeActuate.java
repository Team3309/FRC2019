package org.usfirst.frc.team3309.commands.panelintake;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.subsystems.PanelIntake;
import org.usfirst.frc.team4322.commandv2.Command;

public class PanelIntakeActuate extends Command {

    private PanelIntake.PanelIntakePosition position;

    public PanelIntakeActuate(PanelIntake.PanelIntakePosition position) {
        this.position = position;
    }

    @Override
    protected void execute() {
        Robot.panelIntake.setPosition(position);
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
