package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team4322.commandv2.Subsystem;
import org.usfirst.frc.team3309.commands.climber.ClimberManual;

public class Climber extends Subsystem {

    private WPI_TalonSRX winchMotor;
    private Solenoid latchingSolenoid;

    public Climber() {
        winchMotor = new WPI_TalonSRX(Constants.CLIMBER_TALON_ID);
        latchingSolenoid = new Solenoid(Constants.CLIMBER_LATCHING_SOLENOID_ID);

        winchMotor.configFactoryDefault();
        winchMotor.setNeutralMode(NeutralMode.Brake);
    }

    public void outputToDashboard() {
        SmartDashboard.putNumber("Climber current", winchMotor.getOutputCurrent());
        SmartDashboard.putNumber("Climber position", winchMotor.getSelectedSensorPosition());
        SmartDashboard.putBoolean("Climber raw latch postion", latchingSolenoid.get());
    }

    public void setPosition(ClimberLatchPosition position) {
        latchingSolenoid.set(position.get());
    }

    public void setPower(double power) {
        winchMotor.set(ControlMode.PercentOutput, power);
    }

    public enum ClimberLatchPosition {

        Latched(false),
        Released(true);

        private boolean value;

        ClimberLatchPosition(boolean value) {
            this.value = value;
        }

        public boolean get() {
            return value;
        }

        public static ClimberLatchPosition fromBoolean(boolean value) {
            return value ? Released : Latched;
        }

    }

}
