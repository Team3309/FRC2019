package org.usfirst.frc.team3309.commands.cargointake;

import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class CargoIntakeManual extends Command {

    public CargoIntakeManual() {
        require(Robot.cargoIntake);
    }

    @Override
    protected void execute() {
        double powerOut = OI.INSTANCE.getOperatorController().lt();
        double powerIn = OI.INSTANCE.getOperatorController().rt();

        double power = 0.0;

        if (powerOut > Constants.CARGO_INTAKE_ROLLERS_MIN_POWER) {
            power = powerOut;
        } else if (powerIn > Constants.CARGO_INTAKE_ROLLERS_MIN_POWER) {
            power = -powerIn;
        }

        Robot.cargoIntake.setPower(power);

    }

    @Override
    protected boolean isFinished() {
        return false;
    }

}
