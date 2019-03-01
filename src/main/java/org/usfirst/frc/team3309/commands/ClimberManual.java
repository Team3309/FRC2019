package org.usfirst.frc.team3309.commands;

import edu.wpi.first.wpilibj.GenericHID;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class ClimberManual extends Command {

    public ClimberManual() {
        require(Robot.climber);
    }

    @Override
    protected void execute() {
        Robot.climber.setPower(OI.INSTANCE.getOperatorController().getY(GenericHID.Hand.kLeft));
    }

    @Override
    protected boolean isFinished() {
        return false;
    }


}
