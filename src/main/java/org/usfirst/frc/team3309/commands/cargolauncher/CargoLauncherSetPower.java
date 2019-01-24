package org.usfirst.frc.team3309.commands.cargolauncher;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class CargoLauncherSetPower extends Command {

    private double power;

    public CargoLauncherSetPower(double power) {
        require(Robot.cargoLauncher);
        this.power = power;
    }

    @Override
    protected void initialize() {
        Robot.cargoLauncher.setPower(power);
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
