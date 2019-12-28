package org.usfirst.frc.team3309.commands.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.VisionHelper;
import org.usfirst.frc.team3309.commands.RetractFingerFromFeederStationKt;
import org.usfirst.frc.team3309.lib.util.CheesyDriveHelper;
import org.usfirst.frc.team3309.lib.util.DriveSignal;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team4322.commandv2.Command;
import edu.wpi.first.wpilibj.Timer;

public class DriveManual extends Command {

    private CheesyDriveHelper cheesyDrive = new CheesyDriveHelper();

    private Command command;
    private Timer settleTimer = new Timer();
    private boolean isSettling = false;

    enum AutoStates {
        nothing,
        loadingPanel,
        placingPanel,
        removingFinger
    }

    AutoStates autoState = AutoStates.nothing;

    public DriveManual() {
        require(Robot.drive);
        setInterruptBehavior(InterruptBehavior.Suspend);
    }

    @Override
    protected void execute() {

        //positive y is forward
        double throttle = OI.getLeftJoystick().getYAxis().get();
        //positive x is to the left
        double turn = -OI.getRightJoystick().getXAxis().get();

        boolean isHighGear = Robot.drive.inHighGear();
        boolean isQuickTurn = OI.getRightJoystick().getTrigger().get();
        boolean isAutoTurn = OI.getLeftJoystickLeftClusterGroup().get() && !Robot.isGuestDriver();

        DriveSignal signal = cheesyDrive.update(throttle, turn, isQuickTurn, isHighGear);

        DriveVisionLoad visionLoader = new DriveVisionLoad();
        DriveVisionPlace visionPlacer = new DriveVisionPlace();

        if (Robot.isDemo() && OI.getRightJoystickLeftClusterGroup().get()) {
            Robot.setGuestDriverMode();
        }

        //boolean driverPipelineOverride = OI.getLeftJoystickRightClusterGroup().get();
        //VisionHelper.driverOverride(driverPipelineOverride);

        if (isAutoTurn) {
            VisionHelper.turnOn();
            if (VisionHelper.hasTargets()) {
                double area = Robot.vision.getTargetArea();
                Robot.vision.load3D();

                if (Robot.panelHolder.hasPanel() && autoState != AutoStates.loadingPanel) {
                    visionPlacer.execute();
                } else if (Util.within(area, 0.1, 20.0) && autoState == AutoStates.nothing) {
                    visionLoader.execute();
                }
                if (autoState != AutoStates.nothing &&
                        !(autoState == AutoStates.placingPanel && isSettling)) {
                    signal = VisionHelper.getDriveSignal(autoState == AutoStates.loadingPanel);
                }
            }
        } else if (OI.getOperatorController().getRightStick().get()) {
            VisionHelper.turnOn();
        } else {
            VisionHelper.turnOff();
            if (autoState == AutoStates.loadingPanel) {
                command.cancel();
                RetractFingerFromFeederStationKt.RetractFingerFromFeederStation().start();
                // DriverStation.reportError("Retraced finger from feeder station", false);
            }
            autoState = AutoStates.nothing;
        }

        double leftPower = signal.getLeft();
        double rightPower = signal.getRight();

        if (Robot.isGuestDriver()) {
            leftPower *= 0.28;
            rightPower *= 0.28;
        }
        else if (Robot.isDemo() && !isAutoTurn) {
            leftPower *= 0.4;
            rightPower *= 0.4;
        }

        Robot.drive.setLeftRight(ControlMode.PercentOutput, leftPower, rightPower);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
