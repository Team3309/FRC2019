package org.usfirst.frc.team3309.commands.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.DriverStation;
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
    private boolean extendedForPanel;
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
        boolean isAutoTurn = OI.getLeftJoystickLeftClusterGroup().get();
        boolean driverPipelineOverride = OI.getLeftJoystickRightClusterGroup().get();

        DriveSignal signal = cheesyDrive.update(throttle, turn, isQuickTurn, isHighGear);

        VisionHelper.outputToDashboard();

        VisionHelper.driverOverride(driverPipelineOverride);

        if (isAutoTurn) {
            VisionHelper.turnOn();
            if (VisionHelper.hasTargets()) {
                signal = VisionHelper.getDriveSignal();
                double area = Robot.vision.getTargetArea();
                if (Robot.panelHolder.hasPanel() && !loadingPanel) {
                    placingPanel = true;
                    PanelHolder.ExtendedPosition currentPosition = Robot.panelHolder.getExtendedPosition();

                    // extend in preparation to go on the rocket
                    if (Math.abs(area) > 7.5 &&
                            currentPosition == PanelHolder.ExtendedPosition.ExtendedOutwards) {
                        RemoveFingerKt.RemoveFinger().start();
                        DriverStation.reportError("Removed finger automatically", false);
                        VisionHelper.stopCrawl();
                        // place panel on rocket after having extended
                    } else if (Util.within(area, 0.05, 7.0) &&
                            currentPosition == PanelHolder.ExtendedPosition.RetractedInwards) {
                        PlacePanelKt.PlacePanel().start();
                        DriverStation.reportError("Extended to place panel automatically", false);
                    }
                } else if (Util.within(area, 0.1, 20.0) && !placingPanel) {
                    // extend to check for panel for autograb
                    loadingPanel = true;
                    DriverStation.reportError("Intaking panel from feeder station", false);
                    command = IntakePanelFromStationKt.IntakePanelFromStation();
                    command.start();
                    extendedForPanel = true;
                }
            }
        } else if (OI.getOperatorController().getRightStick().get()) {
            VisionHelper.turnOn();
        } else {
            VisionHelper.turnOff();
            placingPanel = false;
            loadingPanel = false;
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
