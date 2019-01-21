package org.usfirst.frc.team3309.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.physics.DriveCharacterization;
import org.usfirst.frc.team3309.lib.util.ReflectingCSVWriter;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team3309.subsystems.Drive;
import org.usfirst.frc.team4322.commandv2.Command;

import java.util.List;

public class CollectAccelerationData extends Command {

    private static final double kPower = 0.5;
    private static final double kTotalTime = 6.0; //how long to run the test for

    private static Drive mDrive;

    private final ReflectingCSVWriter<DriveCharacterization.AccelerationDataPoint> mCSVWriter;
    private final List<DriveCharacterization.AccelerationDataPoint> mAccelerationData;
    private final boolean mTurn;
    private final boolean mReverse;
    private final boolean mHighGear;

    private double mStartTime = 0.0;
    private double mPrevVelocity = 0.0;
    private double mPrevTime = 0.0;

    /**
     * @param data     reference to the list where data points should be stored
     * @param highGear use high gear or low
     * @param reverse  if true drive in reverse, if false drive normally
     * @param turn     if true turn, if false drive straight
     */
    public CollectAccelerationData(List<DriveCharacterization.AccelerationDataPoint> data, boolean highGear, boolean reverse, boolean turn) {
        require(Robot.drive);
        mDrive = Robot.drive;
        mAccelerationData = data;
        mHighGear = highGear;
        mReverse = reverse;
        mTurn = turn;
        mCSVWriter = new ReflectingCSVWriter<>("/home/lvuser/ACCEL_DATA.csv", DriveCharacterization.AccelerationDataPoint.class);
    }

    @Override
    public void initialize() {
        if (mHighGear) {
            mDrive.setHighGear();
        } else {
            mDrive.setLowGear();
        }
        Robot.drive.setNeutralMode(NeutralMode.Coast);
        mStartTime = Timer.getFPGATimestamp();
        mPrevTime = mStartTime;

        double reverse = mReverse ? -1.0 : 1.0;
        double leftPower =  reverse * kPower;
        double rightPower = reverse * (mTurn ? -1.0 : 1.0) * kPower;

        Robot.drive.setLeftRight(ControlMode.PercentOutput, leftPower, rightPower);
    }

    @Override
    public void execute() {

        double currentVelocity = (Math.abs(mDrive.getLeftEncoderVelocity()) + Math.abs(mDrive.getRightEncoderVelocity())) / 4096.0 * Math.PI * 10;
        double currentTime = Timer.getFPGATimestamp();

        //don't calculate acceleration until we've populated prevTime and prevVelocity
        if (mPrevTime == mStartTime) {
            mPrevTime = currentTime;
            mPrevVelocity = currentVelocity;
            return;
        }

        double acceleration = (currentVelocity - mPrevVelocity) / (currentTime - mPrevTime);

        //ignore accelerations that are too small
        if (acceleration < Util.kEpsilon) {
            mPrevTime = currentTime;
            mPrevVelocity = currentVelocity;
            return;
        }

        mAccelerationData.add(new DriveCharacterization.AccelerationDataPoint(
                currentVelocity, //convert to radians per second
                kPower * 12.0, //convert to volts
                acceleration
        ));

        mCSVWriter.add(mAccelerationData.get(mAccelerationData.size() - 1));

        mPrevTime = currentTime;
        mPrevVelocity = currentVelocity;
    }

    @Override
    public boolean isFinished() {
        return Timer.getFPGATimestamp() - mStartTime > kTotalTime;
    }

    @Override
    public void end() {
        mDrive.setNeutralMode(NeutralMode.Brake);
        mDrive.setLeftRight(ControlMode.Velocity, 0.0, 0.0);
        mCSVWriter.flush();
    }
}
