package org.usfirst.frc.team3309.autos

import org.usfirst.frc.team3309.Constants
import org.usfirst.frc.team3309.commands.Drive_Trajectory
import org.usfirst.frc.team3309.lib.geometry.Pose2d
import org.usfirst.frc.team3309.lib.geometry.Pose2dWithCurvature
import org.usfirst.frc.team3309.lib.geometry.Rotation2d
import org.usfirst.frc.team3309.lib.trajectory.Trajectory
import org.usfirst.frc.team3309.lib.trajectory.timing.CentripetalAccelerationConstraint
import org.usfirst.frc.team3309.lib.trajectory.timing.TimedState
import org.usfirst.frc.team3309.lib.trajectory.timing.TimingConstraint
import org.usfirst.frc.team3309.paths.TrajectoryGenerator
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

import java.util.ArrayList
import java.util.Arrays

object AutoTestPath {

    private val testPath: Trajectory<TimedState<Pose2dWithCurvature>>
        get() {
            val waypoints = ArrayList<Pose2d>()
            waypoints.add(Pose2d(0.0, 0.0, Rotation2d.identity()))
            waypoints.add(Pose2d(100.0, 0.0, Rotation2d.identity()))
            return TrajectoryGenerator.getInstance().generateTrajectory(false,
                    waypoints, Arrays.asList<TimingConstraint<Pose2dWithCurvature>>(CentripetalAccelerationConstraint(Constants.kMaxCentripetalAccel)
            ), Constants.kMaxVelocity, Constants.kMaxAccel, Constants.kMaxVoltage)
        }

    @JvmStatic
    fun to(): Command {
        return group {
            Drive_Trajectory(testPath)
        }
    }

}