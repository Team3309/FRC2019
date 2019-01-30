package org.usfirst.frc.team3309.commands;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.subsystems.PanelHolder;
import org.usfirst.frc.team4322.commandv2.Command;

public class PanelHolderActuate extends Command {

    private PanelHolder.PanelHolderPosition position;

    public PanelHolderActuate(PanelHolder.PanelHolderPosition position) {
        require(Robot.panelHolder);
        this.position = position;
    }

    @Override
    protected void execute() {
        Robot.panelHolder.setPosition(position);
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
