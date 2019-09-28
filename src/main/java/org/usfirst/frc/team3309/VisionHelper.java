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

    private static final boolean isDashboard = false;
    private static final boolean forceVisionOn = true;
    private static boolean loadStation3D = false;

    private static final double farTurnP = 0.028;
    private static final double farTurnI = 0.0;
    private static final double farTurnD = 0.0;
    private static final double closeTurnP = 0.05;
    private static final double closeTurnI = 0.0;
    private static final double closeTurnD = 0.0;

    // Use smaller P when far away to avoid overshoot from potentially large initial correction.
    // Use larger P when closer to provide enough angular power for fine corrections.
    private static PIDController farTurnController = new PIDController(farTurnP, farTurnI, farTurnD);
    private static PIDController closeTurnController = new PIDController(closeTurnP, closeTurnI, closeTurnD);

    private static PolynomialRegression linearRegression;
    private static boolean driverOverrideActive = false;

    private static Timer timer = new Timer();

    private static PIDController turnController = farTurnController;
    private static Limelight.CamMode curCamMode = Limelight.CamMode.DriverCamera;
    private static int curPipeline = -1;
    private static Limelight.LEDMode curLed;
    private static boolean isStopCrawl;
    private static boolean visionOn = false;

    static {
        if (isDashboard) {
            // tuning mode
            farTurnController.outputToDashboard();
            closeTurnController.outputToDashboard();
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

    public static DriveSignal getDriveSignal() {
        if (limelight.getArea() < 15.0) {
            double linearPower = getThrottleCorrection();

            if (limelight.getArea() < 0.05) {
                linearPower = 0.5;
            } else if (isStopCrawl) {
                linearPower = 0.0;
            }
            if (limelight.getArea() >= 0.03) {
                turnController = closeTurnController;
            }
            double angularPower = getTurnCorrection();
//            SmartDashboard.putNumber("Throttle vision power", linearPower);
//            SmartDashboard.putNumber("Turn vision power", angularPower);
//            SmartDashboard.putNumber("linearRegression", linearRegression.R2());
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
        farTurnController.reset();
        closeTurnController.reset();
        if (isDashboard) {
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

    public static double getThrottleCorrection() {
        double throttle = linearRegression.predict(limelight.getArea());
//        throttle =  Math.signum(throttle) * Util.clamp(Math.abs(throttle), 0.2, 0.4);
        return throttle;
    }

    public static double getTurnCorrection() {
        double turnError;

        // don't use 3D vision when picking up at loading station
        // because panel blocks bottom of vision tape
        if (limelight.has3D() && (Robot.panelHolder.hasPanel() || loadStation3D)) {
            turnError = limelight.rotationCenterDegrees3D();
        }
        else {
            turnError = limelight.getTx();
        }

        return turnController.update(turnError, 0.0);
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
    }

}
