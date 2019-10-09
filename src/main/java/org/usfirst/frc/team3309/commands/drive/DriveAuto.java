package org.usfirst.frc.team3309.commands.drive;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class DriveAuto extends Command {

    private double turnCounts;
    private int mode; // 0 = position, 1 = velocity

    public DriveAuto(int mode, double turnCounts) {
        require(Robot.drive);
        setInterruptBehavior(InterruptBehavior.Terminate);
        this.turnCounts = turnCounts;
        this.mode = mode;
    }

    @Override
    protected void initialize() {
        Robot.drive.setHighGear();
        if (mode == 0) {
            Robot.drive.turnPosition(turnCounts);
        } else {
            Robot.drive.turnVelocity(turnCounts);
        }
    }

    @Override
    protected boolean isFinished() {
        return Robot.drive.turnComplete();
    }
}
