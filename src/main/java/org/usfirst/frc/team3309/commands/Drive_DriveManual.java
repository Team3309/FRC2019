package org.usfirst.frc.team3309.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class Drive_DriveManual extends Command {

    public Drive_DriveManual() {
        require(Robot.drive);
        setInterruptBehavior(InterruptBehavior.Suspend);
    }

    private static final double highGearWheelNonLinearity = 0.65;
    private static final double lowGearWheelNonLinearity = 0.5;

    private static final double highGearNegativeInertiaScalar = 4.0;

    private static final double highGearSensitivity = 1;
    private static final double lowGearSensitivity = 1.3;

    private static final double lowGearNegativeInertiaThreshold = 0.65;
    private static final double lowGearNegInertiaTurnScalar = 1.5;
    private static final double lowGearNegativeInertiaCloseScalar = 4.0;
    private static final double lowGearNegativeInertiaFarScalar = 5.0;

    private static final double quickStopDeadband = 0.2;
    private static final double quickStopWeight = 0.1;
    private static final double quickStopScalar = 5.0;

    private double oldTurn = 0.0;
    private double quickStopAccumlator = 0.0;
    private double negativeInertiaAccumlator = 0.0;

    @Override
    protected void initialize() {

    }

    private static double limit(double v, double limit) {
        return (Math.abs(v) < limit) ? v : Math.copySign(limit, v);
    }

    @Override
    protected void execute() {
        // Converted to Xbox controller for convenience with testing

        double turn = Robot.oi.getDriveController().getRightStick().x();
        double throttle = Robot.oi.getDriveController().getLeftStick().y();
//        double turn = Robot.oi.getRightJoystick().getXAxis().get();
//        double throttle = Robot.oi.getLeftJoystick().getYAxis().get();
        boolean isHighGear = Robot.drive.inHighGear();
//        boolean isQuickTurn = Robot.oi.getRightJoystick().getTrigger().get();
        boolean isQuickTurn = Robot.oi.getDriveController().getRb().get();
        double negInertia = turn - oldTurn;
        oldTurn = turn;

        double wheelNonLinearity = isHighGear ? highGearWheelNonLinearity : lowGearWheelNonLinearity;
        double denominator = Math.sin(Math.PI / 2.0 * wheelNonLinearity);

        // Apply a sin function that's scaled to make it feel better.
        turn = Math.sin(Math.PI / 2.0 * wheelNonLinearity * turn) / denominator;

        double sensitivity;

        double angularPower;
        double linearPower;

        // Negative inertia!
        double negInertiaScalar;
        if (isHighGear) {
            negInertiaScalar = highGearNegativeInertiaScalar;
            sensitivity = highGearSensitivity;
        } else {
            if (turn * negInertia > 0) {
                // If we are moving away from 0.0, aka, trying to get more wheel.
                negInertiaScalar = lowGearNegInertiaTurnScalar;
            } else {
                // Otherwise, we are attempting to go back to 0.0.
                if (Math.abs(turn) > lowGearNegativeInertiaThreshold) {
                    negInertiaScalar = lowGearNegativeInertiaFarScalar;
                } else {
                    negInertiaScalar = lowGearNegativeInertiaCloseScalar;
                }
            }
            sensitivity = lowGearSensitivity;
        }
        double negInertiaPower = negInertia * negInertiaScalar;
        negativeInertiaAccumlator += negInertiaPower;

        turn = turn + negativeInertiaAccumlator;
        if (negativeInertiaAccumlator > 1) {
            negativeInertiaAccumlator -= 1;
        } else if (negativeInertiaAccumlator < -1) {
            negativeInertiaAccumlator += 1;
        } else {
            negativeInertiaAccumlator = 0;
        }
        linearPower = throttle;

        // Quickturn!
        if (isQuickTurn) {
            if (Math.abs(linearPower) < quickStopDeadband) {
                double alpha = quickStopWeight;
                quickStopAccumlator = (1 - alpha) * quickStopAccumlator
                        + alpha * limit(turn, 1.0) * quickStopScalar;
            }
            angularPower = turn;
        } else {
            angularPower = Math.abs(throttle) * turn * sensitivity - quickStopAccumlator;
            if (quickStopAccumlator > 1) {
                quickStopAccumlator -= 1;
            } else if (quickStopAccumlator < -1) {
                quickStopAccumlator += 1;
            } else {
                quickStopAccumlator = 0.0;
            }
        }
        Robot.drive.setLeftRight(ControlMode.PercentOutput, linearPower + angularPower, linearPower - angularPower);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
