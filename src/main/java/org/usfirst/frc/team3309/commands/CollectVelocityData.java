package org.usfirst.frc.team3309.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.physics.DriveCharacterization;
import org.usfirst.frc.team3309.lib.util.ReflectingCSVWriter;
import org.usfirst.frc.team3309.subsystems.Drive;
import org.usfirst.frc.team4322.commandv2.Command;

import java.util.List;

public class CollectVelocityData extends Command {

    private static final double kMaxPower = 0.5;
    private static final double kRampRate = 0.02;
    private static Drive mDrive;

    private final ReflectingCSVWriter<DriveCharacterization.VelocityDataPoint> mCSVWriter;
    private final List<DriveCharacterization.VelocityDataPoint> mVelocityData;
    private final boolean mTurn;

    private final boolean mReverse;
    private final boolean mHighGear;

    private boolean isFinished = false;
    private double mStartTime = 0.0;

    /**
     * @param data     reference to the list where data points should be stored
     * @param highGear use high gear or low
     * @param reverse  if true drive in reverse, if false drive normally
     * @param turn     if true turn, if false drive straight
     */

    public CollectVelocityData(List<DriveCharacterization.VelocityDataPoint> data, boolean highGear, boolean reverse, boolean turn) {
        require(Robot.drive);
        mDrive = Robot.drive;
        mVelocityData = data;
        mHighGear = highGear;
        mReverse = reverse;
        mTurn = turn;
        mCSVWriter = new ReflectingCSVWriter<>("/home/lvuser/VELOCITY_DATA.csv", DriveCharacterization.VelocityDataPoint.class);

    }

    @Override
    public void initialize() {
        mDrive.reset();
        if (mHighGear)
            mDrive.setHighGear();
        else
            mDrive.setLowGear();
        mStartTime = Timer.getFPGATimestamp();
    }

    @Override
    public void execute() {
        double percentPower = kRampRate * (Timer.getFPGATimestamp() - mStartTime);

        if (percentPower > kMaxPower) {
            isFinished = true;
            return;
        }

        double leftPower = (mReverse ? -1.0 : 1.0) * percentPower;
        double rightPower = (mReverse ? -1.0 : 1.0) * (mTurn ? -1.0 : 1.0) * percentPower;

        mDrive.setLeftRight(ControlMode.PercentOutput, leftPower, rightPower);

        mVelocityData.add(new DriveCharacterization.VelocityDataPoint(
                (Math.abs(mDrive.getLeftEncoderVelocity()) + Math.abs(mDrive.getRightEncoderVelocity())) / 4096.0 * Math.PI * 10, //convert velocity to radians per second
                percentPower * 12.0 //convert to volts
        ));
        mCSVWriter.add(mVelocityData.get(mVelocityData.size() - 1));
    }

    @Override
    public boolean isFinished() {
        return isFinished;
    }

    @Override
    public void end() {
        isFinished = false;
        mDrive.setNeutralMode(NeutralMode.Brake);
        mCSVWriter.flush();
    }
}
