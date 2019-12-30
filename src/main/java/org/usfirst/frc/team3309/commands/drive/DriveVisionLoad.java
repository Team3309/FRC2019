package org.usfirst.frc.team3309.commands.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.VisionHelper;
import org.usfirst.frc.team3309.commands.IntakePanelFromStationKt;
import org.usfirst.frc.team3309.commands.RetractFingerFromFeederStationKt;
import org.usfirst.frc.team4322.commandv2.Command;
import org.usfirst.frc.team3309.lib.util.DriveSignal;
import org.usfirst.frc.team3309.lib.util.Util;

public class DriveVisionLoad extends Command {
    private Command command;

    // State machine used for logic later in this command.
    enum AutoStates {
        nothing,
        loadingPanel
    }

    // State machine is set to "nothing" by default.
    AutoStates autoState = AutoStates.nothing;
    // Variable to check that the command has been started in the past.
    private boolean cmdStarted = false;

    // Constructor for DriveVisionLoad.
    public DriveVisionLoad() {
        require(Robot.drive);
        setInterruptBehavior(InterruptBehavior.Terminate);
        /* The IntakePanelFromStation command is the only one pertinent to DriveVisionLoad, so the
        instance variable always gets initialized to it. */
        command = IntakePanelFromStationKt.IntakePanelFromStation();
    }

    @Override
    protected void initialize() {
        super.initialize();
        // Allows vision code to be executed in "execute."
        VisionHelper.start();
    }


    @Override
    protected void execute() {
        // Initializes a drive signal to 0 for use later.
        DriveSignal signal = new DriveSignal(0, 0);

        // Checks for targets.
        if (VisionHelper.hasTargets()) {

            Robot.vision.load3D();
            double area = Robot.vision.getTargetArea();

            /* Thresholds for starting IntakePanelFromFeederStation. Also checks that the command
            is starting from nothing (not starting from another state) and ensures that the command
            was started and that the robot is indeed loading a panel. */
            if (Util.within(area, 0.1, 20.0) && autoState == AutoStates.nothing) {
                command.start();
                cmdStarted = true;
                autoState = AutoStates.loadingPanel;
            }

            //Commands robot to follow path made by VisionHelper
            if (autoState == AutoStates.loadingPanel) {
                signal = VisionHelper.getDriveSignal(true);
            }
        }

        //Passes left-right values from "signal" to subsystem "drive", enabling the robot to actually drive.
        double leftPower = signal.getLeft();
        double rightPower = signal.getRight();
        Robot.drive.setLeftRight(ControlMode.PercentOutput, leftPower, rightPower);
    }

    //Checks that the command has finished.
    @Override
    protected boolean isFinished() {

        if (!command.isRunning() && cmdStarted) {
            autoState = AutoStates.nothing;
            return true;
        } else {
            return false;
        }

    }

    //Ends the command.
    @Override
    protected void end() {

        //Checks that the robot *was* loading panel.
        if(autoState == AutoStates.loadingPanel) {
            command.cancel();
            RetractFingerFromFeederStationKt.RetractFingerFromFeederStation().start();
        }

        /* Sets the state machine to "nothing" so that every DriveVisionLoad
        command starts from nothing. */
        autoState = AutoStates.nothing;
    }
}