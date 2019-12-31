package org.usfirst.frc.team3309.commands.drive;

public class Waypoint {
    int downfieldInches;
    int crossfieldInches;
    int turnRadiusInches;
    boolean reverse;  // robot backs into waypoint

    public Waypoint(int downfieldInches,
                    int crossfieldInches,
                    int turnRadiusInches,
                    boolean reverse) {
        this.downfieldInches = downfieldInches;
        this.crossfieldInches = crossfieldInches;
        this.turnRadiusInches = turnRadiusInches;
        this.reverse = reverse;
    }
}
