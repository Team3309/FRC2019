package org.usfirst.frc.team3309.commands.panelholder;

import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team4322.commandv2.Command;

public class PanelHolderManual extends Command {

    public PanelHolderManual() {
        require(Robot.panelHolder);
    }

    @Override
    protected void execute() {
        double powerOut = OI.getOperatorController().getLt().axis();
        double powerIn = OI.getOperatorController().getRt().axis();

        double power = Util.signedMax(powerOut, powerIn, 0.1);

        Robot.panelHolder.setPower(power);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
