package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.commands.cargoholder.CargoHolderManual;
import org.usfirst.frc.team4322.commandv2.Subsystem;

public class CargoHolder extends Subsystem {

    private WPI_VictorSPX motor;

    private DigitalInput bumperSensor;

    public CargoHolder() {
        bumperSensor = new DigitalInput(Constants.CARGO_HOLDER_BUMPER_PORT);
        motor = new WPI_VictorSPX(Constants.CARGO_HOLDER_VICTOR_ID);

        motor.configFactoryDefault();
        motor.setNeutralMode(NeutralMode.Brake);
    }

    @Override
    public void initDefaultCommand() {
        setDefaultCommand(new CargoHolderManual());
    }

    public void outputToDashboard() {
        SmartDashboard.putBoolean("CH bumper sensor", hasCargo());
        SmartDashboard.putNumber("CH motor power", motor.getMotorOutputPercent());
        SmartDashboard.putNumber("CH motor voltage", motor.getMotorOutputVoltage());
        SmartDashboard.putNumber("CH motor current", Robot.pdp.getCurrent(Constants.kPdpCargoHolder));
    }

    public void setPower(double power) {
        motor.set(ControlMode.PercentOutput, power);
    }

    public boolean hasCargo() {
        return !bumperSensor.get();
    }

}
