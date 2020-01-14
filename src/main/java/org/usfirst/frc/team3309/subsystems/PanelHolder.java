package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import edu.wpi.first.wpilibj.*;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.commands.panelholder.PanelHolderManual;
import org.usfirst.frc.team4322.commandv2.Subsystem;
import java.util.concurrent.Semaphore;

public class PanelHolder extends Subsystem {

    private boolean debugPanelHolder = false;

    private WPI_VictorSPX victor;
    private Solenoid extendingSolenoid;

    private boolean panelPulledIn = false;
    private boolean currentLimitReached = false;
    private Timer pullInTimer = new Timer();
    private Timer rampDownTimer = new Timer();
    private boolean isRampingDown = false;
    private Timer detectionDebounceTimer = new Timer();
    private double power;
    private int logSeq = 0;
    static Semaphore setPowerMutex = new Semaphore(1);
    static Semaphore hasPanelMutex = new Semaphore(1);

    public PanelHolder() {
        victor = new WPI_VictorSPX(Constants.PANEL_HOLDER_VICTOR_ID);
        victor.configOpenloopRamp(0.3);
        victor.setNeutralMode(NeutralMode.Brake);
        extendingSolenoid = new Solenoid(Constants.PANEL_HOLDER_TELESCOPING_SOLENOID_ID);
    }

    @Override
    public void initDefaultCommand() {
        setDefaultCommand(new PanelHolderManual());
    }

    public void setPower(double newPower) {

        // This method is called periodically and might not complete before the next call
        // due to debug messages. It isn't thread safe, so we need to protect it with a mutex.
        try {
            setPowerMutex.acquire();
        } catch (InterruptedException e) {
            DriverStation.reportError("Mutex acquire interrupted", false);
        }

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
            if (pullInTimer.get() > 0.25) {
                // after panel is pulled in, allow intake power to return to holding power
                pullInTimer.stop();
                pullInTimer.reset();
            }
            if (pullInTimer.get() > 0) {
                // use reduced power while pulling panel in to avoid overloading motor
                newPower = Constants.PANEL_HOLDER_REDUCED_INTAKE_POWER;
            } else if (hasPanel()) {
                if (!panelPulledIn) {
                    // pull in panel, but back-off intake power to not overload motor
                    pullInTimer.stop();
                    pullInTimer.reset();
                    pullInTimer.start();
                    panelPulledIn = true;
                    newPower = Constants.PANEL_HOLDER_REDUCED_INTAKE_POWER;
                    if (debugPanelHolder) {
                        DriverStation.reportError(++logSeq + ": Start pulling in panel, newPower: " +
                                newPower + ", current power: " + power, false);
                    }
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
            pullInTimer.stop();
            pullInTimer.reset();
            if (currentLimitReached) {
                // back off eject power if panel gets jammed while ejecting
                newPower = Math.min(newPower, Constants.PANEL_HOLDER_REDUCED_EJECT_POWER);
            }
        }

        // check if new power setting is fast in either direction
        if (newPower < Constants.PANEL_HOLDER_HOLDING_POWER || newPower > 0) {
            if (isRampingDown) {
                // ramp down cancelled by new higher power setting
                if (debugPanelHolder) {
                    DriverStation.reportError(++logSeq + ": Ramp down cancelled, timer: " +
                            rampDownTimer.get() + ", new power: " + newPower +
                            ", current power: " + power, false);
                }
                rampDownTimer.stop();
                rampDownTimer.reset();
                isRampingDown = false;
            }
        }
        // at holding power, so check if motor is currently running fast in either direction
        else if (power < Constants.PANEL_HOLDER_HOLDING_POWER || power > 0) {
            // changing from fast speed to holding power, so start ramp down
            if (debugPanelHolder) {
                DriverStation.reportError(++logSeq + ": Ramp down start, timer: " +
                        rampDownTimer.get() + ", new power: " + newPower +
                        ", current power: " + power, false);
            }
            rampDownTimer.stop();
            rampDownTimer.reset();
            rampDownTimer.start();
            isRampingDown = true;
        }
        // check if ramp down is complete
        else if (rampDownTimer.get() >= 0.5) {
            if (debugPanelHolder) {
                DriverStation.reportError(++logSeq + ": Ramp down complete, timer: " +
                        rampDownTimer.get() + ", new power: " + newPower +
                        ", current power: " + power, false);
            }
            rampDownTimer.stop();
            rampDownTimer.reset();
            isRampingDown = false;
        }

        power = newPower;
        victor.set(ControlMode.PercentOutput, power);

        setPowerMutex.release();
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

    public void setExtendingSolenoid(ExtendedPosition position) {
        extendingSolenoid.set(position.get());
    }

    private boolean hadPanel = false;
    private boolean isDebouncingDetection = false;

    public boolean hasPanel() {

        // use current detection only since bumper switch is not reliable

        boolean havePanel;

        // This method is called periodically and from multiple threads.
        // It isn't thread safe, so we need to protect it with a mutex.
        try {
            hasPanelMutex.acquire();
        } catch (InterruptedException e) {
            DriverStation.reportError("Mutex acquire interrupted", false);
        }

        // check if motor is ramping down to holding power
        if (isRampingDown) {
            // use previous value until motor speed settles and we can measure current reliably
            havePanel = hadPanel;
        }
        // check if pulling in panel
        else if (pullInTimer.get() > 0) {
            // don't think we lost the panel if current drops slightly below max due to reduced pull-in power
            havePanel = true;
        }
        // check if ejecting
        else if (power >= 0) {
            // assume that panel is gone until end of eject sequence so we don't
            // mistakenly start an intake sequence
            havePanel = false;
        }
        // if we are forcefully intaking, don't erroneously think we have a panel due to the higher than normal current
        else if (power < Constants.PANEL_HOLDER_HOLDING_POWER) {
            havePanel = (getCurrent() >= Constants.PANEL_HOLDER_MAX_CURRENT);
        }
        // we are at or below holding power
        else {
            havePanel = (getCurrent() >= Constants.PANEL_HOLDER_PANEL_DETECT_CURRENT);
        }

        // check if debounce time has elapsed
        if (detectionDebounceTimer.get() > 0.5) {
            detectionDebounceTimer.stop();
            detectionDebounceTimer.reset();
            isDebouncingDetection = false;
        }

        // check for change in panel detection state
        if (havePanel && !hadPanel) {
            detectionDebounceTimer.stop();
            detectionDebounceTimer.reset();
            detectionDebounceTimer.start();
            isDebouncingDetection = true;
            if (debugPanelHolder) {
                DriverStation.reportError(++logSeq + ": Detected panel, power: " + power +
                        ", current:" + getCurrent(), false);
            }
        }
        else if (!havePanel && hadPanel) {
            // don't accept loss of panel during detection debounce window
            if (isDebouncingDetection) {
                havePanel = hadPanel;
            }
            else if (debugPanelHolder) {
                DriverStation.reportError(++logSeq + ": Lost panel, power: " + power +
                        ", current:" + getCurrent(), false);
            }
        }

        hadPanel = havePanel;
        hasPanelMutex.release();
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

    }

    public void outputToDashboard() {
        SmartDashboard.putBoolean("PH Panel detected", hasPanel());
        // don't read power from the controller because the request can timeout when over max current
        // and prevent us from executing the code that cuts the power
        SmartDashboard.putNumber("PH Power", power);
        SmartDashboard.putNumber("PH Current", getCurrent());
        SmartDashboard.putNumber("PH Ramp down timer: ", rampDownTimer.get());
        SmartDashboard.putNumber("PH Detection debounce timer: ", detectionDebounceTimer.get());
        SmartDashboard.putString("PH ExtendedPosition", getExtendedPosition().toString());
        SmartDashboard.putBoolean("PH Extended raw", getExtendedPosition().value);
    }
}
