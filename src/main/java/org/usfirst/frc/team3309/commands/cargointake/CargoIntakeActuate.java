package org.usfirst.frc.team3309.commands.cargointake;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.subsystems.CargoIntake;
import org.usfirst.frc.team4322.commandv2.Command;

public class CargoIntakeActuate extends Command {

    private CargoIntake.CargoIntakePosition position;

    public CargoIntakeActuate(CargoIntake.CargoIntakePosition position) {
        this.position = position;
    }

    @Override
    protected void execute() {
        if (!Robot.isGuestDriver()) {
            Robot.cargoIntake.setPosition(position);
        }
    }

    @Override
    protected boolean isFinished() {
        return true;
    }

}
