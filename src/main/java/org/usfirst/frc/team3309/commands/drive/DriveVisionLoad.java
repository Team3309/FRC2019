package org.usfirst.frc.team3309.commands.drive;

import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.VisionHelper;
import org.usfirst.frc.team3309.commands.IntakePanelFromStationKt;
import org.usfirst.frc.team4322.commandv2.Command;
import org.usfirst.frc.team3309.lib.util.DriveSignal;
import edu.wpi.first.wpilibj.command.Command;

public class DriveVisionLoad extends Command {
    private Command command;


    public DriveVisionLoad() {
        require(Robot.drive);
        setInterruptBehavior(InterruptBehavior.Terminate);
        command = IntakePanelFromStationKt.IntakePanelFromStation();
    }

    @Override
    protected void initialize() {
        super.initialize();
        VisionHelper.start();
    }


    @Override
    protected void execute() {
        Robot.vision.load3D();

        if (Robot.isDemo() && OI.getRightJoystickLeftClusterGroup().get()) {
            Robot.setGuestDriverMode();
        }

        if (isAutoTurn) {
            VisionHelper.turnOn();
                if (VisionHelper.hasTargets()) {
                    Robot.vision.load3D();
                    command.start();
                } else {
                    Robot.drive.setLeftRight(ControlMode.PercentOutput, 0, 0);
                    DriverStation.reportError("No targets detected. Awaiting manual input.", false);
                }
        }
    }

    @Override
    protected boolean isFinished() {
        return !command.isRunning();
    }
}