package org.usfirst.frc.team3309.commands.climber;

import edu.wpi.first.wpilibj.GenericHID;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.subsystems.Climber;
import org.usfirst.frc.team4322.commandv2.Command;

public class ClimberManual extends Command {

    public ClimberManual() {
        require(Robot.climber);
    }

    @Override
    protected void execute() {
        if (OI.getOperatorController().getLeftStick().get()) {
            Robot.climber.setPosition(Climber.ClimberLatchPosition.Released);
        } else if (OI.getOperatorController().getX().get()) {
            Robot.climber.setPosition(Climber.ClimberLatchPosition.Latched);
        }
        Robot.climber.setPower(OI.getOperatorController().getY(GenericHID.Hand.kLeft));
    }

    @Override
    protected boolean isFinished() {
        return false;
    }


}
