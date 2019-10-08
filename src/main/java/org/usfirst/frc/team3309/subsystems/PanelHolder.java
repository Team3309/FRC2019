package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.commands.panelholder.PanelHolderActuate;
import org.usfirst.frc.team3309.commands.panelholder.PanelHolderManual;
import org.usfirst.frc.team4322.commandv2.Subsystem;

import java.sql.Driver;

public class PanelHolder extends Subsystem {

    private WPI_VictorSPX victor;
    private Solenoid extendingSolenoid;

    private DigitalInput bumperSensor;

    private boolean panelPulledIn = false;
    private boolean currentLimitReached = false;
    private Timer holdTimer = new Timer();
    private double power;

    public PanelHolder() {
        victor = new WPI_VictorSPX(Constants.PANEL_HOLDER_VICTOR_ID);
        victor.configOpenloopRamp(0.3);
        victor.setNeutralMode(NeutralMode.Brake);
        extendingSolenoid = new Solenoid(Constants.PANEL_HOLDER_TELESCOPING_SOLENOID_ID);
        bumperSensor = new DigitalInput(Constants.PANEL_HOLDER_BUMPER_SENSOR_PORT);
        addChild(victor);
        addChild(extendingSolenoid);
        addChild(bumperSensor);
    }

    @Override
    public void initDefaultCommand() {
        setDefaultCommand(new PanelHolderManual());
    }

    public void setPower(double newPower) {

        // check if previously or now over current limit
        currentLimitReached = currentLimitReached ||
                Robot.panelHolder.getCurrent() > Constants.PANEL_HOLDER_MAX_CURRENT;

        if (newPower < 0) {
            // intaking
            if (holdTimer.get() > 0.25) {
                // after panel is pulled in, allow intake power to return to holding power
                holdTimer.stop();
                holdTimer.reset();
            }
            if (holdTimer.get() > 0) {
                // use reduced power while pulling panel in to avoid overloading motor
                newPower = Constants.PANEL_HOLDER_REDUCED_INTAKE_POWER;
            } else if (hasPanel()) {
                if (!panelPulledIn) {
                    // pull in panel, but back-off intake power to not overload motor
                    holdTimer.reset();
                    holdTimer.start();
                    panelPulledIn = true;
                    newPower = Constants.PANEL_HOLDER_REDUCED_INTAKE_POWER;
                } else {
                    // use holding power after panel has been pulled in
                    newPower = (Constants.PANEL_HOLDER_HOLDING_POWER);
                    currentLimitReached = false;
                }
            } else {
                // we don't have a panel yet
                panelPulledIn = false;
            }
        }
        else if (newPower > 0 && currentLimitReached) {
            // back off eject power if panel gets jammed while ejecting
            newPower = Constants.PANEL_HOLDER_REDUCED_EJECT_POWER;
        }
        power = newPower;
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

        // use current detection only since bumper switch is not reliable
        if (power >= 0) {
            // ejecting
            return false;
        }
        // if we are forcefully intaking, don't erroneously think we have a panel due to the higher than normal current
        if (power == Constants.PANEL_HOLDER_INTAKE_POWER) {
            return getCurrent() >= Constants.PANEL_HOLDER_MAX_CURRENT;
        }
        // if are pulling in the panel, assume we have it until the pull-in timer expires
        if (power == Constants.PANEL_HOLDER_REDUCED_INTAKE_POWER) {
            return true;
        }
        // we are at holding power
        return getCurrent() >= Constants.PANEL_HOLDER_PANEL_DETECT_CURRENT;
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
