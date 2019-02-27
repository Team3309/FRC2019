package org.usfirst.frc.team3309.commands.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.PIDController;
import org.usfirst.frc.team3309.lib.util.CheesyDriveHelper;
import org.usfirst.frc.team3309.lib.util.DriveSignal;
import org.usfirst.frc.team3309.subsystems.Vision;
import org.usfirst.frc.team4322.commandv2.Command;

public class DriveManual extends Command {

    private PIDController turnController = new PIDController("turn", 0.0, 0.0, 0.0);

    private CheesyDriveHelper cheesyDrive = new CheesyDriveHelper();

    public DriveManual() {
        require(Robot.drive);
        setInterruptBehavior(InterruptBehavior.Suspend);
    }

    @Override
    protected void execute() {

//        double throttle = OI.INSTANCE.getOperatorController().getLeftStick().y();
//        double turn = -OI.INSTANCE.getOperatorController().getRightStick().x();

        double throttle = OI.INSTANCE.getLeftJoystick().getYAxis().get();
        double turn = OI.INSTANCE.getRightJoystick().getXAxis().get();

        SmartDashboard.putNumber("Throttle", throttle);
        SmartDashboard.putNumber("Turn", turn);

        boolean isHighGear = Robot.drive.inHighGear();
        boolean isQuickTurn = OI.INSTANCE.getRightJoystick().getTrigger().get();
//        boolean isQuickTurn = OI.INSTANCE.getOperatorController().rb();
        boolean isAutoTurn = OI.INSTANCE.getLeftJoystick().getKnobCluster().getBottom().get();

        DriveSignal signal = cheesyDrive.update(throttle, turn, isQuickTurn, isHighGear, isAutoTurn);

        double leftPower = signal.getLeft();
        double rightPower = signal.getRight();

        SmartDashboard.putNumber("Drive left power set", leftPower);
        SmartDashboard.putNumber("Drive right power set", rightPower);

        Vision.Limelight limelight = null;

        if (isAutoTurn) {
            if (Robot.cargoHolder.hasCargo()) {
                limelight = Vision.cargoLimelight;
            } else if (Robot.panelIntake.hasPanel()) {
                limelight = Vision.panelLimelight;
            } else {
                limelight = null;
            }

            if (limelight != null) {
                limelight.setLed(Vision.Limelight.LEDMode.On);

                double gyroAngle = Robot.drive.getAngularPosition();
                double limelightAngle = limelight.getTx();
                double goalAngle = gyroAngle + limelightAngle;

                double angularPower = turnController.update(gyroAngle, goalAngle);

                leftPower += angularPower;
                rightPower -= angularPower;

            }
        } else if (limelight != null) {
            limelight.setLed(Vision.Limelight.LEDMode.Off);
            limelight = null;
        }

        Robot.drive.setLeftRight(ControlMode.PercentOutput, leftPower, rightPower);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }


}
