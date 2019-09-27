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

    private static PIDController turnController = new PIDController("turn", 0.05, 0.000, 0.0);

    private static PolynomialRegression linearRegression;

    private static Timer timer = new Timer();

    private static final boolean isDashboard = true;
    private static Limelight.CamMode curCamMode = Limelight.CamMode.DriverCamera;
    private static int curPipeline = 0;
    private static Limelight.LEDMode curLed;
    private static boolean isStopCrawl;
    private static boolean visionOn = false;

    static {
        if (isDashboard) {
            // tuning/debug mode
            turnController.outputToDashboard();
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
        turnController.reset();
        if (isDashboard) {
            turnController.readDashboard();
        }
        setPipeline(0);
        setLed(Limelight.LEDMode.On);
        timer.reset();
        timer.start();
    }

    private static void disableVision(){
        visionOn = false;
        timer.stop();
        isStopCrawl = false;
        if (!isDashboard) {
            setLed(Limelight.LEDMode.Off);
            setPipeline(1);
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
        if (limelight.has3D() && Robot.panelHolder.hasPanel()) {
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
