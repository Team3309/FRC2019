package org.usfirst.frc.team3309.commands.cargolauncher;

import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class CargoLauncherManual extends Command {

    public CargoLauncherManual() {
        require(Robot.cargoLauncher);
    }

    @Override
    protected void execute() {
        double powerOut = OI.INSTANCE.getOperatorController().lt();
        double powerIn = OI.INSTANCE.getOperatorController().rt();

        double power = 0.0;

        if (powerOut > Constants.CARGO_LAUNCHER_ROLLERS_MIN_POWER) {
            power = powerOut;
        } else if (powerIn > Constants.CARGO_LAUNCHER_ROLLERS_MIN_POWER) {
            power = -powerIn;
        }

        Robot.cargoLauncher.setPower(power);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

}
