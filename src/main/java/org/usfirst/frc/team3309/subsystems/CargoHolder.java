package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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
        addChild(motor);
    }

    public void outputToDashboard() {
        SmartDashboard.putBoolean("CH bumper sensor", hasCargo());
        SmartDashboard.putNumber("CH motor power", motor.getMotorOutputPercent());
        SmartDashboard.putNumber("CH motor voltage", motor.getMotorOutputVoltage());
    }

    public void setPower(double power) {
        motor.set(ControlMode.PercentOutput, power);
    }

    public boolean hasCargo() {
        return !bumperSensor.get();
    }

}
