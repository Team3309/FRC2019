package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team4322.commandv2.Subsystem;

public class CargoIntake extends Subsystem {

    private WPI_VictorSPX intakeMotor;

    private DoubleSolenoid solenoid;

    public CargoIntake() {
        intakeMotor = new WPI_VictorSPX(Constants.CARGO_INTAKE_VICTOR_ID);
        solenoid = new DoubleSolenoid(Constants.CARGO_INTAKE_SOLENOID_A, Constants.CARGO_INTAKE_SOLENOID_B);

        intakeMotor.configFactoryDefault();
    }

    public void setPower(double power) {
        intakeMotor.set(ControlMode.PercentOutput, power);
    }

    public void setSolenoid(DoubleSolenoid.Value value) {
        solenoid.set(value);
    }

    public DoubleSolenoid.Value getSolenoidPosition() {
        return solenoid.get();
    }

}
