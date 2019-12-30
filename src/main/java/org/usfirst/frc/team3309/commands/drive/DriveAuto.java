package org.usfirst.frc.team3309.commands.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Command;



public class DriveAuto extends Command {

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

    private boolean done = false;
    private Waypoint[] path;
    private boolean endRollout;

    // for autonomous path following
    public DriveAuto(Waypoint[] path, boolean endRollOut) {
        require(Robot.drive);
        setInterruptBehavior(InterruptBehavior.Terminate);

        this.path = path;
        this.endRollout = endRollOut;
    }

    @Override
    protected void initialize() {
        Robot.drive.setHighGear();
    }

    @Override
    protected void execute() {

        // Need to adapt this sequential code to work with execute() being
        // continuously called by saving the state from the previous execute()
        // and continuing on the path. This code is just a concept of the work required.

        // This loop won't work because we need to end the method and wait to be called again
        for (int pathIdx = 1; pathIdx < path.length; pathIdx++) {
            Waypoint priorPoint = path[pathIdx - 1];
            Waypoint destPoint = path[pathIdx];
            Waypoint nextPoint = null;
            if (pathIdx + 1 < path.length) {
                nextPoint = path[pathIdx + 1];
            }
            // drive from priorPoint to destPoint and
            // start turning towards nextPoint if it exists

            double leftEncoderVelocity = 0;
            double rightEncoderVelocity = 0;
            boolean segmentDone = false;

            // This loop won't work because we need to end the method and wait to be called again
            while (!segmentDone) {

                // *** add magic code here to calculate drive values ***
                Robot.drive.setLeftRight(ControlMode.Velocity, leftEncoderVelocity, rightEncoderVelocity);
            }
        }
        // stop moving
        Robot.drive.setLeftRight(ControlMode.PercentOutput, 0.0, 0.0);
        done = true;
    }

    @Override
    protected boolean isFinished() {
        return done;
    }
}
