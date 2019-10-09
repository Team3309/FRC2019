package org.usfirst.frc.team3309.commands.panelholder;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team4322.commandv2.Command;

import java.sql.Driver;

public class PanelHolderManual extends Command {

    public PanelHolderManual() {
        require(Robot.panelHolder);
        setInterruptBehavior(InterruptBehavior.Suspend);
    }

    @Override
    protected void execute() {
        double powerOut = OI.getOperatorController().getLt().axis();
        double powerIn = OI.getOperatorController().getRt().axis();

        double manualPower = Util.weirdSignedMax(powerOut, powerIn, 0.1);

        if (!Robot.isGuestDriver()) {
            Robot.panelHolder.setPower(manualPower);
        }
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
