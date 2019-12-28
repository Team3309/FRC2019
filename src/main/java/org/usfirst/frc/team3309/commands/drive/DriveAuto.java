package org.usfirst.frc.team3309.commands.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.SpeedControllerGroup;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;


public class DriveAuto extends Command {

    private double left;
    private double right;
    private ControlMode mode;

    public DriveAuto(ControlMode mode, double left, double right) {
        require(Robot.drive);
        setInterruptBehavior(InterruptBehavior.Terminate);
        this.left = left;
        this.right = right;
        this.mode = mode;
    }

    @Override
    protected void initialize() {
        Robot.drive.setHighGear();
        if (mode == ControlMode.Velocity) {
            Robot.drive.autoVelocity(left, right);
        } else if (mode == ControlMode.PercentOutput) {
            Robot.drive.setLeftRight(mode, left, right);
        }
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
