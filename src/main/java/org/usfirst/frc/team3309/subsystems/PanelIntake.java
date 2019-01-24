package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.Solenoid;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team4322.commandv2.Subsystem;


public class PanelIntake extends Subsystem {

    private WPI_VictorSPX intakeMotor;

    private Solenoid solenoid;

    public PanelIntake() {
        intakeMotor = new WPI_VictorSPX(Constants.PANEL_INTAKE_VICTOR_ID);

        solenoid = new Solenoid(Constants.PANEL_INTAKE_SOLENOID_ID);

        intakeMotor.configFactoryDefault();
    }

    public void setPower(double power) {
        intakeMotor.set(ControlMode.PercentOutput, power);
    }

    public void setSolenoid(boolean on) {
        solenoid.set(on);
    }

}
