package org.usfirst.frc.team3309;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.lib.Limelight;
import org.usfirst.frc.team3309.lib.PIDController;
import org.usfirst.frc.team3309.lib.util.DriveSignal;
import org.usfirst.frc.team3309.lib.util.PolynomialRegression;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team3309.subsystems.Vision;

public class VisionHelper {

    private static Limelight limelight = Vision.panelLimelight;

    private static final boolean isTuning = false;
    private static final boolean forceVisionOn = true;
    private static boolean loadStation3D = true;

    private static final double turnP = 0.023;
    private static final double turnD = 0.0;
    private static final double kMaxVisionAngularPower = 0.2;

    // Use smaller P when far away to avoid overshoot from potentially large initial correction.
    // Use larger P when closer to provide enough angular power for fine corrections.
    private static PIDController turnController = new PIDController("Vision turn", turnP, 0, turnD);

    private static boolean driverOverrideActive = false;

    private static Timer timer = new Timer();

    private static Limelight.CamMode curCamMode = Limelight.CamMode.DriverCamera;
    private static int curPipeline = -1;
    private static Limelight.LEDMode curLed;
    private static boolean isStopCrawl;
    private static boolean visionOn = false;
    private static boolean  visionThrottleEnabled = true;
    private static boolean  visionTurningEnabled = true;
    private static double distanceCorrectionFactor;  // used to scale vision corrections based on distance to target
    private static final String visionThrottleKey = "Vision throttle enabled";
    private static final String visionTurningKey = "Vision turning enabled";

    static {
        if (isTuning) {
            turnController.outputToDashboard();
            SmartDashboard.putBoolean(visionThrottleKey, visionThrottleEnabled);
            SmartDashboard.putBoolean(visionTurningKey, visionTurningEnabled);
        }
        if (forceVisionOn) {
            enableVision();
        }
        else {
            disableVision();
        }
        setCamMode(Limelight.CamMode.VisionProcessor);
    }

    public static DriveSignal getDriveSignal(boolean loadingMode) {
        if (limelight.getArea() < 15.0) {
            double linearPower = getThrottle();

            if (isStopCrawl) {
                linearPower = 0.0;
            }

            // Select correction scale factor based on distance to target.
            // When we are closer to the target the angular error increases faster with forward motion.
            // Therefore, we need to apply a larger angular correction.
            // We can't apply a larger correction when far away from the target because the turn would
            // overshot the target before the next update and cause oscillations.
            // Tuning the stages of this is likely to be a bit tricky. :)
            distanceCorrectionFactor = 1;
            if (limelight.getArea() >= 4.0) {
                distanceCorrectionFactor = 1.5;
            }
            else if (limelight.getArea() >= 3.0) {
                distanceCorrectionFactor = 1.3;
            }
            else if (limelight.getArea() >= 2.0) {
                distanceCorrectionFactor = 1.15;
            }

            double angularPower = getTurnCorrection(loadingMode) * distanceCorrectionFactor;

            angularPower = Util.clamp(angularPower,
                    -kMaxVisionAngularPower,
                    kMaxVisionAngularPower);

            return new DriveSignal(linearPower + angularPower,
                    linearPower - angularPower);
        }
        return DriveSignal.NEUTRAL;
    }

    public static void turnOn() {
        if (!visionOn) {
            enableVision();
        }
    }

    public static void turnOff() {
        if (visionOn) {
            disableVision();
        }
    }

    private static void enableVision() {
        visionOn = true;
        turnController.reset();
        if (isTuning) {
            turnController.readDashboard();
        }
        setPipeline(Constants.kVisionCenterPipeline);
        setLed(Limelight.LEDMode.On);
        timer.reset();
        timer.start();
    }

    private static void disableVision(){
        visionOn = false;
        timer.stop();
        isStopCrawl = false;
        if (!forceVisionOn) {
            setLed(Limelight.LEDMode.Off);
            setPipeline(Constants.kDriverPipeline);
        }
    }

    // for demos
    public static void forceVisionOff() {
        setLed(Limelight.LEDMode.Off);
        setPipeline(Constants.kDriverPipeline);
    }
    public static void driverOverride(boolean mode) {
        if (mode && !driverOverrideActive) {
            setLed(Limelight.LEDMode.Off);
            setPipeline(Constants.kDriverPipeline);
            driverOverrideActive = true;
        }
        else if (!mode && driverOverrideActive) {
            setPipeline(Constants.kVisionCenterPipeline);
            setLed(Limelight.LEDMode.On);
            driverOverrideActive = false;
        }

    }

    public static double visionThrottle;

    public static double getThrottle() {
        if (SmartDashboard.getBoolean(visionThrottleKey, visionThrottleEnabled)) {

            // slow down as we approach the target
            double area = limelight.getArea();
            if (area < 0.8) {
                visionThrottle = 0.6;
            }
            else if (area < 1) {
                visionThrottle = 0.5;
            }
            else if (area < 2) {
                visionThrottle = 0.45;
            }
            else if (area < 3) {
                visionThrottle = 0.35;
            }
            else visionThrottle = 0.2;

            // ramp up slowly at start to avoid jerking the carriage when it's raised
            if (Robot.elevator.getCarriagePercentage() > 0.1) {
                if (getTimeElasped() < 0.07) {
                    visionThrottle = Math.min(visionThrottle, 0.20);
                } else if (getTimeElasped() < 0.12) {
                    visionThrottle = Math.min(visionThrottle, 0.30);
                } else if (getTimeElasped() < 0.2) {
                    visionThrottle = Math.min(visionThrottle, 0.40);
                } else {
                    visionThrottle = Math.min(visionThrottle, 0.60);
                }
            }

            return visionThrottle;
        }
        return 0;
    }

    public static double visionTurnError;
    public static double visionTurnCorrection;

    public static double getTurnCorrection(boolean loadingMode) {

        // don't use 3D vision when picking up at loading station
        // because panel blocks bottom of vision tape
        if (limelight.has3D() && (!loadingMode || loadStation3D)) {
            visionTurnError = limelight.rotationCenterDegrees3D();
        }
        else {
            visionTurnError = limelight.getTx();
        }
        visionTurnCorrection = turnController.update(visionTurnError, 0.0);

        if (SmartDashboard.getBoolean(visionTurningKey, visionTurningEnabled)) {
            return visionTurnCorrection;
        }
        return 0;
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

    public static void stopCrawl() {
        isStopCrawl = true;
    }

    public static double getTimeElasped() {
        return timer.get();
    }

    public static boolean hasTargets() {
        return limelight.hasTarget() && Util.within(getSkew(), -10.0, 10.0);
    }

    public static void outputToDashboard() {
        SmartDashboard.putNumber("Vision throttle", visionThrottle);
        SmartDashboard.putNumber("Vision turn error", visionTurnError);
        SmartDashboard.putNumber("Vision turn correction", visionTurnCorrection);
        Vision.panelLimelight.outputToDashboard();
    }

}
