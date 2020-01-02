package org.usfirst.frc.team3309.commands.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.CheesyDriveHelper;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team4322.commandv2.Command;


public class DriveAuto extends Command {

    private CheesyDriveHelper cheesyDrive = new CheesyDriveHelper();

    private enum travelState {
        stopped,
        accelerating,
        cruising, //Moving at a set speed
        decelerating,
        rolling, //Moving with momentum
        turning,
        turningInPlace //spin turn
    }

    private double speed = 0;
    private double turn = 0;

    private travelState state = travelState.stopped;
    private int nextWaypointIndex = 0;

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
        super.initialize();
        Robot.drive.setHighGear();
        Robot.drive.reset();
        Robot.drive.setNeutralMode(NeutralMode.Brake);
    }

    @Override
    protected void execute() {

        double heading = Robot.drive.getAngularPosition() % 360;

        Waypoint priorPoint = path[nextWaypointIndex - 1];
        Waypoint nextPoint = path[nextWaypointIndex];

        double headingToNextPoint = Math.toDegrees(Math.atan((priorPoint.downfieldInches - nextPoint.downfieldInches)/
                (priorPoint.crossfieldInches - nextPoint.crossfieldInches)));

        double inchesFromWaypoints = Util.distanceFormula(priorPoint.downfieldInches, priorPoint.crossfieldInches,
                nextPoint.downfieldInches, nextPoint.crossfieldInches);

        double inchesTraveled = Robot.drive.encoderCountsToInches(Robot.drive.getEncoderDistance());

        if (nextPoint.turnRadiusInches == 0) {
            /* Drive Straight
             * Accelerate until the robot is 10% to its destination. Then remain at a constant
             * speed until 90% to destination. Finally, decelerate at the same rate of acceleration
             * until destination has been reached
             */
            turn = 0;
            if (state == travelState.accelerating) {
                if (inchesTraveled < nextPoint.accelerationDistance) {
                    if (speed <= nextPoint.maxTravelSpeedEncoderCounts) {
                        if (inchesTraveled / nextPoint.accelerationDistance >= nextPoint.creepSpeed) {
                            speed = inchesTraveled / nextPoint.accelerationDistance;
                        } else {
                            speed = nextPoint.creepSpeed;
                        }
                    }
                } else {
                    //Change state once 10% to the destination
                    state = travelState.cruising;
                }
            } else if (state == travelState.cruising){
                if (inchesTraveled < inchesFromWaypoints - nextPoint.decelerationDistance) {
                    speed = nextPoint.maxTravelSpeed;
                } else {
                    //Change state once 90% to the destination
                    state = travelState.decelerating;
                }
            } else if (state == travelState.decelerating){
                if (inchesTraveled <= inchesFromWaypoints) {
                    if (inchesTraveled / (inchesFromWaypoints - nextPoint.decelerationDistance) > nextPoint.creepSpeed) {
                        speed = inchesTraveled / (inchesFromWaypoints - nextPoint.decelerationDistance);
                    } else {
                        speed = nextPoint.creepSpeed;
                    }
                } else {
                    //Stop and go to next waypoint
                    speed = 0;
                    nextWaypointIndex++;
                    state = travelState.stopped;
                    Robot.drive.zeroEncoders();
                }
            }
        } else if (nextPoint.turnRadiusInches !=0 && nextPoint.crossfieldInches + nextPoint.downfieldInches != 0) {
            state = travelState.turning;

            //Turn to the next point. We may be able to use the Cheesy Drive algorithm for this.


            //Turn on a circle (pseudocode).
            // 1) Find where circle touches straight-line paths of the auto.
            // 2) Start the turn at every other point. Untangling complex auto paths can happen later.
            //    a) Based on the angular and linear velocity of the robot, calculate how many
            //    degrees/sec the robot must turn to reach headingToNextPoint in time.
            //    b) Have the robot turn by that number of deg/sec
            // 3) End the turn at every non-start point.

        } else if (nextPoint.turnRadiusInches == 0 && nextPoint.crossfieldInches + nextPoint.downfieldInches == 0) {
            //Turn in place
            state = travelState.turningInPlace;
            double k1, k2, k3, k4;

            //robot MUST be finished by the time it reaches turnSpace
            double turnSpace = headingToNextPoint;
            double time = 0;
            k1 = 0; //accelerate to k2
            k2 = 0; //how fast we want the bot to turn.
            k3 = 0; //deceleration from k2
            k4 = 0; //final adjustments

            if (Robot.drive.getAngularVelocity() == 0 && Robot.drive.getVelocityY() == 0
                    && Robot.drive.getVelocityX() == 0) {
                Robot.drive.setLeftRight(ControlMode.Velocity, k1, -k1);
            } else if (Robot.drive.getAngularVelocity() == k2) {
                Robot.drive.setLeftRight(ControlMode.Velocity, k2, -k2);
            } else if (time == 0) {
                Robot.drive.setLeftRight(ControlMode.Velocity, k3, -k3);
            }
            //Pseudocode
            //
            // drive.accel(k1, m);
            //   until (k2) Robot.drive.setLeftRight(ControlMode.Velocity, k1 + m, -k1 - m);
            // drive.cruise(k2);
            //   Robot.drive.setLeftRight(ControlMode.Velocity, k2, -k2);
            // drive.decel(k3, n);
            //   until (k3) Robot.drive.setLeftRight(ControlMode.Velocity, k3 - n, n - k3);
            // drive.adjustAt(k4);
            //
            // angularPosition.zero();
            // angularVelocity.zero();


            Robot.drive.setLeftRight(ControlMode.Velocity, k2, -k2);
        }

        Robot.drive.setArcade(ControlMode.Velocity, speed, turn);
    }

    @Override
    protected boolean isFinished() {
        return done;
    }
}
