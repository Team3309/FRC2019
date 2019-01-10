package org.usfirst.frc.team3309.commands;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.RobotState;
import org.usfirst.frc.team3309.lib.geometry.Pose2dWithCurvature;
import org.usfirst.frc.team3309.lib.planners.DriveMotionPlanner;
import org.usfirst.frc.team3309.lib.trajectory.TimedView;
import org.usfirst.frc.team3309.lib.trajectory.Trajectory;
import org.usfirst.frc.team3309.lib.trajectory.TrajectoryIterator;
import org.usfirst.frc.team3309.lib.trajectory.timing.TimedState;
import org.usfirst.frc.team3309.lib.util.DriveSignal;
import org.usfirst.frc.team4322.commandv2.Command;

public class Drive_Trajectory extends Command {

    private static final RobotState mRobotState = RobotState.getInstance();

    private final TrajectoryIterator<TimedState<Pose2dWithCurvature>> mTrajectory;
    private final boolean mResetPose;
    private DriveMotionPlanner mMotionPlanner;

    public Drive_Trajectory(Trajectory<TimedState<Pose2dWithCurvature>> trajectory) {
        this(trajectory, false);
    }

    public Drive_Trajectory(Trajectory<TimedState<Pose2dWithCurvature>> trajectory, boolean mResetPose) {
        require(Robot.drive);

        mTrajectory = new TrajectoryIterator<>(new TimedView<>(trajectory));
        this.mResetPose = mResetPose;
        mMotionPlanner = new DriveMotionPlanner();
    }

    @Override
    public void initialize() {
        if (mResetPose) {
            mRobotState.reset(Timer.getFPGATimestamp(), mTrajectory.getState().state().getPose());
        }
        mMotionPlanner.reset();
        mMotionPlanner.setTrajectory(mTrajectory);
    }

    @Override
    protected void execute() {

        super.execute();
        double now = Timer.getFPGATimestamp();
        DriveMotionPlanner.Output output = mMotionPlanner.update(now, RobotState.getInstance().getFieldToVehicle(now));

        double leftAccel = Robot.drive.radiansPerSecondToTicksPer100ms(output.left_accel) / 1000.0;
        double rightAccel = Robot.drive.radiansPerSecondToTicksPer100ms(output.right_accel) / 1000.0;
        DriveSignal powerSignal = new DriveSignal(Robot.drive.radiansPerSecondToTicksPer100ms(output.left_velocity),
                Robot.drive.radiansPerSecondToTicksPer100ms(output.right_velocity));
        DriveSignal feedforwardSignal = new DriveSignal(output.left_feedforward_voltage / 12.0,
                output.right_feedforward_voltage / 12.0);

        double leftFeedforward = feedforwardSignal.getLeft() + Constants.kDriveLowGearVelocityKd * leftAccel / 1023.0;
        double rightFeedforward = feedforwardSignal.getRight() + Constants.kDriveLowGearVelocityKd * rightAccel / 1023.0;

        Robot.drive.setLeftRight(
                ControlMode.Velocity, DemandType.ArbitraryFeedForward,
                powerSignal.getLeft(), powerSignal.getRight(),
                leftFeedforward, rightFeedforward);
    }

    @Override
    protected boolean isFinished() {
        return mMotionPlanner.isDone();
    }


}