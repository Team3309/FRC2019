package org.usfirst.frc.team3309.subsystems;

import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.commands.LimelightBlinkKt;
import org.usfirst.frc.team3309.lib.Limelight;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team4322.commandv2.Subsystem;


public class Vision extends Subsystem {

    public static Limelight panelLimelight;

    public Vision() {
        panelLimelight = new Limelight("limelight-panel",
                Constants.kPanelLimelightInchesX,
                Constants.kPanelLimelightPlacementInchesZ,
                Constants.kPanelLimelightRotationCenterInchesZ);
        panelLimelight.setLed(Limelight.LEDMode.Off);
        panelLimelight.setCamMode(Limelight.CamMode.DriverCamera);
    }

    @Override
    public void periodic() {
    }

    public void setLed(Limelight.LEDMode mode) {
        panelLimelight.setLed(mode);
    }

    public double getTargetArea() {
        return panelLimelight.getArea();
    }

}
