package org.usfirst.frc.team3309.autos;

import edu.wpi.first.wpilibj.command.CommandGroup;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.commands.Drive_Trajectory;
import org.usfirst.frc.team3309.lib.geometry.Pose2d;
import org.usfirst.frc.team3309.lib.geometry.Pose2dWithCurvature;
import org.usfirst.frc.team3309.lib.geometry.Rotation2d;
import org.usfirst.frc.team3309.lib.trajectory.Trajectory;
import org.usfirst.frc.team3309.lib.trajectory.timing.CentripetalAccelerationConstraint;
import org.usfirst.frc.team3309.lib.trajectory.timing.TimedState;
import org.usfirst.frc.team3309.paths.TrajectoryGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestPath extends CommandGroup {

    public TestPath() {
        addSequential(new Drive_Trajectory(getTestPath()));
        super.start();
    }

    private Trajectory<TimedState<Pose2dWithCurvature>> getTestPath() {
        List<Pose2d> waypoints = new ArrayList<>();
        waypoints.add(new Pose2d(100, 0, Rotation2d.identity()));
        return TrajectoryGenerator.getInstance().generateTrajectory(false,
                waypoints, Arrays.asList(new CentripetalAccelerationConstraint(Constants.kMaxCentripetalAccel)
                ), Constants.kMaxVelocity, Constants.kMaxAccel, Constants.kMaxVoltage);
    }



}
