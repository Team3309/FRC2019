package org.usfirst.frc.team3309.commands.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.VisionHelper;
import org.usfirst.frc.team3309.commands.PlacePanelKt;
import org.usfirst.frc.team3309.commands.RemoveFingerKt;
import org.usfirst.frc.team3309.lib.util.DriveSignal;
import org.usfirst.frc.team3309.lib.util.Util3309;
import org.usfirst.frc.team3309.subsystems.PanelHolder;
import org.usfirst.frc.team4322.commandv2.Command;

public class DriveVisionPlace extends Command {
    private Command removeFingerCommand;
    private Timer settleTimer = new Timer();
    private boolean isSettling = false;

    public DriveVisionPlace() {
        require(Robot.drive);
        setInterruptBehavior(InterruptBehavior.Terminate);
    }

    enum AutoStates {
        nothing,
        placingPanel,
        removingFinger
    }
    AutoStates autoState = AutoStates.nothing;

    @Override
    protected void initialize() {
        super.initialize();
        VisionHelper.start();
        removeFingerCommand = RemoveFingerKt.RemoveFinger();
    }

    @Override
    protected void execute() {
        double area = Robot.vision.getTargetArea();
        Robot.vision.load3D();
        DriveSignal signal;

        if (VisionHelper.hasTargets()) {
            // shorter delay to settle at levels 1 & 2
            if ((Robot.elevator.getCarriagePercentage() < 0.9 && settleTimer.get() > 0.2) ||
                    settleTimer.get() > 0.5) {
                settleTimer.stop();
                settleTimer.reset();
                isSettling = false;
            }
            if (autoState == AutoStates.placingPanel &&
                    (Math.abs(area) > 7.5 || Robot.vision.targetDistInches3D() < 2)) {
                // place panel on rocket after having extended
                removeFingerCommand.start();
                VisionHelper.stopCrawl();
                autoState = AutoStates.removingFinger;
            } else if (Util3309.within(area, 0.05, 7.0) && autoState == AutoStates.nothing) {
                // extend in preparation to go on the rocket
                autoState = AutoStates.placingPanel;

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
            signal = VisionHelper.getDriveSignal(false);
        } else {
            // Don't move if no target is detected
            signal = new DriveSignal(0,0);
        }

        Robot.drive.setLeftRight(ControlMode.PercentOutput, signal);
    }

    @Override
    protected boolean isFinished() {
        return autoState == AutoStates.removingFinger && !removeFingerCommand.isRunning();
    }
}
