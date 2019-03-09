package org.usfirst.frc.team3309.subsystems;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team4322.commandv2.Subsystem;

public class PanelHolder extends Subsystem {

    private Solenoid jointedSolenoid;
    private Solenoid extendingSolenoid;

    private DigitalInput bumperSensor;

    public PanelHolder() {
        jointedSolenoid = new Solenoid(Constants.PANEL_HOLDER_JOINTED_SOLENOID_ID);
        extendingSolenoid = new Solenoid(Constants.PANEL_HOLDER_TELESCOPING_SOLENOID_ID);
        bumperSensor = new DigitalInput(Constants.PANEL_HOLDER_BUMPER_SENSOR_PORT);
        addChild(jointedSolenoid);
        addChild(extendingSolenoid);
        addChild(bumperSensor);
    }

    /*
     * @param position, the desired configuration
     * */
    public void setPosition(PanelHolderPosition position) {
        switch (position) {
            case ReleasePanel:
                setExtendingSolenoid(ExtendedPosition.RetractedInwards);
                setJointedSolenoid(JointedPosition.PointingOutwards);
                break;
            case Extended:
                setExtendingSolenoid(ExtendedPosition.ExtendedOutwards);
                setJointedSolenoid(JointedPosition.PointingOutwards);
                break;
            case GrabPanel:
                setExtendingSolenoid(ExtendedPosition.RetractedInwards);
                setJointedSolenoid(JointedPosition.Vertical);
                break;
            case FingerVertical:
                setJointedSolenoid(JointedPosition.Vertical);
                break;
            case TelescopeBack:
                setExtendingSolenoid(ExtendedPosition.RetractedInwards);
                break;
            case FingerPointingOutwards:
                setJointedSolenoid(JointedPosition.PointingOutwards);
                break;
            case TelescopeForwards:
                setExtendingSolenoid(ExtendedPosition.ExtendedOutwards);
                break;
        }
    }

    public void setPosition(JointedPosition jointedPosition, ExtendedPosition extendedPosition) {
        setExtendingSolenoid(extendedPosition);
        setJointedSolenoid(jointedPosition);
    }

    public JointedPosition getJointedPosition() {
        boolean isPointing = jointedSolenoid.get();
        if (isPointing == JointedPosition.PointingOutwards.get()) {
            return JointedPosition.PointingOutwards;
        } else {
            return JointedPosition.Vertical;
        }
    }

    public ExtendedPosition getExtendedPosition() {
        boolean isExtended = extendingSolenoid.get();
        if (isExtended == ExtendedPosition.ExtendedOutwards.get()) {
            return ExtendedPosition.ExtendedOutwards;
        } else {
            return ExtendedPosition.RetractedInwards;
        }
    }

    public void outputToDashboard() {
        SmartDashboard.putString("PH JointedPosition", getJointedPosition().toString());
        SmartDashboard.putBoolean("PH Jointed raw", getJointedPosition().value);
        SmartDashboard.putString("PH ExtendedPosition", getExtendedPosition().toString());
        SmartDashboard.putBoolean("PH Extended raw", getExtendedPosition().value);
        SmartDashboard.putBoolean("PH bumper pressed", hasPanel());
    }

    // TODO: make private and wrap them through their the main subsystem set function
    public void setJointedSolenoid(JointedPosition position) {
        jointedSolenoid.set(position.get());
    }

    public void setExtendingSolenoid(ExtendedPosition position) {
        extendingSolenoid.set(position.get());
    }

    public boolean hasPanel() {
        return !bumperSensor.get();
    }

    // TODO: revisit and clean up
    public enum PanelHolderPosition {
        ReleasePanel,
        Extended,
        GrabPanel,
        FingerVertical,
        FingerPointingOutwards,
        TelescopeBack,
        TelescopeForwards
    }

    public enum JointedPosition {
        PointingOutwards(true),
        Vertical(false);

        private boolean value;

        JointedPosition(boolean value) {
            this.value = value;
        }

        public boolean get() {
            return value;
        }

        public static JointedPosition fromBoolean(boolean value) {
            return value ? PointingOutwards : Vertical;
        }
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
