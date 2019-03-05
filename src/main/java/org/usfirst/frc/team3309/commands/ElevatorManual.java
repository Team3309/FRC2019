package org.usfirst.frc.team3309.commands;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team4322.commandv2.Command;

public class ElevatorManual extends Command {

    public ElevatorManual() {
        require(Robot.elevator);
    }

    @Override
    protected void execute() {
        double elevatorPower = Util.clamp(OI.INSTANCE.getOperatorController().getY(GenericHID.Hand.kLeft),
                -0.2, 1.0);
        Robot.elevator.setPower(elevatorPower);
//        SmartDashboard.putNumber("Left operator joystick power", elevatorPower);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

}
