package org.usfirst.frc.team3309.commands.panelintake;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class PanelIntakeSetRollers extends Command {

    private double power;

    public PanelIntakeSetRollers(double power) {
        require(Robot.panelIntake);
        this.power = power;
    }

    @Override
    protected void initialize() {
        Robot.panelIntake.setPower(power);
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
