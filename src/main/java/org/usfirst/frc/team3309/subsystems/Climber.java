package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team4322.commandv2.Subsystem;

public class Climber extends Subsystem {

    private WPI_TalonSRX winchMotor;
    private Solenoid latchingSolenoid;

    public Climber() {
        winchMotor = new WPI_TalonSRX(Constants.CLIMBER_TALON_ID);
        latchingSolenoid = new Solenoid(Constants.CLIMBER_LATCHING_SOLENOID_ID);

        winchMotor.configFactoryDefault();
        winchMotor.config_kP(0, 0.0);
        winchMotor.config_kI(0, 0.0);
        winchMotor.config_kD(0, 0.0);
        winchMotor.setNeutralMode(NeutralMode.Brake);
    }

    public void outputToDashboard() {
        SmartDashboard.putNumber("Climber position", winchMotor.getSelectedSensorPosition());
        SmartDashboard.putNumber("Climber goal", winchMotor.getClosedLoopTarget());
        SmartDashboard.putNumber("Climber closed loop error", winchMotor.getClosedLoopError());
        SmartDashboard.putString("Climber latch position", getLatchPosition().toString());
        SmartDashboard.putBoolean("Climber raw latch postion", getLatchPosition().value);
    }

    public void setAngle(ClimberAngle angle) {
        winchMotor.set(ControlMode.Position, angle.get());
    }

    public void setPosition(ClimberLatchPosition position) {
        latchingSolenoid.set(position.get());
    }

    public ClimberLatchPosition getLatchPosition() {
        if (latchingSolenoid.get() == ClimberLatchPosition.Latched.value) {
            return ClimberLatchPosition.Latched;
        } else {
            return ClimberLatchPosition.Released;
        }
    }

    public enum ClimberAngle {

        Extended(0.0);

        private double angle;

        ClimberAngle(double angle) {
            this.angle = angle;
        }

        public double get() {
            return angle;
        }

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
