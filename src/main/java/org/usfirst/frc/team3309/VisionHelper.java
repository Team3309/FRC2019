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

    private static PIDController turnController = new PIDController("turn", 0.015, 0.000, 0.0);

    private static PolynomialRegression linearRegression;

    private static Timer timer = new Timer();

    private static final boolean isDashboard = false;
    private static Limelight.CamMode curCamMode = Limelight.CamMode.DriverCamera;
    private static int curPipeline = 0;
    private static Limelight.LEDMode curLed;
    private static boolean timerStarted;
    private static boolean isStopCrawl;

    static {
//        turnController.outputToDashboard();
        VisionHelper.turnOff();
        setCamMode(Limelight.CamMode.VisionProcessor);

        // points for line to vision target (target area, motor power)
        double[][] motorTrajectory = new double[][]{
                {0.5, 0.4},
                {9.71, 0.03},
        };

        linearRegression = new PolynomialRegression(motorTrajectory, 1);
    }

    public static DriveSignal getDriveSignal() {
        init();
        if (limelight.getArea() < 15.0) {
            double linearPower = getThrottleCorrection();

            if (limelight.getArea() < 0.05) {
                linearPower = 0.5;
            } else if (isStopCrawl) {
                linearPower = 0.0;
            }

            double angularPower = getTurnCorrection();
            SmartDashboard.putNumber("Throttle vision power", linearPower);
            SmartDashboard.putNumber("Turn vision power", angularPower);
            SmartDashboard.putNumber("linearRegression", linearRegression.R2());
            return new DriveSignal(linearPower + angularPower,
                    linearPower - angularPower);
        }
        return DriveSignal.NEUTRAL;
    }

    private static void init() {
        turnController.reset();
        if (isDashboard) {
            turnController.readDashboard();
        }
    }

    public static void turnOn() {
        init();
        setPipeline(0);
        setLed(Limelight.LEDMode.On);
        if (!timerStarted) {
            timer.start();
            timerStarted = true;
        }
    }

    public static void turnOff() {
        setLed(Limelight.LEDMode.Off);
        setPipeline(1);
        if (timerStarted) {
            timer.stop();
            timerStarted = false;
        }
        isStopCrawl = false;
    }

    public static double getThrottleCorrection() {
        double throttle = linearRegression.predict(limelight.getArea());
//        throttle =  Math.signum(throttle) * Util.clamp(Math.abs(throttle), 0.2, 0.4);
        return throttle;
    }

    public static double getTurnCorrection() {
        return turnController.update(limelight.getTx(), 0.0);
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
        return limelight.hasTarget() && Util.within(getSkew(), -5.0, 5.0);
    }

    public static void outputToDashboard() {
    }

}
