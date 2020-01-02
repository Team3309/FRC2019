package org.usfirst.frc.team3309.commands.drive;

import org.usfirst.frc.team3309.Robot;

public class Waypoint {
    double downfieldInches; //how far the waypoint is from the driver station
    double crossfieldInches; //lateral position of the waypoint
    double turnRadiusInches; //centered on the vertices of the straight-line path, not the guide circles
    double maxTravelSpeed; //Inches per second
    boolean reverse;  // robot backs into waypoint

    double creepSpeed = 5; //Inches per second
    double accelerationDistance = 24; //Inches
    double cruiseDistance = 12; //Inches
    double decelerationDistance = 24; //Also in inches

    double maxTravelSpeedEncoderCounts;
    double accelerationDistanceEncoderCounts;
    double cruiseDistanceEncoderCounts;
    double decelerationDistanceEncoderCounts;
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
                    double maxTravelSpeed,
                    double creepSpeed,
                    boolean reverse) {
        this.downfieldInches = downfieldInches;
        this.crossfieldInches = crossfieldInches;
        this.turnRadiusInches = turnRadiusInches;
        this.maxTravelSpeed = maxTravelSpeed;
        this.creepSpeed = creepSpeed;
        this.reverse = reverse;

        initialize();
    }

    private void initialize () {
        maxTravelSpeedEncoderCounts = Robot.drive.inchesPerSecondToEncoderVelocity(maxTravelSpeed);
        accelerationDistanceEncoderCounts = Robot.drive.inchesToEncoderCounts(accelerationDistance);
        cruiseDistanceEncoderCounts = Robot.drive.inchesToEncoderCounts(cruiseDistance);
        decelerationDistanceEncoderCounts = Robot.drive.inchesToEncoderCounts(decelerationDistance);
        creepSpeedEncoderCounts = Robot.drive.inchesPerSecondToEncoderVelocity(creepSpeed);
    }
}
