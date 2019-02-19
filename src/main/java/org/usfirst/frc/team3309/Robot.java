package org.usfirst.frc.team3309;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import org.usfirst.frc.team3309.commands.cargoholder.CargoHolderManual;
import org.usfirst.frc.team3309.commands.cargointake.CargoIntakeManual;
import org.usfirst.frc.team3309.commands.panelintake.PanelIntakeManual;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team3309.subsystems.*;
import org.usfirst.frc.team4322.commandv2.Command;
import org.usfirst.frc.team4322.commandv2.Scheduler;

/*
 * This is the Robot class.
 * It handles the setup for the rest of the robot code.
 */

public class Robot extends TimedRobot {

    public static Drive drive;
    public static Elevator elevator;
    public static CargoIntake cargoIntake;
    public static PanelIntake panelIntake;
    public static CargoHolder cargoHolder;
    public static PanelHolder panelHolder;
    public static Climber climber;
    public static Vision vision;

    private Command autoCommand;

    /*
     * This function is called when the Robot program starts. use it to initialize your subsystems,
     * and to set up anything that needs to be initialized with the robot.
     */
    @Override
    public void robotInit() {
        drive = new Drive();
        elevator = new Elevator();
        cargoIntake = new CargoIntake();
        panelIntake = new PanelIntake();
        cargoHolder = new CargoHolder();
        panelHolder = new PanelHolder();
        climber = new Climber();
        vision = new Vision();

        // TODO: needs to use limelight stream
        CameraServer.getInstance().startAutomaticCapture(0).setFPS(15);
        AutoModeExecutor.displayAutos();
        Scheduler.initialize();
    }

    /*
     * This function is called when the Robot enters disabled.
     * It should be used to shut down processes that should only run when the bot is enabled.
     */
    @Override
    public void disabledInit() {
        Scheduler.killAllCommands();
        drive.reset();
    }

    /*
     * This function is called when the Robot enters autonomous.
     * It should be used to get the current auto mode, and launch the appropriate autonomous mode.
     */
    @Override
    public void autonomousInit() {
        Scheduler.killAllCommands();
        drive.reset();
        autoCommand = AutoModeExecutor.getAutoSelected();
        if (autoCommand != null) {
            autoCommand.start();
        }
    }

    /*
     * This function is called every 2 milliseconds while the robot is in autonomous.
     * It should be used to perform periodic tasks that need to be done while the robot is in autonomous.
     */
    @Override
    public void autonomousPeriodic() {
        Scheduler.update();
    }

    /*
     * This function is called when the Robot enters teleop.
     * It should be used to shut down any autonomous code, and prepare the bot for human control.
     */
    @Override
    public void teleopInit() {
        if (autoCommand != null)
            autoCommand.cancel();
        drive.reset();
    }


    /*
     * This function is called every 2 milliseconds while the robot is in autonomous.
     * It should be used to perform periodic tasks that need to be done while the robot is in teleop.
     */
    @Override
    public void teleopPeriodic() {
        Scheduler.update();
    }

    /*
     * This function is called when the Robot enters test mode.
     */
    @Override
    public void testInit() {
        new CargoHolderManual().start();
        new CargoIntakeManual().start();
        new PanelIntakeManual().start();
    }


    /*
     * This function is called every 2 milliseconds while the Robot is in test mode.
     */
    @Override
    public void testPeriodic() {
    }

    /*
     * This function always runs, regardless of mode.
     */
    @Override
    public void robotPeriodic() {
        drive.outputToDashboard();
        elevator.outputToDashboard();
        panelHolder.outputToDashboard();
        panelIntake.outputToDashboard();
        cargoIntake.outputToDashboard();
        cargoIntake.outputToDashboard();
        climber.outputToDashboard();
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
