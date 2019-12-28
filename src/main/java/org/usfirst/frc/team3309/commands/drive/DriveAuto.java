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

    // for autonomous path following
    public DriveAuto(Waypoint[] path, boolean endRollOut) {
        require(Robot.drive);
        setInterruptBehavior(InterruptBehavior.Terminate);

        for (int pidx = 1; pidx < path.length; pidx++) {
            Waypoint priorPoint = path[pidx - 1];
            Waypoint destPoint = path[pidx];
            Waypoint nextPoint = null;
            if (pidx + 1 < path.length) {
                nextPoint = path[pidx + 1];
            }
            // drive from priorPoint to destPoint and
            // start turning towards nextPoint if it exists

            double leftEncoderVelocity = 0;
            double rightEncoderVelocity = 0;
            boolean segmentDone = false;

            while (!segmentDone) {
                // *** add magic code here ***
                Robot.drive.setLeftRight(ControlMode.Velocity, leftEncoderVelocity, rightEncoderVelocity);
            }
        }
        // stop moving
        Robot.drive.setLeftRight(ControlMode.PercentOutput, 0.0, 0.0);
    }

    @Override
    protected void initialize() {
        Robot.drive.setHighGear();
    }

    @Override
    protected void execute() {
    
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
