package org.usfirst.frc.team3309.subsystems;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.commands.LimelightBlinkKt;
import org.usfirst.frc.team3309.lib.Limelight;
import org.usfirst.frc.team4322.commandv2.Subsystem;

public class Vision extends Subsystem {

    public static Limelight panelLimelight;

    private boolean hasBlinked;
    private boolean sentMessage;

    public Vision() {
        panelLimelight = new Limelight("limelight-panel");
        panelLimelight.setLed(Limelight.LEDMode.Off);
        panelLimelight.setCamMode(Limelight.CamMode.DriverCamera);
    }

    @Override
    public void periodic() {
//        boolean hasCargo = Robot.cargoHolder.hasCargo();
//        boolean hasPanel = Robot.panelHolder.hasPanel();
        boolean hasCargo = false;
        boolean hasPanel = false;

        boolean hasGamePiece = hasCargo || hasPanel;
        boolean hasBothGamePieces = hasCargo && hasPanel;

        if (!hasBothGamePieces) {
            if (hasGamePiece && !hasBlinked) {
                LimelightBlinkKt.LimelightFlash().start();
                hasBlinked = true;
            } else if (!hasGamePiece) {
                hasBlinked = false;
            }
        }

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
