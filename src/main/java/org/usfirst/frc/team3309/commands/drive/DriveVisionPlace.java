package org.usfirst.frc.team3309.commands.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.VisionHelper;
import org.usfirst.frc.team3309.commands.PlacePanelKt;
import org.usfirst.frc.team3309.commands.RemoveFingerKt;
import org.usfirst.frc.team3309.lib.util.CheesyDriveHelper;
import org.usfirst.frc.team3309.lib.util.DriveSignal;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team3309.subsystems.PanelHolder;
import org.usfirst.frc.team4322.commandv2.Command;

public class DriveVisionPlace extends Command {
    private Command command;
    private Timer settleTimer = new Timer();
    private boolean isSettling = false;
    private boolean finished = false;

    public DriveVisionPlace() {
        require(Robot.drive);
        setInterruptBehavior(InterruptBehavior.Terminate);
    }

    enum AutoStates {
        nothing,
        loadingPanel,
        placingPanel,
        removingFinger
    }
    DriveVisionPlace.AutoStates autoState = DriveVisionPlace.AutoStates.loadingPanel;

    @Override
    protected void initialize() {
        super.initialize();
        VisionHelper.start();
    }

    @Override
    protected void execute() {
        double area = Robot.vision.getTargetArea();
        Robot.vision.load3D();

        // shorter delay to settle at levels 1 & 2
        if ((Robot.elevator.getCarriagePercentage() < 0.9 && settleTimer.get() > 0.2) ||
                settleTimer.get() > 0.5) {
            settleTimer.stop();
            settleTimer.reset();
            isSettling = false;
        }
        if (autoState == DriveVisionPlace.AutoStates.placingPanel &&
                (Math.abs(area) > 7.5 || Robot.vision.targetDistInches3D() < 2)) {
            // place panel on rocket after having extended
            RemoveFingerKt.RemoveFinger().start();
            VisionHelper.stopCrawl();
            autoState = DriveVisionPlace.AutoStates.removingFinger;
            finished = true;
        } else if (Util.within(area, 0.05, 7.0) && autoState == DriveVisionPlace.AutoStates.nothing) {
            // extend in preparation to go on the rocket
            autoState = DriveVisionPlace.AutoStates.placingPanel;

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

        DriveSignal signal = VisionHelper.getDriveSignal(false);
        Robot.drive.setLeftRight(ControlMode.PercentOutput, signal.getLeft(), signal.getRight());
    }

    @Override
    protected boolean isFinished() {
        return autoState == AutoStates.placingPanel && !PlacePanelKt.PlacePanel().isRunning();
    }
}
