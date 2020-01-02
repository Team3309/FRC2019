package org.usfirst.frc.team3309.commands.drive;

public class Waypoint {
    float downfieldInches; //how far the waypoint is from the driver station
    float crossfieldInches; //lateral position of the waypoint
    float turnRadiusInches; //centered on the vertices of the straight-line path, not the guide circles
    float maxTravelSpeed; //Encoder ticks per second
    float maxSpeedChange; //Maximum difference in speed from second to second
    boolean reverse;  // robot backs into waypoint

    public Waypoint(float downfieldInches,
                    float crossfieldInches,
                    float turnRadiusInches,
                    float maxTravelSpeed,
                    float maxSpeedChange,
                    boolean reverse) {
        this.downfieldInches = downfieldInches;
        this.crossfieldInches = crossfieldInches;
        this.turnRadiusInches = turnRadiusInches;
        this.maxTravelSpeed = maxTravelSpeed;
        this.maxSpeedChange = maxSpeedChange;
        this.reverse = reverse;
    }
}
