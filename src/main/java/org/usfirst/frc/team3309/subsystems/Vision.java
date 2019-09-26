package org.usfirst.frc.team3309.subsystems;

import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.commands.LimelightBlinkKt;
import org.usfirst.frc.team3309.lib.Limelight;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team4322.commandv2.Subsystem;


public class Vision extends Subsystem {

    public static Limelight panelLimelight;

    private boolean sentMessage;

    public Vision() {
        panelLimelight = new Limelight("limelight-panel",
                Constants.kPanelLimelightInchesX, Constants.kPanelLimelightInchesZ);
        panelLimelight.setLed(Limelight.LEDMode.Off);
        panelLimelight.setCamMode(Limelight.CamMode.DriverCamera);
    }

    @Override
    public void periodic() {
        boolean hasCargo = Robot.cargoHolder.hasCargo();
        boolean hasPanel = Robot.panelHolder.hasPanel();

        boolean hasGamePiece = hasCargo || hasPanel;
        boolean hasBothGamePieces = hasCargo && hasPanel;
        
//        if (hasBothGamePieces && !sentMessage) {
//            new LimelightMorseCode().start();
//            sentMessage = true;
//        } else if (!hasBothGamePieces) {
//            sentMessage = false;
//        }

    }

    public void setLed(Limelight.LEDMode mode) {
        panelLimelight.setLed(mode);
    }

    public double getTargetArea() {
        return panelLimelight.getArea();
    }

}
