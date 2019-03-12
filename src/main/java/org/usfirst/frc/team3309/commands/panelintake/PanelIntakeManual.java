package org.usfirst.frc.team3309.commands.panelintake;

import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team4322.commandv2.Command;

public class PanelIntakeManual extends Command {

    public PanelIntakeManual() {
        require(Robot.panelIntake);
    }

    @Override
    protected void execute() {
        double powerOut = OI.getOperatorController().getLt().axis();
        double powerIn = OI.getOperatorController().getRt().axis();

        double power = Util.signedMax(powerOut, powerIn, Constants.PANEL_INTAKE_MIN_POWER);

        Robot.panelIntake.setPower(power);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
