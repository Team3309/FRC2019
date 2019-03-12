package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team4322.commandv2.Subsystem;


public class CargoIntake extends Subsystem {

    private WPI_VictorSPX intakeMotor;
    private Solenoid solenoid;
    private boolean solenoidValue;

    public CargoIntake() {
        intakeMotor = new WPI_VictorSPX(Constants.CARGO_INTAKE_VICTOR_ID);
        solenoid = new Solenoid(Constants.CARGO_INTAKE_SOLENOID_ID);

        intakeMotor.configFactoryDefault();
        intakeMotor.setInverted(true);

        addChild(intakeMotor);
        addChild(solenoid);
        solenoidValue = solenoid.get();
    }

    public void setPower(double power) {
        intakeMotor.set(ControlMode.PercentOutput, power);
    }

    public void setPosition(CargoIntakePosition position) {
//        if (position == CargoIntakePosition.Stowed
//            && Robot.hasCargoInIntakeZone()) {
//            DriverStation.reportWarning("Cannot stow CargoIntake with " +
//                    "elevator down and holding cargo", true);
//            return;
//        }
        if(solenoidValue != position.get())
            solenoid.set(solenoidValue = position.get());
    }

    public CargoIntakePosition getPosition() {
       if (solenoidValue == CargoIntakePosition.Stowed.get()) {
           return CargoIntakePosition.Stowed;
       } else {
           return CargoIntakePosition.Extended;
       }
    }

    public void outputToDashboard() {
        SmartDashboard.putString("CI position", getPosition().toString());
        SmartDashboard.putBoolean("CI raw position", getPosition().value);
        SmartDashboard.putNumber("CI power", intakeMotor.getMotorOutputPercent());
        SmartDashboard.putNumber("CI voltage", intakeMotor.getMotorOutputVoltage());
    }

    public enum CargoIntakePosition {
        Stowed(false),
        Extended(true);

        private boolean value;

        CargoIntakePosition(boolean value) {
            this.value = value;
        }

        public boolean get() {
            return value;
        }

        public static CargoIntakePosition fromBoolean(boolean value) {
            return value ? CargoIntakePosition.Extended : CargoIntakePosition.Stowed;
        }
    }

}
