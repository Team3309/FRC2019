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
    private static boolean loadStation3D = false;

    private static final double farTurnP = 0.023;
    private static final double farTurnI = 0.0;
    private static final double farTurnD = 0.0;
    private static final double closeTurnP = 0.05;
    private static final double closeTurnI = 0.0;
    private static final double closeTurnD = 0.0;
    private static final double kFarMaxVisionAngularPower = 0.2;
    private static final double kCloseMaxVisionAngularPower = 0.35;
    private static double maxVisionAngularPower = kFarMaxVisionAngularPower;

    // Use smaller P when far away to avoid overshoot from potentially large initial correction.
    // Use larger P when closer to provide enough angular power for fine corrections.
    private static PIDController farTurnController = new PIDController("Far turn", farTurnP, farTurnI, farTurnD);
    private static PIDController closeTurnController = new PIDController("Close turn", closeTurnP, closeTurnI, closeTurnD);

    private static PolynomialRegression linearRegression;
    private static boolean driverOverrideActive = false;

    private static Timer timer = new Timer();

    private static PIDController turnController = farTurnController;
    private static Limelight.CamMode curCamMode = Limelight.CamMode.DriverCamera;
    private static int curPipeline = -1;
    private static Limelight.LEDMode curLed;
    private static boolean isStopCrawl;
    private static boolean visionOn = false;
    private static boolean  visionThrottleEnabled = true;
    private static boolean  visionTurningEnabled = true;
    private static final String visionThrottleKey = "Vision throttle enabled";
    private static final String visionTurningKey = "Vision turning enabled";

    static {
        if (isTuning) {
            farTurnController.outputToDashboard();
            closeTurnController.outputToDashboard();
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

        // points for line to vision target (target area, motor power)
        double[][] motorTrajectory = new double[][]{
                {0.5, 0.4},
                {9.71, 0.03},
        };

        linearRegression = new PolynomialRegression(motorTrajectory, 1);
    }

    public static DriveSignal getDriveSignal(boolean loadingMode) {
        if (limelight.getArea() < 15.0) {
            double linearPower = getThrottle();

            if (limelight.getArea() < 4.5 && linearPower > 0) {
                linearPower = 0.5;
            } else if (isStopCrawl) {
                linearPower = 0.0;
            }
            if (limelight.getArea() >= 3.0) {
                turnController = closeTurnController;
                maxVisionAngularPower = kCloseMaxVisionAngularPower;
            }
            double angularPower = getTurnCorrection(loadingMode);
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
        turnController = farTurnController;
        maxVisionAngularPower = kFarMaxVisionAngularPower;
        farTurnController.reset();
        closeTurnController.reset();
        if (isTuning) {
            farTurnController.readDashboard();
            closeTurnController.readDashboard();
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
        visionThrottle = linearRegression.predict(limelight.getArea());
//        throttle =  Math.signum(throttle) * Util.clamp(Math.abs(throttle), 0.2, 0.4);
        if (SmartDashboard.getBoolean(visionThrottleKey, visionThrottleEnabled)) {
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
        visionTurnCorrection = Util.clamp(turnController.update(visionTurnError, 0.0),
                -maxVisionAngularPower, maxVisionAngularPower);

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
        SmartDashboard.putNumber("Vision linear regression", linearRegression.R2());
        SmartDashboard.putNumber("Vision throttle", visionThrottle);
        SmartDashboard.putNumber("Vision turn error", visionTurnError);
        SmartDashboard.putNumber("Vision turn correction", visionTurnCorrection);
        Vision.panelLimelight.outputToDashboard();
    }

}
