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
        boolean isAutoTurn = OI.getLeftJoystickLeftClusterGroup().get();

        DriveSignal signal = cheesyDrive.update(throttle, turn, isQuickTurn, isHighGear);

        VisionHelper.outputToDashboard();

        if (isAutoTurn) {
            VisionHelper.turnOn();
            if (VisionHelper.hasTargets()) {
                signal = VisionHelper.getDriveSignal();
                if ((Robot.panelHolder.hasPanel()  || Robot.panelHolder.getCurrent() > 7.0)
                        && VisionHelper.getTimeElasped() > 0.25) {
                    double dist = VisionHelper.getDist();
                    PanelHolder.ExtendedPosition currentPosition = Robot.panelHolder.getExtendedPosition();
                    if (Math.abs(dist) < 2.0 &&
                            currentPosition == PanelHolder.ExtendedPosition.ExtendedOutwards) {
                        RemoveFingerKt.RemoveFinger().start();
                        DriverStation.reportError("Removed finger automatically", false);
                    } else if (Util.within(dist, 3.0, 15.0) &&
                            currentPosition == PanelHolder.ExtendedPosition.RetractedInwards) {
                        PlacePanelKt.PlacePanel().start();
                        DriverStation.reportError("Extended to place panel automatically", false);
                    }
                }
            }
        } else if (OI.getOperatorController().getRightStick().get()) {
            VisionHelper.turnOn();
        } else {
            VisionHelper.turnOff();
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
