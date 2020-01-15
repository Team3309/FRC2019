package org.usfirst.frc.team3309.commands.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.DriverStation;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.CheesyDriveHelper;
import org.usfirst.frc.team3309.lib.util.DriveSignal;
import org.usfirst.frc.team4322.commandv2.Command;

public class DriveManual extends Command {

    private CheesyDriveHelper cheesyDrive = new CheesyDriveHelper();

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

        DriveSignal signal = cheesyDrive.update(throttle, turn, isQuickTurn, isHighGear);

        if (Robot.isDemo() && OI.getRightJoystickLeftClusterGroup().get()) {
            Robot.setGuestDriverMode();
        }

        //boolean driverPipelineOverride = OI.getLeftJoystickRightClusterGroup().get();
        //VisionHelper.driverOverride(driverPipelineOverride);

        double leftPower = signal.getLeft();
        double rightPower = signal.getRight();

        if (Robot.isGuestDriver()) {
            leftPower *= 0.28;
            rightPower *= 0.28;
        }
        else if (Robot.isDemo()) {
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
