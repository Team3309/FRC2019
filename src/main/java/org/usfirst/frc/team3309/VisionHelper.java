package org.usfirst.frc.team3309;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.lib.Limelight;
import org.usfirst.frc.team3309.lib.PIDController;
import org.usfirst.frc.team3309.lib.util.DriveSignal;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team3309.subsystems.Vision;

public class VisionHelper {

    private static Limelight limelight = Vision.panelLimelight;

    private static PIDController turnController = new PIDController("turn", 0.012, 0.000, 0.00);
    private static PIDController throttleController = new PIDController("throttle", 0.013, 0.0011, 0.0);
    private static PIDController skewController = new PIDController("skew", 0.0, 0.0, 0.0);

    private static final boolean isDashboard = true;
    private static Limelight.CamMode curCamMode = Limelight.CamMode.DriverCamera;
    private static int curPipeline = 0;
    private static Limelight.LEDMode curLed;

    private static final double skewGain = 1.0;
    private static final double PANEL_HEIGHT = 28.875;
    private static final double CAMERA_HEIGHT = 33.1875;
    private static final double CAMERA_MOUNTING_ANGLE = -10.236;
    private static final double LIMELIGHT_FRONT_OFFSET = 24.0;

    static {
        turnController.outputToDashboard();
        throttleController.outputToDashboard();
        skewController.outputToDashboard();
        VisionHelper.turnOff();
    }

    public static DriveSignal getDriveSignal() {
        init();
        if (limelight.getArea() < 15.0) {
            double linearPower = getThrottleCorrection();
            double angularPower = getTurnCorrection();
            double skewPower = -getSkewCorrection();
            SmartDashboard.putNumber("Skew vision power", skewPower);
            SmartDashboard.putNumber("Throttle vision power", linearPower);
            SmartDashboard.putNumber("Turn vision power", angularPower);
            return new DriveSignal(linearPower + angularPower + skewPower,
                    linearPower - angularPower - skewPower);
        }
        return DriveSignal.NEUTRAL;
    }

    private static void init() {
        turnController.reset();
        throttleController.reset();
        skewController.reset();
        if (isDashboard) {
            turnController.readDashboard();
            throttleController.readDashboard();
            skewController.readDashboard();
        }
    }

    public static void turnOn() {
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
        return -throttleController.update(getDist(), 3);
    }

    public static double getTurnCorrection() {
        return -turnController.update(limelight.getTx());
    }

    public static double getSkewCorrection() {
        return skewGain * skewController.update(getSkew()) * getSkewScale(getDist(),
                10, 100);
    }

    public static double getSkew() {
        double skew;
        if (limelight.getSkew() < -45) {
            skew = limelight.getSkew() + 90;
        } else {
            skew = limelight.getSkew();
        }
        return skew;
    }


    private static double getSkewScale(double dist, double min, double max) {
        if (!Util.within(dist, min, max)) {
            return 0.0;
        }
        return (dist - min) / (max - min);
    }

    public static double getDist() {
        return ((PANEL_HEIGHT - CAMERA_HEIGHT) /
                Math.tan(Math.toRadians(limelight.getTy() + CAMERA_MOUNTING_ANGLE)))
                - LIMELIGHT_FRONT_OFFSET;
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
        return limelight.hasTarget() && Util.within(getSkew(), -5.0, 5.0);
    }

    public static void outputToDashboard() {
        SmartDashboard.putNumber("Distance from target", getDist());
    }

}
