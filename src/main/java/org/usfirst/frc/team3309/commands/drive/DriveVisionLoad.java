package org.usfirst.frc.team3309.commands.drive;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.VisionHelper;
import org.usfirst.frc.team3309.commands.IntakePanelFromStationKt;
import org.usfirst.frc.team4322.commandv2.Command;
import org.usfirst.frc.team3309.lib.util.DriveSignal;

public class DriveVisionLoad extends Command {
    private Command command;

    /*Tasks for DriveVisionLoad:
      1) Drive the robot as was done when the code was in DriveManual. Currently it won’t move.
      2) Don’t drive if there are no targets. #FINISHED#
      3) Only invoke IntakePanelFromStation() once after targets have been detected. #FINISHED#
      4) Detect when the command is complete. The easiest way is to go back to saving a reference
         to the invoked IntakePanelFromStation() command and then check if it has completed using the
         isrunning() method.
    */

    public enum DriveVisionLoadSM {
        nothing,
        loadingPanel,
        placingPanel
    }

    DriveVisionLoadSM state = new DriveVisionLoadSM();
    public DriveVisionLoad() {
        super(1.0);
        require(Robot.drive);
        setInterruptBehavior(InterruptBehavior.Terminate);
    }

    @Override
    protected void initialize() {
        super.initialize();
        VisionHelper.start();
    }


    @Override
    protected void execute() {
        Robot.vision.load3D();
        if (VisionHelper.hasTargets()) {
            IntakePanelFromStationKt.IntakePanelFromStation().start();
        } else {
            state = DriveVisionLoadSM.nothing;
            IntakePanelFromStationKt.IntakePanelFromStation().cancel();
            Robot.drive.setLeftRight(ControlMode.PercentOutput, 0, 0);
            DriverStation.reportError("No targets detected. Awaiting manual input.", false);
        }
    }

    /*
    * Task: Make sure that DriveVisionLoad has a state machine so that it will only start one command at a
    * time.
    * */
    @Override
    protected boolean isFinished() {}
}