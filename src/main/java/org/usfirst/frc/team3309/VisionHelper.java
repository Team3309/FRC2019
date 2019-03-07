package org.usfirst.frc.team3309;

import org.usfirst.frc.team3309.lib.PIDController;
import org.usfirst.frc.team3309.subsystems.Vision;

public class VisionHelper {

    private static Vision.Limelight limelight = Vision.panelLimelight;
    private static PIDController turnController = new PIDController("turn", 0.07, 0.00001, 0.0);

    private static final boolean isDashboard = false;

    public static double getTurnCorrection() {
        if (isDashboard) {
            turnController.outputToDashboard();
            turnController.readDashboard();
        }

        if (!limelight.hasTarget()) {
            return 0.0;
        }
        double angularPower = 0.0;
        if (limelight.getArea() < 14.0) {
            double limelightAngle = limelight.getTx();
            angularPower = turnController.update(limelightAngle);
        }
        return angularPower;
    }

}
