package org.usfirst.frc.team3309;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.lib.Limelight;
import org.usfirst.frc.team3309.lib.PIDController;
import org.usfirst.frc.team3309.lib.util.DriveSignal;
import org.usfirst.frc.team3309.subsystems.Vision;

public class VisionHelper {

    private static Limelight limelight = Vision.panelLimelight;

    private static PIDController turnController = new PIDController("turn", 0.02509,0.0, 0.0);
    private static PIDController throttleController = new PIDController("throttle", 0.0, 0.0, 0.0);

    private static final boolean isDashboard = true;
    private static Limelight.CamMode curCamMode = Limelight.CamMode.DriverCamera;
    private static int curPipeline = 0;
    private static Limelight.LEDMode curLed;

    private static final double PANEL_HEIGHT = 28.875;
    private static final double CAMERA_HEIGHT = 33.25;
    private static final double CAMERA_MOUNTING_ANGLE = -7.1;

    static {
        turnController.outputToDashboard();
    }

    public static DriveSignal getDriveSignal() {
        init();
        double linearPower = getThrottleCorrection();
        double angularPower = getTurnCorrection();
        return new DriveSignal(linearPower + angularPower,
                linearPower - angularPower);
    }

    private static void init() {
        if (isDashboard) {
            turnController.readDashboard();
        }
    }

    public static void turnOn() {
        turnController.reset();
        init();
        setCamMode(Limelight.CamMode.VisionProcessor);
        setPipeline(0);
        setLed(Limelight.LEDMode.On);
    }

    public static void turnOff() {
        setCamMode(Limelight.CamMode.DriverCamera);
        setLed(Limelight.LEDMode.Off);
    }

    public static double getThrottleCorrection() {
        return throttleController.update(getDist());
    }

    public static double getTurnCorrection() {
        init();

        if (!limelight.hasTarget()) {
            return 0.0;
        }

        double angularPower = 0.0;

        if (limelight.getArea() < 15.0) {
            double limelightAngle = limelight.getTx();
            angularPower = turnController.update(limelightAngle);
        }

        return angularPower;
    }

    public static double getDist() {
        return Math.abs(PANEL_HEIGHT - CAMERA_HEIGHT) /
                Math.tan(Math.toRadians(limelight.getTy() + CAMERA_MOUNTING_ANGLE));
    }

    private static void setCamMode(Limelight.CamMode camMode) {
        if (curCamMode != camMode) {
            limelight.setCamMode(camMode);
            curCamMode = camMode;
        }
    }

    private static void setPipeline(int pipeline) {
        if (curPipeline != pipeline) {
            limelight.setPipeline(pipeline);
            curPipeline = pipeline;
        }
    }

    private static void setLed(Limelight.LEDMode led) {
        if (curLed != led) {
            limelight.setLed(led);
            curLed = led;
        }
    }

    public static boolean hasTargets() {
        return limelight.hasTarget();
    }

    public static void outputToDashboard() {
        SmartDashboard.putNumber("Distance from target", getDist());
    }

}
