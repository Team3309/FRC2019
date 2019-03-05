package org.usfirst.frc.team3309.commands;

import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team4322.commandv2.Command;

public class ElevateNudge extends Command {

    private double prevTime;

    public ElevateNudge() {
        require(Robot.elevator);
        setInterruptBehavior(InterruptBehavior.Suspend);
    }

    @Override
    protected void initialize() {
        prevTime = Timer.getFPGATimestamp();
    }

    @Override
    protected void execute() {
        double curTime = Timer.getFPGATimestamp();
        double deltaTime = curTime - prevTime;

        double offset = -Constants.LIFT_NUDGE_PCT_PER_SEC * deltaTime *
                OI.INSTANCE.getOperatorController().getLeftStick().y();
        double goalPercentage = Util.clamp(Robot.elevator.getCarriagePercentage() + offset,
                0.0, 0.8);
        Robot.elevator.setPosition(goalPercentage, null);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
