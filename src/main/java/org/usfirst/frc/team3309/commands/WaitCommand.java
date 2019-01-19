package org.usfirst.frc.team3309.commands;

import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team4322.commandv2.Command;

public class WaitCommand extends Command {

    private double mTimeToWait;
    private double mStartTime;

    public WaitCommand(double timeToWait) {
        mTimeToWait = timeToWait;
    }

    @Override
    public void initialize() {
        mStartTime = Timer.getFPGATimestamp();
    }

    @Override
    public boolean isFinished() {
        return Timer.getFPGATimestamp() - mStartTime >= mTimeToWait;
    }

    @Override
    protected void end() {

    }
}
