package org.usfirst.frc.team3309.commands.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.PIDController;
import org.usfirst.frc.team3309.lib.util.CheesyDriveHelper;
import org.usfirst.frc.team3309.lib.util.DriveSignal;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team3309.subsystems.Vision;
import org.usfirst.frc.team4322.commandv2.Command;

public class DriveManual extends Command {

    private PIDController turnController = new PIDController("turn", 0.25, 0.0, 0.0);

    private CheesyDriveHelper cheesyDrive = new CheesyDriveHelper();

    private double goalAngle;
    private double limelightAngle;
    private boolean lock;

    private Vision.Limelight limelight = Vision.panelLimelight;


    public DriveManual() {
        require(Robot.drive);
        setInterruptBehavior(InterruptBehavior.Suspend);
//        turnController.outputToDashboard();
    }

    @Override
    protected void execute() {

//        double throttle = OI.INSTANCE.getOperatorController().getLeftStick().y();
//        double turn = -OI.INSTANCE.getOperatorController().getRightStick().x();

        double throttle = OI.INSTANCE.getLeftJoystick().getYAxis().get();
        double turn = OI.INSTANCE.getRightJoystick().getXAxis().get();

//           SmartDashboard.putNumber("Throttle", throttle);
//        SmartDashboard.putNumber("Turn", turn);

        boolean isHighGear = Robot.drive.inHighGear();
        boolean isQuickTurn = OI.INSTANCE.getRightJoystick().getTrigger().get();
//        boolean isQuickTurn = OI.INSTANCE.getOperatorController().rb();
        boolean isAutoTurn = OI.INSTANCE.getLeftJoystick().getKnobCluster().getBottom().get();

        DriveSignal signal = cheesyDrive.update(throttle, turn, isQuickTurn, isHighGear, isAutoTurn);

        double leftPower = signal.getLeft();
        double rightPower = signal.getRight();

//        SmartDashboard.putNumber("Drive left power set", leftPower);
//        SmartDashboard.putNumber("Drive right power set", rightPower);


        if (isAutoTurn) {
//            turnController.readDashboard();

            if (Robot.cargoHolder.hasCargo()) {
                limelight = Vision.cargoLimelight;
            } else if (Robot.panelHolder.hasPanel()) {
                limelight = Vision.panelLimelight;
            }

//            if (!lock) {
                limelight.setCamMode(Vision.Limelight.CamMode.VisionProcessor);
                limelight.setLed(Vision.Limelight.LEDMode.On);
       /*     } else {
                limelight.setLed(Vision.Limelight.LEDMode.Off);
            }*/

            if (Util.within(limelight.getArea(), 3.1, 12)) {

                double skew = limelight.getSkew();
                if (skew == 0 || skew <= -89.0) {
//                    if (!lock) {
                        limelightAngle = limelight.getTx();
//                        lock = true;
//                    }

                    double gyroAngle = Robot.drive.getAngularPosition();

                    goalAngle = gyroAngle + limelightAngle;

                    double angularPower = -turnController.update(gyroAngle, goalAngle);

//                    SmartDashboard.putNumber("Limelight angle", limelightAngle);
//                    SmartDashboard.putNumber("Turn angular power", angularPower);

                    leftPower += angularPower;
                    rightPower -= angularPower;
                }
            }
        } else {
            limelight.setCamMode(Vision.Limelight.CamMode.DriverCamera);
            limelight.setLed(Vision.Limelight.LEDMode.Off);
            limelight = Vision.panelLimelight;
//            lock = false;
        }

        Robot.drive.setLeftRight(ControlMode.PercentOutput, leftPower, rightPower);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }


}
