package org.usfirst.frc.team3309.lib.trajectory;

import org.usfirst.frc.team3309.lib.geometry.Pose2d;
import org.usfirst.frc.team3309.lib.geometry.Twist2d;

public interface IPathFollower {
    public Twist2d steer(Pose2d current_pose);

    public boolean isDone();
}
