package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.Util3309;
import org.usfirst.frc.team4322.commandv2.Subsystem;


public class PanelIntake extends Subsystem {

    private WPI_VictorSPX intakeMotor;

    private Solenoid solenoid;

    private AnalogInput sharpSensor;
    private DigitalInput bannerSensor;

    public PanelIntake() {
        sharpSensor = new AnalogInput(Constants.PANEL_INTAKE_SHARP_SENSOR_PORT);
        intakeMotor = new WPI_VictorSPX(Constants.PANEL_INTAKE_VICTOR_ID);
        solenoid = new Solenoid(Constants.PANEL_INTAKE_SOLENOID_ID);
        bannerSensor = new DigitalInput(Constants.PANEL_INTAKE_BANNER_SENSOR_PORT);

        intakeMotor.configFactoryDefault();
        intakeMotor.setInverted(true);
    }

    public void setPosition(PanelIntakePosition position) {
        setSolenoid(position.get());
    }

    public PanelIntakePosition getPosition() {
        if (solenoid.get() == PanelIntakePosition.Up.get()) {
            return PanelIntakePosition.Up;
        } else {
            return PanelIntakePosition.Down;
        }
    }

    public void outputToDashboard() {
        SmartDashboard.putString("PI position", getPosition().toString());
        SmartDashboard.putBoolean("PI position raw", getPosition().value);
        SmartDashboard.putBoolean("PI has panel", hasPanel());
        SmartDashboard.putNumber("Dustpan Sharp Value", sharpSensor.getAverageValue());
        SmartDashboard.putNumber("Dustpan Sharp Voltage", sharpSensor.getAverageVoltage());
        SmartDashboard.putNumber("PI current", getCurrent());
    }

    private void setSolenoid(boolean on) {
        solenoid.set(on);
    }

    public void setPower(double power) {
        intakeMotor.set(ControlMode.PercentOutput, power);
    }

    public boolean hasPanel() {
//        double threshold = 0.0;
//        if (Constants.currentRobot == Constants.Robot.PRACTICE) {
//            threshold = 2.65;
//        } else if (Constants.currentRobot == Constants.Robot.COMPETITION) {
//            threshold = 2.55;
//        }
//        return Util.within(sharpSensor.getAverageVoltage(), threshold - 0.5, threshold + 0.5);
        return !bannerSensor.get();
    }

    public double getCurrent() {
        return Robot.pdp.getCurrent(11);
    }

    public enum PanelIntakePosition {
        Up(false),
        Down(true);

        private boolean value;

        PanelIntakePosition(boolean value) {
            this.value = value;
        }

        public boolean get() {
            return value;
        }

        public static PanelIntakePosition fromBoolean(boolean value) {
            return value ? Up : Down;
        }

    }

}
