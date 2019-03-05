package org.usfirst.frc.team3309.commands.cargointake;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class CargoIntakeSetRollers extends Command {

    private double power;

    public CargoIntakeSetRollers(double power) {
        require(Robot.cargoIntake);
        this.power = power;
    }

    @Override
    protected void execute() {
        Robot.cargoIntake.setPower(power);
    }

    @Override
    protected boolean isFinished() {
        return true;
    }

}
