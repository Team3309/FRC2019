package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Solenoid;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team4322.commandv2.Subsystem;


public class PanelIntake extends Subsystem {

    private WPI_VictorSPX intakeMotor;

    private Solenoid solenoid;

    private AnalogInput sharpSensor;

    public PanelIntake() {
        sharpSensor = new AnalogInput(Constants.PANEL_INTAKE_SENSOR_PORT);
        intakeMotor = new WPI_VictorSPX(Constants.PANEL_INTAKE_VICTOR_ID);
        solenoid = new Solenoid(Constants.PANEL_INTAKE_HOLDER_ID);

        intakeMotor.configFactoryDefault();
    }

    public void setPosition(PanelIntakePosition position) {
        if (position == PanelIntakePosition.Up) {
            setSolenoid(true);
        } else if (position == PanelIntakePosition.Down) {
            setSolenoid(false);
        }
    }

    public PanelIntakePosition getPosition() {
        if (solenoid.get()) {
            return PanelIntakePosition.Up;
        } else {
            return PanelIntakePosition.Down;
        }
    }

    private void setSolenoid(boolean on) {
        solenoid.set(on);
    }

    public void setPower(double power) {
        intakeMotor.set(ControlMode.PercentOutput, power);
    }

    public boolean hasPanel() {
        return sharpSensor.getAverageVoltage() > 0.7;
    }

    public enum PanelIntakePosition {
        Up,
        Down
    }

}
