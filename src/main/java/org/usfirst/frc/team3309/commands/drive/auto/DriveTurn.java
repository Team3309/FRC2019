package org.usfirst.frc.team3309.commands.drive.auto;

import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.PIDController;
import org.usfirst.frc.team3309.lib.util.LibTimer;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team4322.commandv2.Command;

public class DriveTurn extends Command {

    private double goalAngle;
    private PIDController angleController;
    private final double ANGLE_LENIENCY = 4;
    private LibTimer timer = new LibTimer(0.1);
    private double timeoutSec = Double.POSITIVE_INFINITY;
    private boolean isPigeon = false;
    private double start = Double.POSITIVE_INFINITY;

    double kP = 0.0788; //   0.0245
    double kI = 0.000378; //  0.000002
    double kD = 0.072; // 0.0184

    public DriveTurn(double goalAngle) {
        this.goalAngle = goalAngle;
        require(Robot.drive);
    }

    public DriveTurn(double goalAngle, double timeoutSec) {
        this(goalAngle);
        this.timeoutSec = timeoutSec;
        angleController = new PIDController(kP, kI, kD);
    }

    @Override
    public void initialize() {
        super.initialize();
        Robot.drive.reset();
        Robot.drive.setHighGear();
        timer.start();
        start = Timer.getFPGATimestamp();
    }

    @Override
    protected void execute() {
        double power = 30000 * angleController.update(Robot.drive.getGyroAngle(), goalAngle);
        Robot.drive.setVelocity(power, -power);
        System.out.println(power);

    }

    @Override
    protected boolean isFinished() {
        boolean inRange = timer.isConditionMaintained(
                Util.within(Robot.drive.getGyroAngle(),
                        goalAngle - ANGLE_LENIENCY, goalAngle + ANGLE_LENIENCY));
        boolean isTimeout = (Timer.getFPGATimestamp() - start) > timeoutSec;
        return inRange || isTimeout;
    }

    @Override
    public void end() {
        super.end();
        timer.reset();
        start = Double.POSITIVE_INFINITY;
        timeoutSec = Double.POSITIVE_INFINITY;
    }

}

