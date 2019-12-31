package org.usfirst.frc.team3309.commands.drive;

public class Waypoint {
    float downfieldInches;
    float crossfieldInches;
    float turnRadiusInches;
    float maxTravelSpeed; //Encoder ticks per second
    float maxSpeedChange;
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
