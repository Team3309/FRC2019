package org.usfirst.frc.team3309.subsystems;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.commands.LimelightBlinkKt;
import org.usfirst.frc.team3309.lib.Limelight;
import org.usfirst.frc.team4322.commandv2.Subsystem;

public class Vision extends Subsystem {

    public static Limelight panelLimelight;

    private boolean hasBlinked;

    public Vision() {
        panelLimelight = new Limelight("limelight-panel");
        panelLimelight.setLed(Limelight.LEDMode.Off);
        panelLimelight.setCamMode(Limelight.CamMode.DriverCamera);
    }

    @Override
    public void periodic() {
        boolean hasGamePiece = Robot.cargoHolder.hasCargo() || Robot.panelHolder.hasPanel();
        if (hasGamePiece && !hasBlinked) {
            LimelightBlinkKt.LimelightBlink().start();
            hasBlinked = true;
        }  else if (!hasGamePiece) {
            hasBlinked = false;
        }
    }

    public void setLed(Limelight.LEDMode mode) {
        panelLimelight.setLed(mode);
    }

}
