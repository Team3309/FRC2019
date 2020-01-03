package org.usfirst.frc.team3309.commands.drive;

import org.usfirst.frc.team3309.Robot;

public class Waypoint {
    double downfieldInches = 0; //how far the waypoint is from the driver station
    double crossfieldInches = 0; //lateral position of the waypoint
    double turnRadiusInches = 0; //centered on the vertices of the straight-line path, not the guide circles
    boolean reverse = false;  // robot backs into waypoint

    double creepSpeed = 5; //Inches per second

    //TODO: Tune these
    double maxLinearSpeed = 0; //Inches per second
    double maxAngularSpeed = 0;
    double linearAccelerationInchesPerSec2 = 0; //Inches per second^2
    double linearDecelerationInchesPerSec2 = 0; //Also in inches per second^2
    double angularAccelerationDegreesPerSec2 = 0;
    double angularDecelerationDegreesPerSec2 = 0;

    double cruiseDistance = 12; //Inches

    double maxTravelSpeedEncoderCounts = 0;
    double linearAccelerationEncoderCountsPerSec2 = 0;
    double linearDecelerationEncoderCountsPerSec2 = 0;
    double cruiseDistanceEncoderCounts = 0;
    double creepSpeedEncoderCounts = 0;

    public Waypoint() {
        initialize();
    }

    public Waypoint(double downfieldInches,
                    double crossfieldInches,
                    double turnRadiusInches,
                    boolean reverse) {
        this.downfieldInches = downfieldInches;
        this.crossfieldInches = crossfieldInches;
        this.turnRadiusInches = turnRadiusInches;
        this.reverse = reverse;

        initialize();
    }

    public Waypoint(double downfieldInches,
                    double crossfieldInches,
                    double turnRadiusInches,
                    double maxLinearSpeed,
                    double maxAngularSpeed,
                    double creepSpeed,
                    boolean reverse) {
        this.downfieldInches = downfieldInches;
        this.crossfieldInches = crossfieldInches;
        this.turnRadiusInches = turnRadiusInches;
        this.maxLinearSpeed = maxLinearSpeed;
        this.creepSpeed = creepSpeed;
        this.reverse = reverse;

        initialize();
    }

    private void initialize () {
        maxTravelSpeedEncoderCounts = Robot.drive.inchesPerSecondToEncoderVelocity(maxLinearSpeed);
        linearAccelerationEncoderCountsPerSec2 = Robot.drive.inchesPerSecondToEncoderVelocity(linearAccelerationInchesPerSec2);
        linearDecelerationEncoderCountsPerSec2 = Robot.drive.inchesPerSecondToEncoderVelocity(linearDecelerationInchesPerSec2);
        cruiseDistanceEncoderCounts = Robot.drive.inchesToEncoderCounts(cruiseDistance);
        creepSpeedEncoderCounts = Robot.drive.inchesPerSecondToEncoderVelocity(creepSpeed);
    }
}
