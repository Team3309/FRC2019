package org.usfirst.frc.team3309.commands.panelholder;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class PanelHolderSetRollers extends Command {

    private double power;

    public PanelHolderSetRollers(double power, double timeout) {
        super(timeout);
        setInterruptBehavior(InterruptBehavior.Terminate);
        require(Robot.panelHolder);
        this.power = power;
    }

    public PanelHolderSetRollers(double power) {
        this(power, Double.POSITIVE_INFINITY);
    }

    @Override
    protected void execute() {
        Robot.panelHolder.setPower(power);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
