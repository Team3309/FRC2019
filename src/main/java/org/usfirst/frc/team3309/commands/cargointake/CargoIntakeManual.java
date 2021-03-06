package org.usfirst.frc.team3309.commands.cargointake;

import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.Util3309;
import org.usfirst.frc.team4322.commandv2.Command;

public class CargoIntakeManual extends Command {

    public CargoIntakeManual() {
        require(Robot.cargoIntake);
    }

    @Override
    protected void execute() {
        double powerOut = OI.getOperatorController().getLt().axis();
        double powerIn = OI.getOperatorController().getRt().axis();

        double power = Util3309.weirdSignedMax(powerOut, powerIn, Constants.CARGO_INTAKE_ROLLERS_MIN_POWER);
        // scale control input for maximum sensitivity
        power = power * Constants.CARGO_INTAKE_ROLLERS_MAX_POWER;

       if (!OI.getOperatorCargoIntakeButton().get()) {
            power = 0.0;
        }

        Robot.cargoIntake.setPower(power);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

}
