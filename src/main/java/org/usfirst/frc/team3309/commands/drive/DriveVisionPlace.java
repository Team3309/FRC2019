package org.usfirst.frc.team3309.commands.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.VisionHelper;
import org.usfirst.frc.team3309.commands.IntakePanelFromStationKt;
import org.usfirst.frc.team3309.commands.PlacePanelKt;
import org.usfirst.frc.team3309.commands.RemoveFingerKt;
import org.usfirst.frc.team3309.commands.RetractFingerFromFeederStationKt;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team3309.subsystems.PanelHolder;
import org.usfirst.frc.team4322.commandv2.Command;

public class DriveVisionPlace extends Command {
    public DriveVisionPlace() {
        require(Robot.drive);
        setInterruptBehavior(InterruptBehavior.Terminate);
    }

    @Override
    protected void execute() {
        VisionHelper.turnOn();
        if (VisionHelper.hasTargets()) {
            double area = Robot.vision.getTargetArea();
            Robot.vision.load3D();

            //if (Robot.panelHolder.hasPanel() && autoState != DriveManual.AutoStates.loadingPanel) {
                // shorter delay to settle at levels 1 & 2
                if ((Robot.elevator.getCarriagePercentage() < 0.9 && settleTimer.get() > 0.2) ||
                        settleTimer.get() > 0.5) {
                    settleTimer.stop();
                    settleTimer.reset();
                    isSettling = false;
                }
                if (autoState == DriveManual.AutoStates.placingPanel &&
                        (Math.abs(area) > 7.5 || Robot.vision.targetDistInches3D() < 2)) {
                    // place panel on rocket after having extended
                    RemoveFingerKt.RemoveFinger().start();
                    VisionHelper.stopCrawl();
                    autoState = DriveManual.AutoStates.removingFinger;
                } else if (Util.within(area, 0.05, 7.0) && autoState == DriveManual.AutoStates.nothing) {
                    // extend in preparation to go on the rocket
                    autoState = DriveManual.AutoStates.placingPanel;
                    // If engaging from a stop and finger is retracted, allow panel holder to settle
                    // from finger extension before starting to drive. Don't wait if already moving
                    // to avoid extra jerking from stopping and restarting drive.
                    if (Robot.panelHolder.getExtendedPosition() !=
                            PanelHolder.ExtendedPosition.ExtendedOutwards &&
                            Robot.drive.getLeftEncoderVelocity() < 500) {
                        settleTimer.start();
                        isSettling = true;
                    } else {
                        settleTimer.stop();
                        settleTimer.reset();
                        isSettling = false;
                    }
                    PlacePanelKt.PlacePanel().start();
                }
            } else if (Util.within(area, 0.1, 20.0) && autoState == DriveManual.AutoStates.nothing) {
                // extend to check for panel for auto grab
                command = IntakePanelFromStationKt.IntakePanelFromStation();
                command.start();
                autoState = DriveManual.AutoStates.loadingPanel;
            }
            if (autoState != DriveManual.AutoStates.nothing &&
                    !(autoState == DriveManual.AutoStates.placingPanel && isSettling)) {
                signal = VisionHelper.getDriveSignal(autoState == DriveManual.AutoStates.loadingPanel);
            }
        }
    } else if (OI.getOperatorController().getRightStick().get()) {
        VisionHelper.turnOn();
    } else {
        VisionHelper.turnOff();
        if (autoState == DriveManual.AutoStates.loadingPanel) {
            command.cancel();
            RetractFingerFromFeederStationKt.RetractFingerFromFeederStation().start();
            // DriverStation.reportError("Retraced finger from feeder station", false);
        }
        autoState = DriveManual.AutoStates.nothing;
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
