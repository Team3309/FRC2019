package org.usfirst.frc.team3309.commands.panelholder;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.subsystems.PanelHolder;
import org.usfirst.frc.team4322.commandv2.Command;
import edu.wpi.first.wpilibj.DriverStation;

public class PanelHolderActuate extends Command {

    private PanelHolder.PanelHolderPosition position;

    public PanelHolderActuate(PanelHolder.PanelHolderPosition position) {
        this.position = position;
    }

    @Override
    protected void execute() {
        if (!Robot.isGuestDriver()) {
            Robot.panelHolder.setPosition(position);
        }
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
