package org.usfirst.frc.team3309.commands.cargoholder;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class CargoHolderSetPower extends Command {

    private double power;

    public CargoHolderSetPower(double power) {
        require(Robot.cargoHolder);
        this.power = power;
    }

    @Override
    protected void initialize() {
        Robot.cargoHolder.setPower(power);
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
