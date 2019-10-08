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
    private Timer rampDownTimer = new Timer();
    private boolean rampingDown = false;
    private double power;
    private int logSeq = 0;


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

        // check if under manual control with power of holding power or less
        if (newPower <= 0 && newPower >= Constants.PANEL_HOLDER_HOLDING_POWER) {
            currentLimitReached = false;
            // default to slow intake speed to hold panel or accept one if we grab it
            newPower = Constants.PANEL_HOLDER_HOLDING_POWER;
        } else {
            // check if previously or now over current limit
            currentLimitReached = currentLimitReached ||
                    Robot.panelHolder.getCurrent() > Constants.PANEL_HOLDER_MAX_CURRENT;
        }

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
                    holdTimer.stop();
                    holdTimer.reset();
                    holdTimer.start();
                    panelPulledIn = true;
                    newPower = Constants.PANEL_HOLDER_REDUCED_INTAKE_POWER;
                    DriverStation.reportError(++logSeq + ": Start pulling in panel, newPower: " +
                            newPower + ", current power: " + power, false);
                } else {
                    // use holding power after panel has been pulled in
                    newPower = Constants.PANEL_HOLDER_HOLDING_POWER;
                    currentLimitReached = false;
                }
            } else {
                // we don't have a panel yet
                panelPulledIn = false;
            }
        } else {
            // ejecting
            if (currentLimitReached) {
                // back off eject power if panel gets jammed while ejecting
                newPower = Math.min(newPower, Constants.PANEL_HOLDER_REDUCED_EJECT_POWER);
            }
        }

        // check if new power setting is fast in either direction
        if (newPower < Constants.PANEL_HOLDER_HOLDING_POWER || newPower > 0) {
            if (rampingDown) {
                // ramp down cancelled by new higher power setting
                DriverStation.reportError(++logSeq + ": Ramp down cancelled, timer: " +
                        rampDownTimer.get() + ", new power: " + newPower +
                        ", current power: " + power, false);
                rampDownTimer.stop();
                rampDownTimer.reset();
                rampingDown = false;
            }
        }
        // at holding power, so check if motor is currently running fast in either direction
        else if (power < Constants.PANEL_HOLDER_HOLDING_POWER || power > 0) {
            // changing from fast speed to holding power, so start ramp down
            DriverStation.reportError(++logSeq + ": Ramp down start, timer: " +
                    rampDownTimer.get() + ", new power: " + newPower +
                    ", current power: " + power, false);
            rampDownTimer.stop();
            rampDownTimer.reset();
            rampDownTimer.start();
            rampingDown = true;
        }
        // check if ramp down is complete
        else if (rampDownTimer.get() >= 0.5) {
            DriverStation.reportError(++logSeq + ": Ramp down complete, timer: " +
                    rampDownTimer.get() + ", new power: " + newPower +
                    ", current power: " + power, false);
            rampDownTimer.stop();
            rampDownTimer.reset();
            rampingDown = false;
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
        SmartDashboard.putBoolean("PH Panel detected", hasPanel());
        SmartDashboard.putNumber("PH Power", victor.getMotorOutputPercent());
        SmartDashboard.putNumber("PH Current", getCurrent());
        SmartDashboard.putNumber("PH Ramp down timer: ", rampDownTimer.get());
        SmartDashboard.putString("PH ExtendedPosition", getExtendedPosition().toString());
        SmartDashboard.putBoolean("PH Extended raw", getExtendedPosition().value);
    }

    public void setExtendingSolenoid(ExtendedPosition position) {
        extendingSolenoid.set(position.get());
    }

    private boolean hadPanel = false;

    public boolean hasPanel() {

        // use current detection only since bumper switch is not reliable

        boolean havePanel;

        // check if motor is ramping down to holding power
        if (rampDownTimer.get() > 0) {
            // use previous value until motor speed settles and we can measure current reliably
            havePanel = hadPanel;
        }
        // check if ejecting
        else if (power >= 0) {
            // assume that panel is gone until end of eject sequence so we don't
            // mistakenly start an intake sequence
            havePanel = false;
        }
        // if are pulling in the panel, assume we have it until the pull-in timer expires
        else if (holdTimer.get() > 0) {
            //DriverStation.reportError(++logSeq + ": Pulling in panel", false);
            havePanel = true;
        }
        // if we are forcefully intaking, don't erroneously think we have a panel due to the higher than normal current
        else if (power < Constants.PANEL_HOLDER_HOLDING_POWER) {
            havePanel = (getCurrent() >= Constants.PANEL_HOLDER_MAX_CURRENT);
            if (havePanel) {
                DriverStation.reportError(++logSeq + ": Panel holder over max current", false);
            }
        }
        // we are at or below holding power
        else {
            havePanel = (getCurrent() >= Constants.PANEL_HOLDER_PANEL_DETECT_CURRENT);
            if (havePanel && !hadPanel) {
                DriverStation.reportError(++logSeq + ": Detected panel, power: " + power +
                        ", current:" + getCurrent(), false);
            }
        }
        hadPanel = havePanel;
        return havePanel;
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
