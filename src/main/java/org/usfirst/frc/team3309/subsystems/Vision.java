package org.usfirst.frc.team3309.subsystems;

import org.usfirst.frc.team3309.lib.Limelight;
import org.usfirst.frc.team4322.commandv2.Subsystem;

public class Vision extends Subsystem {

    public static Limelight panelLimelight;

    public Vision() {
        panelLimelight = new Limelight("limelight-panel");
        panelLimelight.setLed(Limelight.LEDMode.Off);
        panelLimelight.setCamMode(Limelight.CamMode.DriverCamera);
    }

    public double getAngle(Limelight limelight) {
       return limelight.getTx();
    }

    public void setLed(Limelight limelight, Limelight.LEDMode mode) {
        limelight.setLed(mode);
    }


}
