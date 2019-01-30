package org.usfirst.frc.team3309.commands.panelintake;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class PanelIntakeSetRollers extends Command {

    private double power;
    private boolean out;

    public PanelIntakeSetRollers(double power, boolean out) {
        require(Robot.panelIntake);
        this.power = power;
        this.out = out;
    }

    @Override
    protected void execute() {
        if (out) {
            power = -1 * Math.abs(power);
        }
        Robot.panelIntake.setPower(power);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
