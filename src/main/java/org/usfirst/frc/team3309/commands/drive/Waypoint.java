<<<<<<< HEAD
package org.usfirst.frc.team3309.commands.drive;

import org.usfirst.frc.team3309.Robot;

public class Waypoint {
    double downfieldInches = 0; //how far the waypoint is from the driver station
    double crossfieldInches = 0; //lateral position of the waypoint
    double turnRadiusInches = 0; //centered on the vertices of the straight-line path, not the guide circles
    boolean reverse = false;  // robot backs into waypoint

    //TODO: Tune these
    double linearCreepSpeed = 5; //Inches per second
    double angularCreepSpeed = 23;
    double maxLinearSpeed = 40; //Inches per second
    double maxAngularSpeed = 50; //Degrees per second
    double linearAccelerationInchesPerSec2 = 80; //Inches per second^2
    double linearDecelerationInchesPerSec2 = 160; //Also in inches per second^2
    double angularAccelerationDegreesPerSec2 = 70;
    double angularDecelerationDegreesPerSec2 = 70;

    double linearToleranceInches = .5;

    double maxLinearSpeedEncoderCountsPerSec;
    double linearAccelerationEncoderCountsPerSec2;
    double linearDecelerationEncoderCountsPerSec2;
    double linearCreepSpeedEncoderCountsPerSec;
    double maxAngularSpeedEncoderCountsPerSec;
    double angularAccelerationEncoderCountsPerSec2;
    double angularDecelerationEncoderCountsPerSec2;
    double angularCreepSpeedEncoderCountsPerSec;

    double linearToleranceEncoderCounts;

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
                    double linearCreepSpeed,
                    double angularCreepSpeed,
                    double linearToleranceInches,
                    boolean reverse) {
        this.downfieldInches = downfieldInches;
        this.crossfieldInches = crossfieldInches;
        this.turnRadiusInches = turnRadiusInches;
        this.maxLinearSpeed = maxLinearSpeed;
        this.maxAngularSpeed = maxAngularSpeed;
        this.linearCreepSpeed = linearCreepSpeed;
        this.angularCreepSpeed = angularCreepSpeed;
        this.reverse = reverse;

        initialize();
    }

    private void initialize () {
        maxLinearSpeedEncoderCountsPerSec = Robot.drive.inchesPerSecondToEncoderVelocity(maxLinearSpeed);
        linearAccelerationEncoderCountsPerSec2 = Robot.drive.inchesPerSecondToEncoderVelocity(linearAccelerationInchesPerSec2);
        linearDecelerationEncoderCountsPerSec2 = Robot.drive.inchesPerSecondToEncoderVelocity(linearDecelerationInchesPerSec2);
        linearCreepSpeedEncoderCountsPerSec = Robot.drive.inchesPerSecondToEncoderVelocity(linearCreepSpeed);
        maxAngularSpeedEncoderCountsPerSec = Robot.drive.degreesPerSecondToEncoderVelocity(maxAngularSpeed);
        angularAccelerationEncoderCountsPerSec2 = Robot.drive.degreesPerSecondToEncoderVelocity(angularAccelerationDegreesPerSec2);
        angularDecelerationEncoderCountsPerSec2 = Robot.drive.degreesPerSecondToEncoderVelocity(angularDecelerationDegreesPerSec2);
        angularCreepSpeedEncoderCountsPerSec = Robot.drive.degreesPerSecondToEncoderVelocity(angularCreepSpeed);
        linearToleranceEncoderCounts = Robot.drive.inchesToEncoderCounts(linearToleranceInches);
    }
}
=======
package org.usfirst.frc.team3309.commands.drive;

import org.usfirst.frc.team3309.Robot;

public class Waypoint {

    //All the positional data of each waypoint.
    public double xFieldInches;
    public double downFieldInches;
    double turnRadiusInches = 0; //centered on the vertices of the straight-line path, not the guide circles
    boolean reverse = false;  // robot backs into waypoint

    //TODO: Tune these.
    //Velocity data for Waypoint objects. Non-encoder values in inches/sec for linear motion, and in degrees per
    //second for angular motion.
    double maxLinearSpeed = 40;
    double maxAngularSpeed = 50;
    double linearCreepSpeed = 5;
    double angularCreepSpeed = 23;
    double maxLinSpeedEncoderCtsPer100ms;
    double maxAngSpeedEncoderCtsPer100ms;
    double linCreepSpeedEncoderCtsPer100ms;
    double angCreepSpeedEncoderCtsPer100ms;

    //Acceleration data for Waypoint objects.
    double linAccelerationInsPer100ms2 = 80; //Inches per second^2
    double linDecelerationInsPer100ms2 = 160; //Also in inches per second^2
    double angAccelerationDegsPer100ms2 = 70;
    double angDecelerationDegsPer100ms2 = 70;
    double linAccelerationEncoderCtsPer100ms2;
    double linDecelerationEncoderCtsPer100ms2;
    double angAccelerationEncoderCtsPer100ms2;
    double angDecelerationEncoderCtsPer100ms2;


    public Waypoint() {
        initialize();
    }

    public Waypoint(double xFieldInches,
                    double downFieldInches,
                    double turnRadiusInches,
                    boolean reverse) {
        this.xFieldInches = xFieldInches;
        this.downFieldInches = downFieldInches;
        this.turnRadiusInches = turnRadiusInches;
        this.reverse = reverse;

        initialize();
    }

    public Waypoint(double xFieldInches,
                    double downFieldInches,
                    double turnRadiusInches,
                    double maxLinearSpeed,
                    double maxAngularSpeed,
                    double linearCreepSpeed,
                    double angularCreepSpeed,
                    boolean reverse) {
        this.xFieldInches = xFieldInches;
        this.downFieldInches = downFieldInches;
        this.turnRadiusInches = turnRadiusInches;
        this.maxLinearSpeed = maxLinearSpeed;
        this.maxAngularSpeed = maxAngularSpeed;
        this.linearCreepSpeed = linearCreepSpeed;
        this.angularCreepSpeed = angularCreepSpeed;
        this.reverse = reverse;

        initialize();
    }

    private void initialize () {
        maxLinSpeedEncoderCtsPer100ms = Robot.drive.inchesPerSecondToEncoderVelocity(maxLinearSpeed);
        linAccelerationEncoderCtsPer100ms2 = Robot.drive.inchesPerSecondToEncoderVelocity(linAccelerationInsPer100ms2);
        linDecelerationEncoderCtsPer100ms2 = Robot.drive.inchesPerSecondToEncoderVelocity(linDecelerationInsPer100ms2);
        linCreepSpeedEncoderCtsPer100ms = Robot.drive.inchesPerSecondToEncoderVelocity(linearCreepSpeed);
        maxAngSpeedEncoderCtsPer100ms = Robot.drive.degreesPerSecondToEncoderVelocity(maxAngularSpeed);
        angAccelerationEncoderCtsPer100ms2 = Robot.drive.degreesPerSecondToEncoderVelocity(angAccelerationDegsPer100ms2);
        angDecelerationEncoderCtsPer100ms2 = Robot.drive.degreesPerSecondToEncoderVelocity(angDecelerationDegsPer100ms2);
        angCreepSpeedEncoderCtsPer100ms = Robot.drive.degreesPerSecondToEncoderVelocity(angularCreepSpeed);
    }
}
>>>>>>> origin/temp
