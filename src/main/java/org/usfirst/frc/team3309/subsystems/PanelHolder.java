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
        jointedSolenoid = new Solenoid(Constants.PANEL_HOLDER_EXTENDING_SOLENOID_ID);
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
        SmartDashboard.putBoolean("PH bumper pressed", isBumperPressed());
    }

    private void setJointedSolenoid(JointedPosition position) {
        jointedSolenoid.set(position.get());
    }

    private void setExtendingSolenoid(ExtendedPosition position) {
       extendingSolenoid.set(position.get());
    }

    public boolean isBumperPressed() {
        return bumperSensor.get();
    }

    public enum PanelHolderPosition {
        ReleasePanel,
        PlacePanel,
        GrabPanel,
        Unknown
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
    }

    public enum ExtendedPosition {
        ExtendedOutwards(true),
        RetractedInwards(false);

        private boolean value;

        ExtendedPosition(boolean value){
            this.value = value;
        }

        public boolean get(){
            return value;
        }
    }

}
