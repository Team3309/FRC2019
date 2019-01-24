package org.usfirst.frc.team3309.commands.cargointake;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;

public class CargoIntakeActuate extends Command {

    private DoubleSolenoid.Value value;

    public CargoIntakeActuate(DoubleSolenoid.Value value) {
        require(Robot.cargoIntake);
        this.value = value;
    }

    @Override
    protected void execute() {
        Robot.cargoIntake.setSolenoid(value);
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
