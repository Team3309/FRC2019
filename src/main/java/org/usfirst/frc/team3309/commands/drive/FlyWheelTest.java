package org.usfirst.frc.team3309.commands.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class FlyWheelTest extends Command {
    private Command command;

    public FlyWheelTest () {
        require(Robot.drive);
    }

    @Override
    protected void execute () {
        Robot.drive.setLeftRight(ControlMode.PercentOutput, -1.0, -);
    }

    @Override
    protected boolean isFinished () {
        return !OI.getRightJoystickLeftClusterGroup().get();
    }
}