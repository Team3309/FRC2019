package org.usfirst.frc.team3309.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class Drive_Brake extends Command {

    @Override
    protected void execute() {
        Robot.drive.setLeftRight(ControlMode.PercentOutput, 0.0, 0.0);
        Robot.drive.setNeutralMode(NeutralMode.Brake);
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
