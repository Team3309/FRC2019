package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.DigitalInput;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team4322.commandv2.Subsystem;

public class CargoHolder extends Subsystem {

    private WPI_VictorSPX motor;

    private DigitalInput bumperSensor;

    public CargoHolder() {
        bumperSensor = new DigitalInput(Constants.CARGO_HOLDER_BUMPER_PORT);
        motor = new WPI_VictorSPX(Constants.CARGO_HOLDER_VICTOR_ID);

        motor.configFactoryDefault();

        addChild(bumperSensor);
        addChild(  motor);
    }

    public void setPower(double power) {
        motor.set(ControlMode.PercentOutput, power);
    }

    public boolean isBumperPressed() {
        return !bumperSensor.get();
    }

}