package org.usfirst.frc.team3309.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team3309.subsystems.Drive;
import org.usfirst.frc.team4322.commandv2.Command;

/*
 *   Experimental: do NOT yet run on robot
 *
 *   Autotunes drive PID constants using twiddle
 *
 *   TODO: refactor to be subsystem independent,
 *      clean drive side implementation
 *
 *   @see
 *       https://martin-thoma.com/twiddle/
 *       https://www.youtube.com/watch?v=2uQ2BSzDvXs
 * */
public class Drive_SelfTune extends Command {

    private double tolerance;
    private double feedforward;
    private double goalVelocity;

    private Drive.DriveSide side;

    private int n = 1000;

    private double bestError = Double.POSITIVE_INFINITY;

    private double[] constants = new double[3];
    private double[] deltaConstants = {1.0, 1.0, 1.0};

    /*
     *   @params
     *      tolerance, stops adjusting constants when
     *       change drops below this
     *      feedforward, supplement output to drive to
     *       assist PID
     *
     * */
    public Drive_SelfTune(double tolerance, Drive.DriveSide side, double goalVelocity, double feedforward) {
        this.tolerance = tolerance;
        this.side = side;
        this.goalVelocity = goalVelocity;
        this.feedforward = feedforward;
    }

    public Drive_SelfTune(double tolerance, Drive.DriveSide side, double goalVelocity) {
        this(tolerance, side, goalVelocity, 0.0);
    }

    @Override
    protected void execute() {
        for (int i = 0; i < constants.length; i++) {
            constants[i] += deltaConstants[i];
            double error = runConstants();
            if (error < bestError) {
                bestError = error;
                deltaConstants[i] *= 1.1;
            } else {
                constants[i] -= 2 * constants[i];
                error = runConstants();
                if (error < bestError) {
                    bestError = error;
                    deltaConstants[i] *= 1.1;
                } else {
                    constants[i] += deltaConstants[i];
                    deltaConstants[i] *= 0.9;
                }
            }
        }
    }

    private double runConstants() {
        Robot.drive.setPIDConstants(side, constants);
        double totalError = 0.0;
        for (int i = 0; i < 2 * n; i++) {
            Robot.drive.setSide(side, ControlMode.Velocity, goalVelocity, DemandType.ArbitraryFeedForward, feedforward);
            if (i > n) {
                totalError += Math.pow(Robot.drive.getClosedLoopError(side), 2);
            }
        }
        return totalError / (double) n;
    }


    @Override
    protected boolean isFinished() {
        return tolerance > Util.sum(deltaConstants);
    }

}
