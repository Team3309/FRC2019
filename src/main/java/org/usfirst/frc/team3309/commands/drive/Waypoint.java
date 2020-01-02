package org.usfirst.frc.team3309.commands.drive;

import org.usfirst.frc.team3309.Robot;

public class Waypoint {
    double downfieldInches; //how far the waypoint is from the driver station
    double crossfieldInches; //lateral position of the waypoint
    double turnRadiusInches; //centered on the vertices of the straight-line path, not the guide circles
    double maxTravelSpeed; //Inches per second
    double maxSpeedChange; //Maximum difference in speed from second to second
    boolean reverse;  // robot backs into waypoint

    double maxTravelSpeedEncoderCounts;
    double maxSpeedChangeEncoderCounts;

    public Waypoint() {}

    public Waypoint(double downfieldInches,
                    double crossfieldInches,
                    double turnRadiusInches,
                    double maxTravelSpeed,
                    double maxSpeedChange,
                    boolean reverse) {
        this.downfieldInches = downfieldInches;
        this.crossfieldInches = crossfieldInches;
        this.turnRadiusInches = turnRadiusInches;
        this.maxTravelSpeed = maxTravelSpeed;
        this.maxSpeedChange = maxSpeedChange;
        this.reverse = reverse;

        maxTravelSpeedEncoderCounts = Robot.drive.inchesPerSecondToEncoderVelocity(maxTravelSpeed);
        maxSpeedChangeEncoderCounts = Robot.drive.inchesToEncoderCounts(maxSpeedChange);
    }
}
