package org.usfirst.frc.team3309.subsystems;

import edu.wpi.first.wpilibj.Solenoid;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team4322.commandv2.Subsystem;

public class PanelPlacer extends Subsystem {

    private Solenoid fingerSolenoid;
    private Solenoid telescopingSolenoid;

    public PanelPlacer() {
        fingerSolenoid = new Solenoid(Constants.PANEL_PLACER_FINGER_SOLENOID_ID);
        telescopingSolenoid = new Solenoid(Constants.PANEL_PLACER_TELESCOPING_SOLENOID_ID);
    }

    public void setFingerSolenoid(boolean on) {
        fingerSolenoid.set(on);
    }

    public boolean getFingerState() {
        return fingerSolenoid.get();
    }

    public void setTelescopingSolenoid(boolean on) {
        telescopingSolenoid.set(on);
    }

    public boolean getTelescopingState() {
        return telescopingSolenoid.get();
    }

}
