package org.usfirst.frc.team3309.commands.panelintake;

import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class PanelIntakeManual extends Command {

    public PanelIntakeManual() {
        require(Robot.panelIntake);
    }

    @Override
    protected void execute() {
        double powerOut = OI.INSTANCE.getOperatorController().lt();
        double powerIn = OI.INSTANCE.getOperatorController().rt();

        double power = 0.0;

        if (powerOut > Constants.PANEL_INTAKE_MIN_POWER) {
            power = powerOut;
        } else if (powerIn > Constants.PANEL_INTAKE_MIN_POWER) {
            power = -powerIn;
        }

        Robot.panelIntake.setPower(power);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
