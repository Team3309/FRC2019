package org.usfirst.frc.team3309.commands.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.VisionHelper;
import org.usfirst.frc.team3309.commands.IntakePanelFromStationKt;
import org.usfirst.frc.team3309.commands.PlacePanelKt;
import org.usfirst.frc.team3309.commands.RemoveFingerKt;
import org.usfirst.frc.team3309.commands.RetractFingerFromFeederStationKt;
import org.usfirst.frc.team3309.commands.panelholder.PanelHolderSetRollers;
import org.usfirst.frc.team3309.lib.PIDController;
import org.usfirst.frc.team3309.lib.util.CheesyDriveHelper;
import org.usfirst.frc.team3309.lib.util.DriveSignal;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team3309.subsystems.PanelHolder;
import org.usfirst.frc.team3309.subsystems.Vision;
import org.usfirst.frc.team4322.commandv2.Command;

import java.awt.*;

public class DriveManual extends Command {

    private CheesyDriveHelper cheesyDrive = new CheesyDriveHelper();

    private boolean hadPanel;
    private boolean extendedForPanel;
    private Command command;

    public DriveManual() {
        require(Robot.drive);
        setInterruptBehavior(InterruptBehavior.Suspend);
        SmartDashboard.putNumber("true distance", 24.0);
    }

    @Override
    protected void execute() {

        double throttle = OI.getLeftJoystick().getYAxis().get();
        double turn = OI.getRightJoystick().getXAxis().get();

        boolean isHighGear = Robot.drive.inHighGear();
        boolean isQuickTurn = OI.getRightJoystick().getTrigger().get();
        boolean isAutoTurn = OI.getLeftJoystickLeftClusterGroup().get();

        DriveSignal signal = cheesyDrive.update(throttle, turn, isQuickTurn, isHighGear);

        VisionHelper.outputToDashboard();

        if (isAutoTurn) {
            VisionHelper.turnOn();
            if (VisionHelper.hasTargets()) {
                signal = VisionHelper.getDriveSignal();
                double dist = VisionHelper.getDist();
                if ((Robot.panelHolder.hasPanel() || Robot.panelHolder.getCurrent() > 2.5)
                        && VisionHelper.getTimeElasped() > 0.25) {
                    hadPanel = true;
                    PanelHolder.ExtendedPosition currentPosition = Robot.panelHolder.getExtendedPosition();

                    // extend in preparation to go on the rocket
                    if (Math.abs(dist) < 4 &&
                            currentPosition == PanelHolder.ExtendedPosition.ExtendedOutwards) {
                        RemoveFingerKt.RemoveFinger().start();
                        DriverStation.reportError("Removed finger automatically", false);
                        // place panel on rocket after having extended
                    } else if (Util.within(dist, 4, 25.0) &&
                            currentPosition == PanelHolder.ExtendedPosition.RetractedInwards) {
                        PlacePanelKt.PlacePanel().start();
                        DriverStation.reportError("Extended to place panel automatically", false);
                    }
                } else {
                    // extend to check for panel for autograb
                    if (Util.within(dist, 0.0, 40.0) && !hadPanel) {
                        DriverStation.reportError("Intaking panel from feeder station", false);
                        command = IntakePanelFromStationKt.IntakePanelFromStation();
                        command.start();
                        extendedForPanel = true;
                    }
                }
            }
        } else if (OI.getOperatorController().getRightStick().get()) {
            VisionHelper.turnOn();
            double trueDistance = SmartDashboard.getNumber("true distance", 24.0);
            SmartDashboard.putNumber("Mounting angle", VisionHelper.getCameraMountingAngle(trueDistance));
        } else {
            VisionHelper.turnOff();
            hadPanel = false;
            if (extendedForPanel) {
                command.cancel();
                RetractFingerFromFeederStationKt.RetractFingerFromFeederStation().start();
                DriverStation.reportError("Retraced finger from feeder station", false);
                extendedForPanel = false;
            }
        }

        double leftPower = signal.getLeft();
        double rightPower = signal.getRight();

        Robot.drive.setLeftRight(ControlMode.PercentOutput, leftPower, rightPower);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }


}
