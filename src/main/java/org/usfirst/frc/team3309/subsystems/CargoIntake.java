package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.Solenoid;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team4322.commandv2.Subsystem;


public class CargoIntake extends Subsystem {

    private WPI_VictorSPX intakeMotor;
    private Solenoid solenoid;

    public CargoIntake() {
        intakeMotor = new WPI_VictorSPX(Constants.CARGO_INTAKE_VICTOR_ID);
        solenoid = new Solenoid(Constants.CARGO_INTAKE_SOLENOID_ID);

        intakeMotor.configFactoryDefault();

        addChild("CargoIntakeMotor", intakeMotor);
        addChild("CargoIntakeSolenoid", solenoid);
    }

    public void setPower(double power) {
        intakeMotor.set(ControlMode.PercentOutput, power);
    }

    public void setPosition(CargoIntakePosition position) {
        if (position == CargoIntakePosition.Extended) {
            solenoid.set(true);
        } else {
            solenoid.set(false);
        }
    }

    public CargoIntakePosition getPosition() {
        if (solenoid.get()) {
            return CargoIntakePosition.Extended;
        } else {
            return CargoIntakePosition.Stowed;
        }
    }

    // TODO; wrap values, then check in getPosition
    public enum CargoIntakePosition {
        Stowed,
        Extended
    }

}
