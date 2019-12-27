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
import edu.wpi.first.wpilibj.Timer;

public class DriveVisionLoad extends Command {

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

    DriveManual.AutoStates autoState = DriveManual.AutoStates.loadingPanel;

    public DriveVisionLoad() {
        require(Robot.drive);
        setInterruptBehavior(InterruptBehavior.Terminate);
    }

    @Override
    protected void execute() {
        double throttle = OI.getLeftJoystick().getYAxis().get();
        double turn = OI.getRightJoystick().getXAxis().get();

        boolean isHighGear = Robot.drive.inHighGear();
        boolean isQuickTurn = OI.getRightJoystick().getTrigger().get();
        boolean isAutoTurn = OI.getLeftJoystickLeftClusterGroup().get() && !Robot.isGuestDriver();


        VisionHelper.turnOn();
        if (VisionHelper.hasTargets()) {
            double area = Robot.vision.getTargetArea();
            Robot.vision.load3D();

            // extend to check for panel for auto grab
            command = IntakePanelFromStationKt.IntakePanelFromStation();
            command.start();
            autoState = DriveManual.AutoStates.loadingPanel;
        }
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
