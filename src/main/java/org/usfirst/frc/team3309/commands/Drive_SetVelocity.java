package org.usfirst.frc.team3309.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class Drive_SetVelocity extends Command {

    private final double velocity;

    public Drive_SetVelocity(double velocity) {
        require(Robot.drive);
        this.velocity = velocity;
    }

    @Override
    protected void execute() {
        Robot.drive.setLeftRight(ControlMode.Velocity, velocity, velocity);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

}
