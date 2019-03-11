package org.usfirst.frc.team3309;

import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.RobotBase;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.commands.ClimberManual;
import org.usfirst.frc.team3309.commands.Elevate;
import org.usfirst.frc.team3309.commands.cargoholder.CargoHolderManual;
import org.usfirst.frc.team3309.commands.cargointake.CargoIntakeManual;
import org.usfirst.frc.team3309.commands.drive.DriveManual;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team3309.subsystems.*;
import org.usfirst.frc.team4322.commandv2.Command;
import org.usfirst.frc.team4322.commandv2.CommandV2Robot;
import org.usfirst.frc.team4322.commandv2.Scheduler;
import org.usfirst.frc.team4322.logging.RobotLogger;

/*
 * This is the Robot class.
 * It handles the setup for the rest of the robot code.
 */

public class Robot extends CommandV2Robot {

    public static Drive drive;
    public static Elevator elevator;
    public static CargoIntake cargoIntake;
    public static PanelIntake panelIntake;
    public static CargoHolder cargoHolder;
    public static PanelHolder panelHolder;
    public static Climber climber;
    public static Vision vision;
    private static LightComm lightComm;

    private Command autoCommand;

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
        lightComm = new LightComm();

        UsbCamera camera = CameraServer.getInstance().startAutomaticCapture(0);
        camera.setFPS(15);
        camera.setResolution(320, 240);

        AutoModeExecutor.displayAutos();

        RobotLogger.INSTANCE.setCurrentLogLevel(RobotLogger.LogLevel.INFO);

        // TODO: flip every joystick?
        // invert turning joystick's left to right
        OI.getRightJoystick().getXAxis().setRampFunction((x) -> (-x));
        OI.getLeftJoystick().getYAxis().setRampFunction((x) -> (-x));

        // TODO: temporary until limit switch
        elevator.zeroEncoder();

        drive.initDefaultCommand();

        SmartDashboard.putBoolean("outputSubsystemsToDashboard", false);
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
        // TODO: switch to default commands
        new CargoIntakeManual().start();
        new CargoHolderManual().start();
//        new ClimberManual().start();
        //        new ElevatorManual().start();
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
        new ClimberManual().start();
        if (!DriverStation.getInstance().isFMSAttached()) {
            elevator.zeroEncoder();
//            new ClimberManual().start();
        }
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
     * This function is called every 2 milliseconds while the Robot is in test mode.
     */
    @Override
    public void testPeriodic() {
        super.testPeriodic();

        NetworkTable testTable = NetworkTableInstance.getDefault().getTable("test");
        NetworkTable pneumaticsTable = testTable.getSubTable("pneumatics");

        boolean cargoIntakeExtend = pneumaticsTable.getEntry("CargoIntake extend").getBoolean(false);
        boolean panelIntakeExtend = pneumaticsTable.getEntry("PanelIntake extend").getBoolean(false);
        boolean panelHolderJointedExtended = pneumaticsTable
                .getEntry("PanelHolder jointed extended").getBoolean(false);
        boolean panelHolderTelescopingExtended = pneumaticsTable
                .getEntry("PanelHolder telescoping extended").getBoolean(false);
        boolean climberReleased = pneumaticsTable.getEntry("Climber released").getBoolean(false);

//        cargoIntake.setPosition(CargoIntake.CargoIntakePosition.fromBoolean(cargoIntakeExtend));
        panelIntake.setPosition(PanelIntake.PanelIntakePosition.fromBoolean(panelIntakeExtend));
//        panelHolder.setPosition(PanelHolder.JointedPosition.fromBoolean(panelHolderJointedExtended),
//                PanelHolder.ExtendedPosition.fromBoolean(panelHolderTelescopingExtended));
        climber.setPosition(Climber.ClimberLatchPosition.fromBoolean(climberReleased));

        // TODO: remove require(cargoIntake) in actuate
        if (OI.getOperatorController().getA().get()) {
            cargoIntake.setPosition(CargoIntake.CargoIntakePosition.Extended);
        } else if (OI.getOperatorController().getB().get()) {
            cargoIntake.setPosition(CargoIntake.CargoIntakePosition.Stowed);
        }

        if (OI.getOperatorController().getX().get()) {
            panelHolder.setJointedSolenoid(PanelHolder.JointedPosition.Vertical);
        } else if (OI.getOperatorController().getY().get()) {
            panelHolder.setJointedSolenoid(PanelHolder.JointedPosition.PointingOutwards);
        }

        if (OI.getOperatorController().getDPad().getDown().get()) {
            panelHolder.setExtendingSolenoid(PanelHolder.ExtendedPosition.RetractedInwards);
        } else if (OI.getOperatorController().getDPad().getUp().get()) {
            panelHolder.setExtendingSolenoid(PanelHolder.ExtendedPosition.ExtendedOutwards);
        }

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
        elevator.outputToDashboard();
//        panelHolder.outputToDashboard();
//        cargoHolder.outputToDashboard();
//        drive.outputToDashboard();
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
