package org.usfirst.frc.team3309.lib.spline;

import org.usfirst.frc.team3309.lib.geometry.Pose2d;
import org.usfirst.frc.team3309.lib.geometry.Pose2dWithCurvature;
import org.usfirst.frc.team3309.lib.geometry.Rotation2d;
import org.usfirst.frc.team3309.lib.geometry.Translation2d;

public abstract class Spline {
    public abstract Translation2d getPoint(double t);

    public abstract Rotation2d getHeading(double t);

    public abstract double getCurvature(double t);

    // dk/ds
    public abstract double getDCurvature(double t);

    // ds/dt
    public abstract double getVelocity(double t);

    public Pose2d getPose2d(double t) {
        return new Pose2d(getPoint(t), getHeading(t));
    }

    public Pose2dWithCurvature getPose2dWithCurvature(double t) {
        return new Pose2dWithCurvature(getPose2d(t), getCurvature(t), getDCurvature(t) / getVelocity(t));
    }

    // TODO add toString
    // public abstract String toString();
}
