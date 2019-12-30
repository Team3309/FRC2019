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

    enum AutoStates {
        nothing,
        loadingPanel
    }

    AutoStates autoState = AutoStates.nothing;
    private boolean cmdStarted = false;


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
        DriveSignal signal = new DriveSignal(0, 0);


        if (VisionHelper.hasTargets()) {
            Robot.vision.load3D();
            double area = Robot.vision.getTargetArea();

            if (Util.within(area, 0.1, 20.0) && autoState == AutoStates.nothing) {
                command.start();
                cmdStarted = true;
                autoState = AutoStates.loadingPanel;
            }

            if (autoState == AutoStates.loadingPanel) {
                signal = VisionHelper.getDriveSignal(true);
            }
        }

        double leftPower = signal.getLeft();
        double rightPower = signal.getRight();
        Robot.drive.setLeftRight(ControlMode.PercentOutput, leftPower, rightPower);
    }



    @Override
    protected boolean isFinished() {
        return !command.isRunning() && cmdStarted;
    }

    @Override
    protected void interrupted() {

        if(autoState == AutoStates.loadingPanel) {
            command.cancel();
            RetractFingerFromFeederStationKt.RetractFingerFromFeederStation().start();
        }
        autoState = AutoStates.nothing;
    }
}