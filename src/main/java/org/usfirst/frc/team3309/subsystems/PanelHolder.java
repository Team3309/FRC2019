package org.usfirst.frc.team3309.subsystems;

import edu.wpi.first.wpilibj.Solenoid;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team4322.commandv2.Subsystem;

public class PanelHolder extends Subsystem {

    private Solenoid jointedSolenoid;
    private Solenoid extendingSolenoid;

    public PanelHolder() {
        jointedSolenoid = new Solenoid(Constants.PANEL_HOLDER_EXTENDING_SOLENOID_ID);
        extendingSolenoid = new Solenoid(Constants.PANEL_HOLDER_TELESCOPING_SOLENOID_ID);
    }

    /*
     * @param position, the desired configuration
     * */
    public void setPosition(PanelHolderPosition position) {
        if (position == PanelHolderPosition.ReleasePanel) {
            setExtendingSolenoid(ExtendedPosition.RetractedInwards);
            setJointedSolenoid(JointedPosition.PointingOutwards);
        } else if (position == PanelHolderPosition.PlacePanel) {
            setExtendingSolenoid(ExtendedPosition.ExtendedOutwards);
            setJointedSolenoid(JointedPosition.PointingOutwards);
        } else if (position == PanelHolderPosition.GrabPanel) {
            setExtendingSolenoid(ExtendedPosition.RetractedInwards);
            setJointedSolenoid(JointedPosition.Vertical);
        }
    }

    /*
     * @return current PanelHolder configuration
     * */
    public PanelHolderPosition getPosition() {
        JointedPosition jointedPosition = getJointedPosition();
        ExtendedPosition extendedPosition = getExtendedPosition();

        if (extendedPosition == ExtendedPosition.RetractedInwards
                && jointedPosition == JointedPosition.PointingOutwards) {
            return PanelHolderPosition.ReleasePanel;
        } else if (extendedPosition == ExtendedPosition.ExtendedOutwards
                && jointedPosition == JointedPosition.PointingOutwards) {
            return PanelHolderPosition.PlacePanel;
        } else if (extendedPosition == ExtendedPosition.RetractedInwards
                && jointedPosition == JointedPosition.Vertical) {
            return PanelHolderPosition.GrabPanel;
        }
        return PanelHolderPosition.Unknown;
    }

    public JointedPosition getJointedPosition() {
        boolean isPointing = jointedSolenoid.get();
        if (isPointing) {
            return JointedPosition.PointingOutwards;
        } else {
            return JointedPosition.Vertical;
        }
    }

    public ExtendedPosition getExtendedPosition() {
        boolean isExtended = extendingSolenoid.get();
        if (isExtended) {
            return ExtendedPosition.ExtendedOutwards;
        } else {
            return ExtendedPosition.RetractedInwards;
        }
    }

    private void setJointedSolenoid(JointedPosition position) {
        if (position == JointedPosition.PointingOutwards) {
            jointedSolenoid.set(true);
        } else if (position == JointedPosition.Vertical) {
            jointedSolenoid.set(false);
        }
    }

    private void setExtendingSolenoid(ExtendedPosition position) {
        if (position == ExtendedPosition.ExtendedOutwards) {
            extendingSolenoid.set(true);
        } else if (position == ExtendedPosition.RetractedInwards) {
            extendingSolenoid.set(false);
        }
    }

    public enum PanelHolderPosition {
        ReleasePanel,
        PlacePanel,
        GrabPanel,
        Unknown
    }

    public enum JointedPosition {
        PointingOutwards,
        Vertical,
    }

    public enum ExtendedPosition {
        ExtendedOutwards,
        RetractedInwards,
    }

}
