package org.usfirst.frc.team3309;

import org.usfirst.frc.team3309.lib.Limelight;
import org.usfirst.frc.team3309.lib.PIDController;
import org.usfirst.frc.team3309.lib.util.DriveSignal;
import org.usfirst.frc.team3309.subsystems.Vision;

public class VisionHelper {

    private static Limelight limelight = Vision.panelLimelight;

    private static PIDController turnController = new PIDController("turn", 0.07, 0.00001, 0.0);
    private static PIDController throttleController = new PIDController("throttle", 0.0, 0.0, 0.0);

    private static final boolean isDashboard = false;

    private static final double PANEL_HEIGHT = 0.0;
    private static final double CAMERA_HEIGHT = 0.0;

    public static DriveSignal getDriveSignal() {
        double linearPower = getThrottleCorrection();
        double angularPower = getTurnCorrection();
        return new DriveSignal(linearPower + angularPower,
                linearPower - angularPower);
    }

    public static double getThrottleCorrection() {
        return throttleController.update(getDist());
    }

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

    public static double getDist() {
        return Math.abs(PANEL_HEIGHT - CAMERA_HEIGHT) / Math.tan(limelight.getTy());
    }

}
