package org.usfirst.frc.team3309.commands.panelholder;

import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team4322.commandv2.Command;

public class PanelHolderManual extends Command {

    private boolean hadPanel;
    private boolean timerStarted;
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


        if (!(Math.abs(manualPower) > 0)) {
            if (Robot.panelHolder.hasPanel()) {
                if (!hadPanel) {
                    hadPanel = true;
                    holdTimer.reset();
                    holdTimer.start();
                    power = -0.6;
                } else if (hadPanel && holdTimer.get() > 0.25) {
                    power = -0.28;
                    holdTimer.stop();
                }
            } else {
                power = 0.0;
            }
        } else {
            if (OI.getOperatorCargoIntakeButton().get()) {
                power = 0.0;
            } else {
                power = manualPower;
            }
            hadPanel = false;
        }

        Robot.panelHolder.setPower(power);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
