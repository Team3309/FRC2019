package org.usfirst.frc.team3309.commands.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.VisionHelper;
import org.usfirst.frc.team3309.lib.PIDController;
import org.usfirst.frc.team3309.lib.util.CheesyDriveHelper;
import org.usfirst.frc.team3309.lib.util.DriveSignal;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team3309.subsystems.Vision;
import org.usfirst.frc.team4322.commandv2.Command;

public class DriveManual extends Command {

    private CheesyDriveHelper cheesyDrive = new CheesyDriveHelper();

    public DriveManual() {
        require(Robot.drive);
        setInterruptBehavior(InterruptBehavior.Suspend);
    }

    @Override
    protected void execute() {

        double throttle = OI.INSTANCE.getLeftJoystick().getYAxis().get();
        double turn = OI.INSTANCE.getRightJoystick().getXAxis().get();

//           SmartDashboard.putNumber("Throttle", throttle);
//        SmartDashboard.putNumber("Turn", turn);

        boolean isHighGear = Robot.drive.inHighGear();
        boolean isQuickTurn = OI.INSTANCE.getRightJoystick().getTrigger().get();
        boolean isAutoTurn = OI.INSTANCE.getLeftJoystick().getKnobCluster().getBottom().get();

        DriveSignal signal = cheesyDrive.update(throttle, turn, isQuickTurn, isHighGear);

        double leftPower = signal.getLeft();
        double rightPower = signal.getRight();

        VisionHelper.outputToDashboard();
        if (isAutoTurn) {
            VisionHelper.turnOn();
            double angularPower = -VisionHelper.getTurnCorrection();
            leftPower = throttle + angularPower;
            rightPower = throttle - angularPower;
        } else {
            VisionHelper.turnOff();
        }

        Robot.drive.setLeftRight(ControlMode.PercentOutput, leftPower, rightPower);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }


}
