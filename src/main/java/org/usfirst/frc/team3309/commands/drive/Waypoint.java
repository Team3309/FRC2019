package org.usfirst.frc.team3309.commands.drive;

import org.usfirst.frc.team3309.Robot;

public class Waypoint {
    public double downFieldInches = 0; //how far the waypoint is from the driver station
    public double xFieldInches = 0; //lateral position of the waypoint
    public double turnRadiusInches = 0; //centered on the vertices of the straight-line path, not the guide circles
    public boolean reverse = false;  // robot backs into waypoint

    //TODO: Tune these
    public double linCreepSpeed = 5; //Inches per second
    public double angCreepSpeed = 23;
    public double maxLinearSpeed = 40; //Inches per 100 milliseconds
    public double maxAngularSpeed = 50; //Degrees per 100 milliseconds
    public double linAccelerationInchesPer100ms2 = 80; //Inches per 100 milliseconds^2
    public double linDecelerationInchesPer100ms2 = 160; //Also in inches per 100 milliseconds^2
    public double angAccelerationDegsPer100ms2 = 70;
    public double angDecelerationDegsPer100ms2 = 70;
    public double linToleranceEncoderCounts;
    public double linToleranceInches;

    double maxLinSpeedEncoderCtsPer100ms;
    double linAccelerationEncoderCtsPer100ms2;
    double linDecelerationEncoderCtsPer100ms2;
    double linCreepSpeedEncoderCtsPer100ms;
    double maxAngSpeedEncoderCtsPer100ms;
    double angAccelerationEncoderCtsPer100ms2;
    double angDecelerationEncoderCtsPer100ms2;
    double angCreepSpeedEncoderCtsPer100ms;

    public Waypoint() {
        initialize();
    }

    public Waypoint(double downFieldInches,
                    double xFieldInches,
                    double turnRadiusInches,
                    boolean reverse) {
        this.downFieldInches = downFieldInches;
        this.xFieldInches = xFieldInches;
        this.turnRadiusInches = turnRadiusInches;
        this.reverse = reverse;

        initialize();
    }

    public Waypoint(double downFieldInches,
                    double xFieldInches,
                    double turnRadiusInches,
                    double maxLinearSpeed,
                    double maxAngularSpeed,
                    double linCreepSpeed,
                    double angularCreepSpeed,
                    boolean reverse) {
        this.downFieldInches = downFieldInches;
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
        maxLinSpeedEncoderCtsPer100ms = Robot.drive.inchesPerSecondToEncoderVelocity(maxLinearSpeed);
        linAccelerationEncoderCtsPer100ms2 = Robot.drive.inchesPerSecondToEncoderVelocity(linAccelerationInchesPer100ms2);
        linDecelerationEncoderCtsPer100ms2 = Robot.drive.inchesPerSecondToEncoderVelocity(linDecelerationInchesPer100ms2);
        linCreepSpeedEncoderCtsPer100ms = Robot.drive.inchesPerSecondToEncoderVelocity(linCreepSpeed);
        maxAngSpeedEncoderCtsPer100ms = Robot.drive.degreesPerSecondToEncoderVelocity(maxAngularSpeed);
        angAccelerationEncoderCtsPer100ms2 = Robot.drive.degreesPerSecondToEncoderVelocity(angAccelerationDegsPer100ms2);
        angDecelerationEncoderCtsPer100ms2 = Robot.drive.degreesPerSecondToEncoderVelocity(angDecelerationDegsPer100ms2);
        angCreepSpeedEncoderCtsPer100ms = Robot.drive.degreesPerSecondToEncoderVelocity(angCreepSpeed);
    }
}
