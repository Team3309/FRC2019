package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Subsystem;

public class PanelHolder extends Subsystem {

    private WPI_VictorSPX victor;
    private Solenoid extendingSolenoid;

    private DigitalInput bumperSensor;

    public PanelHolder() {
        victor = new WPI_VictorSPX(Constants.PANEL_HOLDER_VICTOR_ID);
        victor.configOpenloopRamp(0.15);
        victor.setNeutralMode(NeutralMode.Brake);
        extendingSolenoid = new Solenoid(Constants.PANEL_HOLDER_TELESCOPING_SOLENOID_ID);
        bumperSensor = new DigitalInput(Constants.PANEL_HOLDER_BUMPER_SENSOR_PORT);
        addChild(victor);
        addChild(extendingSolenoid);
        addChild(bumperSensor);
    }

    public void setPower(double power) {
  /*      if (getCurrent() > Constants.PANEL_HOLDER_MAX_CURRENT) {
            power = -0.28;
        }*/
        victor.set(ControlMode.PercentOutput, power);
    }

    /*
     * @param position, the desired configuration
     * */
    public void setPosition(PanelHolderPosition position) {
        switch (position) {
            case TelescopeBack:
                setExtendingSolenoid(ExtendedPosition.RetractedInwards);
                break;
            case TelescopeForwards:
                setExtendingSolenoid(ExtendedPosition.ExtendedOutwards);
                break;
        }
    }

    public void setPosition(ExtendedPosition extendedPosition) {
        setExtendingSolenoid(extendedPosition);
    }

    public ExtendedPosition getExtendedPosition() {
        boolean isExtended = extendingSolenoid.get();
        if (isExtended == ExtendedPosition.ExtendedOutwards.get()) {
            return ExtendedPosition.ExtendedOutwards;
        } else {
            return ExtendedPosition.RetractedInwards;
        }
    }

    public double getCurrent() {
        return Robot.pdp.getCurrent(7);
    }

    public void outputToDashboard() {
        SmartDashboard.putNumber("PH power", victor.getMotorOutputPercent());
        SmartDashboard.putString("PH ExtendedPosition", getExtendedPosition().toString());
        SmartDashboard.putNumber("PH Current", getCurrent());
        SmartDashboard.putBoolean("PH Extended raw", getExtendedPosition().value);
        SmartDashboard.putBoolean("PH bumper pressed", hasPanel());
    }

    public void setExtendingSolenoid(ExtendedPosition position) {
        extendingSolenoid.set(position.get());
    }

    public boolean hasPanel() {
        return !bumperSensor.get();
    }

    public enum PanelHolderPosition {
        TelescopeBack,
        TelescopeForwards
    }

    public enum ExtendedPosition {
        ExtendedOutwards(true),
        RetractedInwards(false);

        private boolean value;

        ExtendedPosition(boolean value) {
            this.value = value;
        }

        public boolean get() {
            return value;
        }

        public static ExtendedPosition fromBoolean(boolean value) {
            return value ? ExtendedOutwards : RetractedInwards;
        }

    }

}
