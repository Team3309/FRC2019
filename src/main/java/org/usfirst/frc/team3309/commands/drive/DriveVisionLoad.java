package org.usfirst.frc.team3309.commands.drive;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.VisionHelper;
import org.usfirst.frc.team3309.commands.IntakePanelFromStationKt;
import org.usfirst.frc.team4322.commandv2.Command;

public class DriveVisionLoad extends Command {

    private Command command;

    public DriveVisionLoad() {
        super(1.0);
        require(Robot.drive);
        setInterruptBehavior(InterruptBehavior.Terminate);
    }

    @Override
    protected void execute() {
        //check for targets
        VisionHelper.turnOn();
        if (VisionHelper.hasTargets()) {
            Robot.vision.load3D();
            // extend to check for panel for auto grab
            command = IntakePanelFromStationKt.IntakePanelFromStation();
            command.start();
        }
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}