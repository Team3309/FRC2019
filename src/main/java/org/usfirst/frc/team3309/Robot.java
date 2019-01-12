package org.usfirst.frc.team3309;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.subsystems.Drive;
import org.usfirst.frc.team4322.commandv2.Command;
import org.usfirst.frc.team4322.commandv2.Scheduler;

/*
 * This is the Robot class.
 * It handles the setup for the rest of the robot code.
 */

public class Robot extends TimedRobot {

    public static Drive drive;

    public static OI oi;

    private Command autoCommand;

    private double prev_velocity;
    private double prev_time;

    private double max_accel;

    /*
     * This function is called when the Robot program starts. use it to initialize your subsystems,
     * and to set up anything that needs to be initialized with the robot.
     */
    @Override
    public void robotInit() {

        drive = new Drive();

        oi = new OI();

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
        Scheduler.initialize();
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
        drive.outputToSmartdashboard();
    }

    /*
     * This function is called when the Robot enters teleop.
     * It should be used to shut down any autonomous code, and prepare the bot for human control.
     */
    @Override
    public void teleopInit() {
        if (autoCommand != null)
            autoCommand.cancel();
        drive.initDefaultCommand();
        prev_velocity = drive.getEncoderVelocity();
        prev_time = Timer.getFPGATimestamp();
    }


    /*
     * This function is called every 2 milliseconds while the robot is in autonomous.
     * It should be used to perform periodic tasks that need to be done while the robot is in teleop.
     */
    @Override
    public void teleopPeriodic() {

        double curTime = Timer.getFPGATimestamp();
        double delta_time = curTime - prev_time;

        double accel = Robot.drive.encoderVelocityToInchesPerSecond(
                Robot.drive.getEncoderVelocity() - prev_velocity) / delta_time;

        SmartDashboard.putNumber("Acceleration", accel);

        if (Math.abs(accel) > max_accel) {
            max_accel = Math.abs(accel);
            SmartDashboard.putNumber("Max accel", max_accel);
        }

        prev_velocity = Robot.drive.getEncoderVelocity();
        prev_time = Timer.getFPGATimestamp();

        drive.outputToSmartdashboard();
        Scheduler.update();
    }

    /*
     * This function is called when the Robot enters test mode.
     */
    @Override
    public void testInit() {
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

    }

    /*
     * This is the main function, which is where every java program starts.
     * All we do here is insert the code that is used to start up the rest of the robot code.
     */
    public static void main(String[] args) {
        RobotBase.startRobot(Robot::new);
    }

}
