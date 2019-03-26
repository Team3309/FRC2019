package org.usfirst.frc.team3309.commands.panelholder;

import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team4322.commandv2.Command;

public class PanelHolderManual extends Command {

    private boolean hadPanel;
    private boolean timerStarted;
    private boolean currentLimitReached;
    private Timer holdTimer = new Timer();

    private double power;

    public PanelHolderManual() {
        require(Robot.panelHolder);
        setInterruptBehavior(InterruptBehavior.Suspend);
    }

    @Override
    protected void execute() {
        double powerOut = OI.getOperatorController().getLt().axis();
        double powerIn = OI.getOperatorController().getRt().axis();

        double manualPower = Util.signedMax(powerOut, powerIn, 0.1);

        if (!(Math.abs(manualPower) > 0) || currentLimitReached) {
            currentLimitReached = false;
            if (Robot.panelHolder.hasPanel()) {
                if (!hadPanel && !timerStarted) {
                    hadPanel = true;
                    timerStarted = true;
                    holdTimer.reset();
                    holdTimer.start();
                    power = -0.6;
                } else if (holdTimer.get() > 0.25) {
                    timerStarted = false;
                    power = -0.28;
                    holdTimer.stop();
                }
            } else {
                hadPanel = false;
                power = 0.0;
            }
        } else {
            if (Robot.panelHolder.getCurrent() > Constants.PANEL_HOLDER_MAX_CURRENT) {
                currentLimitReached = true;
            } else {
                power = manualPower;
            }
            hadPanel = false;
        }

        Robot.panelHolder.setPower(power);
    }

    @Override
    protected void interrupted() {
        hadPanel = false;
//        timerStarted = false;
        holdTimer.reset();
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
