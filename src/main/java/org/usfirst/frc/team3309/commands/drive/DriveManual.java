package org.usfirst.frc.team3309.commands.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.VisionHelper;
import org.usfirst.frc.team3309.commands.IntakePanelFromStationKt;
import org.usfirst.frc.team3309.commands.PlacePanelKt;
import org.usfirst.frc.team3309.commands.RemoveFingerKt;
import org.usfirst.frc.team3309.commands.RetractFingerFromFeederStationKt;
import org.usfirst.frc.team3309.lib.util.CheesyDriveHelper;
import org.usfirst.frc.team3309.lib.util.DriveSignal;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team3309.subsystems.PanelHolder;
import org.usfirst.frc.team4322.commandv2.Command;

public class DriveManual extends Command {

    private CheesyDriveHelper cheesyDrive = new CheesyDriveHelper();

    private boolean placingPanel = false;
    private boolean loadingPanel = false;
    private Command command;

    public DriveManual() {
        require(Robot.drive);
        setInterruptBehavior(InterruptBehavior.Suspend);
    }

    @Override
    protected void execute() {

        double throttle = OI.getLeftJoystick().getYAxis().get();
        double turn = OI.getRightJoystick().getXAxis().get();

        boolean isHighGear = Robot.drive.inHighGear();
        boolean isQuickTurn = OI.getRightJoystick().getTrigger().get();
        boolean isAutoTurn = OI.getLeftJoystickLeftClusterGroup().get() && !Robot.isGuestDriver();

        DriveSignal signal = cheesyDrive.update(throttle, turn, isQuickTurn, isHighGear);

        if (Robot.isDemo() && OI.getRightJoystickLeftClusterGroup().get()) {
            Robot.setGuestDriverMode();
        }

        //boolean driverPipelineOverride = OI.getLeftJoystickRightClusterGroup().get();
        //VisionHelper.driverOverride(driverPipelineOverride);

        if (isAutoTurn) {
            VisionHelper.turnOn();
            if (VisionHelper.hasTargets()) {
                double area = Robot.vision.getTargetArea();
                if (Robot.panelHolder.hasPanel() && !loadingPanel) {
                    placingPanel = true;
                    PanelHolder.ExtendedPosition currentPosition = Robot.panelHolder.getExtendedPosition();

                    if (Math.abs(area) > 7.5 &&
                            currentPosition == PanelHolder.ExtendedPosition.ExtendedOutwards) {
                        // place panel on rocket after having extended
                        RemoveFingerKt.RemoveFinger().start();
                        VisionHelper.stopCrawl();
                    } else if (Util.within(area, 0.05, 7.0) &&
                            currentPosition == PanelHolder.ExtendedPosition.RetractedInwards) {
                        // extend in preparation to go on the rocket
                        PlacePanelKt.PlacePanel().start();
                    }
                } else if (Util.within(area, 0.1, 20.0) && !placingPanel) {
                    // extend to check for panel for autograb
                    loadingPanel = true;
                    command = IntakePanelFromStationKt.IntakePanelFromStation();
                    // don't restart the command to prevent cycling the pneumatic valve
                    if (!command.isRunning()) {
                        command.start();
                        // DriverStation.reportError("Intaking panel from feeder station", false);
                    }
                }
                signal = VisionHelper.getDriveSignal(loadingPanel);
            }
        } else if (OI.getOperatorController().getRightStick().get()) {
            VisionHelper.turnOn();
        } else {
            VisionHelper.turnOff();
            if (loadingPanel) {
                command.cancel();
                RetractFingerFromFeederStationKt.RetractFingerFromFeederStation().start();
                // DriverStation.reportError("Retraced finger from feeder station", false);
            }
            placingPanel = false;
            loadingPanel = false;
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
