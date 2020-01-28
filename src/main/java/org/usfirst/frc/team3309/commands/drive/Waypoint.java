package org.usfirst.frc.team3309.commands.drive;

import org.usfirst.frc.team3309.Robot;

public class Waypoint {
    double downfieldInches = 0; //how far the waypoint is from the driver station
    double xFieldInches = 0; //lateral position of the waypoint
    double turnRadiusInches = 0; //centered on the vertices of the straight-line path, not the guide circles
    boolean reverse = false;  // robot backs into waypoint

    //TODO: Tune these
    double linCreepSpeed = 5; //Inches per second
    double angCreepSpeed = 23;
    double maxLinearSpeed = 40; //Inches per second
    double maxAngularSpeed = 50; //Degrees per second
    double linAccelerationInchesPerSec2 = 80; //Inches per second^2
    double linDecelerationInchesPerSec2 = 160; //Also in inches per second^2
    double angAccelerationDegsPerSec2 = 70;
    double angDecelerationDegsPerSec2 = 70;

    double maxLinSpeedEncoderCtsPerSec;
    double linAccelerationEncoderCtsPerSec2;
    double linDecelerationEncoderCtsPerSec2;
    double linCreepSpeedEncoderCtsPerSec;
    double maxAngSpeedEncoderCtsPerSec;
    double angAccelerationEncoderCtsPerSec2;
    double angDecelerationEncoderCtsPerSec2;
    double angCreepSpeedEncoderCtsPerSec;

    public Waypoint() {
        initialize();
    }

    public Waypoint(double downfieldInches,
                    double xFieldInches,
                    double turnRadiusInches,
                    boolean reverse) {
        this.downfieldInches = downfieldInches;
        this.xFieldInches = xFieldInches;
        this.turnRadiusInches = turnRadiusInches;
        this.reverse = reverse;

        initialize();
    }

    public Waypoint(double downfieldInches,
                    double xFieldInches,
                    double turnRadiusInches,
                    double maxLinearSpeed,
                    double maxAngularSpeed,
                    double linCreepSpeed,
                    double angularCreepSpeed,
                    boolean reverse) {
        this.downfieldInches = downfieldInches;
        this.xFieldInches = xFieldInches;
        this.turnRadiusInches = turnRadiusInches;
        this.maxLinearSpeed = maxLinearSpeed;
        this.maxAngularSpeed = maxAngularSpeed;
        this.linCreepSpeed = linCreepSpeed;
        this.angCreepSpeed = angularCreepSpeed;
        this.reverse = reverse;

        initialize();
    }

    private void initialize () {
        maxLinSpeedEncoderCtsPerSec = Robot.drive.inchesPerSecondToEncoderVelocity(maxLinearSpeed);
        linAccelerationEncoderCtsPerSec2 = Robot.drive.inchesPerSecondToEncoderVelocity(linAccelerationInchesPerSec2);
        linDecelerationEncoderCtsPerSec2 = Robot.drive.inchesPerSecondToEncoderVelocity(linDecelerationInchesPerSec2);
        linCreepSpeedEncoderCtsPerSec = Robot.drive.inchesPerSecondToEncoderVelocity(linCreepSpeed);
        maxAngSpeedEncoderCtsPerSec = Robot.drive.degreesPerSecondToEncoderVelocity(maxAngularSpeed);
        angAccelerationEncoderCtsPerSec2 = Robot.drive.degreesPerSecondToEncoderVelocity(angAccelerationDegsPerSec2);
        angDecelerationEncoderCtsPerSec2 = Robot.drive.degreesPerSecondToEncoderVelocity(angDecelerationDegsPerSec2);
        angCreepSpeedEncoderCtsPerSec = Robot.drive.degreesPerSecondToEncoderVelocity(angCreepSpeed);
    }
}
