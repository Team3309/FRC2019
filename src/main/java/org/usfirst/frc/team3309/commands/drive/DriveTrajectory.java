package org.usfirst.frc.team3309.commands.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.DemandType;
import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.geometry.Pose2d;
import org.usfirst.frc.team3309.lib.geometry.Pose2dWithCurvature;
import org.usfirst.frc.team3309.lib.planners.DriveMotionPlanner;
import org.usfirst.frc.team3309.lib.trajectory.TimedView;
import org.usfirst.frc.team3309.lib.trajectory.Trajectory;
import org.usfirst.frc.team3309.lib.trajectory.TrajectoryIterator;
import org.usfirst.frc.team3309.lib.trajectory.timing.TimedState;
import org.usfirst.frc.team3309.lib.util.DriveSignal;
import org.usfirst.frc.team4322.commandv2.Command;

/*
 *   Follows a trajectory using 254's Ramsete implementation
 * */
public class DriveTrajectory extends Command {

    private final TrajectoryIterator<TimedState<Pose2dWithCurvature>> mTrajectory;
    private final boolean mResetPose;
    private DriveMotionPlanner mMotionPlanner;

    public DriveTrajectory(Trajectory<TimedState<Pose2dWithCurvature>> trajectory) {
        this(trajectory, false);
    }

    /*
     *   @params
     *       trajectory, the path to follow
     *       mResetPose, whether to rezero
     *
     * */
    public DriveTrajectory(Trajectory<TimedState<Pose2dWithCurvature>> trajectory, boolean mResetPose) {
        require(Robot.drive);
        setInterruptBehavior(InterruptBehavior.Suspend);
        mTrajectory = new TrajectoryIterator<>(new TimedView<>(trajectory));
        this.mResetPose = mResetPose;
        mMotionPlanner = new DriveMotionPlanner();
        mMotionPlanner.setFollowerType(DriveMotionPlanner.FollowerType.NONLINEAR_FEEDBACK);
    }

    @Override
    public void initialize() {
        Robot.drive.setHighGear();
        if (mResetPose) {
            Robot.drive.getRobotStateEstimator().reset(Timer.getFPGATimestamp(), mTrajectory.getState().state().getPose());
        }
        mMotionPlanner.reset();
        mMotionPlanner.setTrajectory(mTrajectory);
    }

    @Override
    protected void execute() {
        super.execute();
        double now = Timer.getFPGATimestamp();
        Pose2d robotPose = Robot.drive.getRobotStateEstimator().getFieldToVehicle(now);

        DriveMotionPlanner.Output output = mMotionPlanner.update(now, robotPose);

        double leftAccel = Robot.drive.radiansPerSecondToTicksPer100ms(output.left_accel) / 1000.0;
        double rightAccel = Robot.drive.radiansPerSecondToTicksPer100ms(output.right_accel) / 1000.0;
        DriveSignal powerSignal = new DriveSignal(Robot.drive.radiansPerSecondToTicksPer100ms(output.left_velocity),
                Robot.drive.radiansPerSecondToTicksPer100ms(output.right_velocity));
        DriveSignal feedforwardSignal = new DriveSignal(output.left_feedforward_voltage / 12.0,
                output.right_feedforward_voltage / 12.0);

        double leftFeedforward = feedforwardSignal.getLeft() + Constants.DRIVE_D * leftAccel / 1023.0;
        double rightFeedforward = feedforwardSignal.getRight() + Constants.DRIVE_D * rightAccel / 1023.0;

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
