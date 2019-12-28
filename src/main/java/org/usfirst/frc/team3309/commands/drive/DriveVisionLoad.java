package org.usfirst.frc.team3309.commands.drive;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.VisionHelper;
import org.usfirst.frc.team3309.commands.IntakePanelFromStationKt;
import org.usfirst.frc.team4322.commandv2.Command;

public class DriveVisionLoad extends Command {
    private Command command;

    public enum DVLStateMachine {
        nothing,
        loadingPanel,
        placingPanel
    }
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

    /*
    *Pseudocode:
    *
    * 1) Check for targets
    *    >If targets present, execute the code
    *    >If not, have the robot stand still and transfer control to the driver.
    */
    @Override
    protected void execute() {
        Robot.vision.load3D();
        if (VisionHelper.hasTargets()) {
            IntakePanelFromStationKt.IntakePanelFromStation().start();
        } else {

        }
    }

    /*
    * Task: Make sure that DriveVisionLoad has a state machine so that it will only start one command at a
    * time.
    * */
    @Override
    protected boolean isFinished() {
        return false;
    }
}