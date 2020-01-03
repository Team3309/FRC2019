package org.usfirst.frc.team3309.commands.drive;

import org.usfirst.frc.team3309.Robot;

public class Waypoint {
    double downfieldInches; //how far the waypoint is from the driver station
    double crossfieldInches; //lateral position of the waypoint
    double turnRadiusInches; //centered on the vertices of the straight-line path, not the guide circles
    double maxStraightTravelSpeed; //Inches per second
    boolean reverse;  // robot backs into waypoint

    double creepSpeed = 5; //Inches per second

    //TODO: Tune these
    double linearAcelerationRateInchesPerSec2 = 0; //Inches per second^2
    double linearDecelerationRateInchesPerSec2 = 0; //Also in inches per second^2
    double angularAcelerationRateDegreesPerSec2 = 0;
    double angularDcelerationRateDegreesPerSec2 = 0;

    double cruiseDistance = 12; //Inches

    double maxTravelSpeedEncoderCounts;
    double linearAcelerationRateEncoderCountsPerSec2;
    double linearDecelerationRateEncoderCountsPerSec2;
    double cruiseDistanceEncoderCounts;
    double creepSpeedEncoderCounts;

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
                    double maxStraightTravelSpeed,
                    double creepSpeed,
                    boolean reverse) {
        this.downfieldInches = downfieldInches;
        this.crossfieldInches = crossfieldInches;
        this.turnRadiusInches = turnRadiusInches;
        this.maxStraightTravelSpeed = maxStraightTravelSpeed;
        this.creepSpeed = creepSpeed;
        this.reverse = reverse;

        initialize();
    }

    private void initialize () {
        maxTravelSpeedEncoderCounts = Robot.drive.inchesPerSecondToEncoderVelocity(maxStraightTravelSpeed);
        linearAcelerationRateEncoderCountsPerSec2 = Robot.drive.inchesPerSecondToEncoderVelocity(linearAcelerationRateInchesPerSec2);
        linearDecelerationRateEncoderCountsPerSec2 = Robot.drive.inchesPerSecondToEncoderVelocity(linearDecelerationRateInchesPerSec2);
        cruiseDistanceEncoderCounts = Robot.drive.inchesToEncoderCounts(cruiseDistance);
        creepSpeedEncoderCounts = Robot.drive.inchesPerSecondToEncoderVelocity(creepSpeed);
    }
}
