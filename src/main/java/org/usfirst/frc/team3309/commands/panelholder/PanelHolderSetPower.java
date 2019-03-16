package org.usfirst.frc.team3309.commands.panelholder;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class PanelHolderSetPower extends Command {

    private double power;

    public PanelHolderSetPower(double power) {
        require(Robot.panelHolder);
        this.power = power;
    }

    @Override
    protected void initialize() {
        Robot.panelHolder.setPower(power);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
