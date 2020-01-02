package org.usfirst.frc.team3309.commands.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.CheesyDriveHelper;
import org.usfirst.frc.team3309.lib.util.Util3309;
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

        double inchesFromWaypoints = Util3309.distanceFormula(priorPoint.downfieldInches, priorPoint.crossfieldInches,
                nextPoint.downfieldInches, nextPoint.crossfieldInches);

        double inchesTraveled = Robot.drive.encoderCountsToInches(Robot.drive.getEncoderDistance());

        if (nextPoint.turnRadiusInches == 0) {
            /*
             * Drive Straight
             *
             * Accelerate until the robot has traveled the acceleration distance. Then
             * remain at a constant speed until robot enters the deceleration zone. Finally,
             * decelerate until destination has been reached
             *
             *          │    ______________
             *          │   /              \
             * Velocity │  /                \
             *          │ /                  \___
             *          │/                       \
             *           -------------------------
             *                    Time
             *
             * Prepare to enter the logic jungle. You have been warned
             */
            turn = 0;
            if (state == travelState.accelerating) {
                if (inchesTraveled < nextPoint.accelerationDistance) {
                    //Prevent the robot from not moving by making the minimum speed the creepSpeed
                    if (inchesTraveled / nextPoint.accelerationDistance >= nextPoint.creepSpeed) {
                        speed = inchesTraveled / nextPoint.accelerationDistance;
                    } else {
                        speed = nextPoint.creepSpeed;
                    }
                } else {
                    state = travelState.cruising;
                }
            } else if (state == travelState.cruising){
                if (inchesTraveled < inchesFromWaypoints - nextPoint.decelerationDistance) {
                    speed = nextPoint.maxTravelSpeed;
                } else {
                    state = travelState.decelerating;
                }
            } else if (state == travelState.decelerating){
                if (inchesTraveled <= inchesFromWaypoints) {
                    //Prevent the robot from not moving by making the minimum speed the creepSpeed
                    if (inchesTraveled / (inchesFromWaypoints - nextPoint.decelerationDistance) > nextPoint.creepSpeed) {
                        speed = inchesTraveled / (inchesFromWaypoints - nextPoint.decelerationDistance);
                    } else {
                        speed = nextPoint.creepSpeed;
                    }
                } else {
                    //Stop robot and start moving to next waypoint
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
