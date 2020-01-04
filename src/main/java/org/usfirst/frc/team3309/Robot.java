package org.usfirst.frc.team3309;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.commands.Elevate;
import org.usfirst.frc.team3309.commands.cargoholder.CargoHolderManual;
import org.usfirst.frc.team3309.commands.cargointake.CargoIntakeManual;
import org.usfirst.frc.team3309.commands.panelholder.PanelHolderManual;
import org.usfirst.frc.team3309.lib.util.Util3309;
import org.usfirst.frc.team3309.subsystems.*;
import org.usfirst.frc.team4322.commandv2.Command;
import org.usfirst.frc.team4322.commandv2.CommandV2Robot;
import org.usfirst.frc.team4322.commandv2.Scheduler;
import org.usfirst.frc.team4322.logging.RobotLogger;

import static org.usfirst.frc.team3309.VisionHelper.driverOverride;
import static org.usfirst.frc.team3309.VisionHelper.forceVisionOff;

/*
 * This is the Robot class.
 * It handles the setup for the rest of the robot code.
 */

public class Robot extends CommandV2Robot {

    // compile flag to activate demo mode
    private static final boolean demoMode = false;

    public static Drive drive;
    public static Elevator elevator;
    public static CargoIntake cargoIntake;
    public static PanelIntake panelIntake;
    public static CargoHolder cargoHolder;
    public static PanelHolder panelHolder;
    public static Climber climber;
    public static Vision vision;
    public static PowerDistributionPanel pdp;

    private Command autoCommand;

    private static final String driveDashboardKey = "Display Drive Values";
    private static final String elevatorDashboardKey = "Display Elevator Values";
    private static final String panelHolderDashboardKey = "Display Panel Holder Values";
    private static final String cargoIntakeDashboardKey = "Display Cargo Intake Values";
    private static final String cargoHolderDashboardKey = "Display Cargo Holder Values";
    private static final String climberDashboardKey = "Display Climber Values";
    private static final String visionDashboardKey = "Display Vision Values";

    /*
     * This function is called when the Robot program starts. use it to initialize your subsystems,
     * and to set up anything that needs to be initialized with the robot.
     */
    @Override
    public void robotInit() {
        super.robotInit();
        LiveWindow.disableAllTelemetry();
        drive = new Drive();
        elevator = new Elevator();
        cargoIntake = new CargoIntake();
        panelIntake = new PanelIntake();
        cargoHolder = new CargoHolder();
        panelHolder = new PanelHolder();
        climber = new Climber();
        vision = new Vision();
        pdp = new PowerDistributionPanel();

        UsbCamera cam0 = CameraServer.getInstance().startAutomaticCapture(0);
        cam0.setFPS(10);
        cam0.setResolution(320, 240);
        cam0.setPixelFormat(VideoMode.PixelFormat.kYUYV);

        UsbCamera cam1 = CameraServer.getInstance().startAutomaticCapture(1);
        cam1.setFPS(10);
        cam1.setResolution(320/2, 240/2);
        cam1.setPixelFormat(VideoMode.PixelFormat.kYUYV);
        AutoModeExecutor.displayAutos();

        RobotLogger.INSTANCE.setCurrentLogLevel(RobotLogger.LogLevel.ERR);

        SmartDashboard.putBoolean(driveDashboardKey, false);
        SmartDashboard.putBoolean(elevatorDashboardKey, false);
        SmartDashboard.putBoolean(panelHolderDashboardKey, false);
        SmartDashboard.putBoolean(cargoIntakeDashboardKey, false);
        SmartDashboard.putBoolean(cargoHolderDashboardKey, false);
        SmartDashboard.putBoolean(climberDashboardKey, false);
        SmartDashboard.putBoolean(visionDashboardKey, false);

        // invert turning joystick's left to right
        OI.getRightJoystick().getXAxis().setRampFunction((x) -> (-x));
        OI.getLeftJoystick().getYAxis().setRampFunction((x) -> (-x));

        elevator.zeroEncoder();
        VisionHelper.init();

        drive.initDefaultCommand();
    }

    /*
     * This function is called when the Robot enters disabled.
     * It should be used to shut down processes that should only run when the bot is sendIsEnabled.
     */
    @Override
    public void disabledInit() {
        super.disabledInit();
        Scheduler.killAllCommands();
        drive.reset();
        drive.setHighGear();
    }

    /*
     * This function is called when the Robot enters autonomous.
     * It should be used to get the current auto mode, and launch the appropriate autonomous mode.
     */
    @Override
    public void autonomousInit() {
        super.autonomousInit();
        Scheduler.killAllCommands();
        drive.reset();
        drive.setHighGear();
        elevator.zeroEncoder();
        autoCommand = AutoModeExecutor.getAutoSelected();
        if (autoCommand != null) {
            autoCommand.start();
        }
        new Elevate(Elevate.Level.Home).start();
    }

    /*
     * This function is called every 20 milliseconds while the robot is in autonomous.
     * It should be used to perform periodic tasks that need to be done while the robot is in autonomous.
     */
    @Override
    public void autonomousPeriodic() {
        super.autonomousPeriodic();
    }

    /*
     * This function is called when the Robot enters teleop.
     * It should be used to shut down any autonomous code, and prepare the bot for human control.
     */
    @Override
    public void teleopInit() {
        super.teleopInit();
        if (autoCommand != null)
            autoCommand.cancel();
        drive.setHighGear();
        drive.reset();
    }

    /*
     * This function is called every 2 milliseconds while the robot is in autonomous.
     * It should be used to perform periodic tasks that need to be done while the robot is in teleop.
     */
    @Override
    public void teleopPeriodic() {
        super.teleopPeriodic();
    }

    /*
     * This function is called when the Robot enters test mode.
     */
    @Override
    public void testInit() {
        super.testInit();
        drive.setHighGear();
        drive.reset();
    }

    @Override
    public void testPeriodic() {
        super.testPeriodic();
    }

    public static boolean getDriveDebug() {
        return SmartDashboard.getBoolean(driveDashboardKey, false);
    }

    /*
     * This function always runs, regardless of mode.
     */
    @Override
    public void robotPeriodic() {
        super.robotPeriodic();
        if (getDriveDebug()) {
            drive.outputToDashboard();
        }
        if (SmartDashboard.getBoolean(elevatorDashboardKey, false)) {
            elevator.outputToDashboard();
        }
        if (SmartDashboard.getBoolean(panelHolderDashboardKey, false)) {
            panelHolder.outputToDashboard();
        }
        if (SmartDashboard.getBoolean(cargoIntakeDashboardKey, false)) {
            cargoIntake.outputToDashboard();
        }
        if (SmartDashboard.getBoolean(cargoHolderDashboardKey, false)) {
            cargoHolder.outputToDashboard();
        }
        if (SmartDashboard.getBoolean(climberDashboardKey, false)) {
            climber.outputToDashboard();
        }
        if (SmartDashboard.getBoolean(visionDashboardKey, false)) {
            VisionHelper.outputToDashboard();
        }
    }

    public static boolean hasCargoInIntakeZone() {
        return cargoHolder.hasCargo()
                && Util3309.within(elevator.getCarriagePercentage(),
                Constants.CARGO_INTAKE_ZONE_MIN,
                Constants.CARGO_INTAKE_ZONE_MAX);
    }

    public static boolean isDemo() {
        return demoMode;
    }

    private static boolean guestDriverMode = false;

    public static boolean isGuestDriver() {
        return guestDriverMode;
    }

    public static void setGuestDriverMode() {
        if (!guestDriverMode) {
            DriverStation.reportError("Guest driver mode activated", false);
            forceVisionOff();
            guestDriverMode = true;
        }
    }

    /*
     * This is the main function, which is where every java program starts.
     * All we do here is insert the code that is used to start up the rest of the robot code.
     */
    public static void main(String[] args) {
        RobotBase.startRobot(Robot::new);
    }

}
