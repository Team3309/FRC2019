package org.usfirst.frc.team3309;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.cscore.VideoMode;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import jaci.pathfinder.Trajectory;
import org.usfirst.frc.team3309.commands.RocketAndCargoShipKt;
import org.usfirst.frc.team3309.commands.elevator.Elevate;
import org.usfirst.frc.team3309.commands.cargoholder.CargoHolderManual;
import org.usfirst.frc.team3309.commands.cargointake.CargoIntakeManual;
import org.usfirst.frc.team3309.commands.drive.auto.MoveCommand;
import org.usfirst.frc.team3309.commands.panelholder.PanelHolderManual;
import org.usfirst.frc.team3309.lib.Ramsete;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team3309.subsystems.*;
import org.usfirst.frc.team4322.commandv2.Command;
import org.usfirst.frc.team4322.commandv2.CommandV2Robot;
import org.usfirst.frc.team4322.commandv2.Scheduler;
import org.usfirst.frc.team4322.logging.RobotLogger;

import java.util.LinkedHashMap;

/*
 * This is the Robot class.
 * It handles the setup for the rest of the robot code.
 */

public class Robot extends CommandV2Robot {

    public enum Side {
        BALL, PANEL,
    }

    public static Side activeSide;

    public static Drive drive;
    public static Elevator elevator;
    public static CargoIntake cargoIntake;
    public static PanelIntake panelIntake;
    public static CargoHolder cargoHolder;
    public static PanelHolder panelHolder;
    public static Climber climber;
    public static Vision vision;

    public static PowerDistributionPanel pdp;

    //Map of all autonomous paths
    private Command autoCommand;
    public static LinkedHashMap<PathLoader.Path, Trajectory> autonPaths;


    /*
     * This function is called when the Robot program starts. use it to initialize your subsystems,
     * and to set up anything that needs to be initialized with the robot.
     */
    @Override
    public void robotInit() {
        super.robotInit();

        drive = new Drive();
        elevator = new Elevator();
        cargoIntake = new CargoIntake();
        panelIntake = new PanelIntake();
        cargoHolder = new CargoHolder();
        panelHolder = new PanelHolder();
        climber = new Climber();
        vision = new Vision();

        pdp = new PowerDistributionPanel();

        VisionHelper.turnOff();

        UsbCamera camera = CameraServer.getInstance().startAutomaticCapture(0);
        camera.setFPS(10);
        camera.setResolution(320 / 2, 240 / 2);
        camera.setPixelFormat(VideoMode.PixelFormat.kYUYV);

        AutoModeExecutor.displayAutos();

        RobotLogger.INSTANCE.setCurrentLogLevel(RobotLogger.LogLevel.INFO);

        // invert turning joystick's left to right
        OI.getRightJoystick().getXAxis().setRampFunction((x) -> (-x));
        OI.getLeftJoystick().getYAxis().setRampFunction((x) -> (-x));

        autonPaths = PathLoader.loadPaths();

        elevator.zeroEncoder();

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
        Ramsete.getInstance().stop();
    }

    /*
     * This function is called when the Robot enters autonomous.
     * It should be used to get the current auto mode, and launch the appropriate autonomous mode.
     */
    @Override
    public void autonomousInit() {
        super.autonomousInit();
        Scheduler.killAllCommands();
        drive.setHighGear();
        drive.zeroSensor();
        elevator.zeroEncoder();

        autoCommand = RocketAndCargoShipKt.RocketAndCargoShip();
        autoCommand.start();
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
        new CargoIntakeManual().start();
        new CargoHolderManual().start();
        new PanelHolderManual().start();
        Ramsete.getInstance().stop();
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
    }

    /*
     * This function always runs, regardless of mode.
     */
    @Override
    public void robotPeriodic() {
        super.robotPeriodic();
        boolean outputSubsystemsToDashboard = SmartDashboard.getBoolean("outputSubsystemsToDashboard",
                false);
        if (outputSubsystemsToDashboard) {
            drive.outputToDashboard();
            elevator.outputToDashboard();
            panelHolder.outputToDashboard();
            panelIntake.outputToDashboard();
            cargoIntake.outputToDashboard();
            cargoHolder.outputToDashboard();
            climber.outputToDashboard();
        }

        if (Robot.panelHolder.hasPanel()) {
            activeSide = Side.PANEL;
        } else if (Robot.cargoHolder.hasCargo()) {
            activeSide = Side.BALL;
        }
    }

    public static boolean hasCargoInIntakeZone() {
        return cargoHolder.hasCargo()
                && Util.within(elevator.getCarriagePercentage(),
                Constants.CARGO_INTAKE_ZONE_MIN,
                Constants.CARGO_INTAKE_ZONE_MAX);
    }

    /*
     * This is the main function, which is where every java program starts.
     * All we do here is insert the code that is used to start up the rest of the robot code.
     */
    public static void main(String[] args) {
        RobotBase.startRobot(Robot::new);
    }

}
