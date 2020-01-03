package org.usfirst.frc.team3309.commands.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.CheesyDriveHelper;
import org.usfirst.frc.team3309.lib.util.Util3309;
import org.usfirst.frc.team4322.commandv2.Command;
import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team4322.configuration.RobotConfigFileReader;

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

    private enum spinTurnState {
        notStarted,
        accelerating,
        cruising,
        decelerating,
        tweaking,
        stopped,
        straightDrive
    }

    private double speed = 0;
    private double turn = 0;
    private double left = 0;
    private double lastVelocity;
    Timer ControlTimer = new Timer();


    private travelState state = travelState.stopped;
    private spinTurnState turnState = spinTurnState.notStarted;
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

        boolean debugMode = Robot.getDriveDebug();
        double heading = Robot.drive.getAngularPosition() % 360;

        Waypoint priorPoint = path[nextWaypointIndex];
        Waypoint nextPoint = path[nextWaypointIndex + 1];
        lastVelocity = Robot.drive.getAngularVelocity();

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
             * JK, it actually uses some pretty simple state machine logic:
             * 
             * if state is accelerating, then
             *     if (condition that tells us that we are still accelerating), then
             *         accelerate
             *     else
             *         set state to cruising
             * else if state is cruising, then
             *     if (condition that tells us that we are still cruising), then
             *         cruise
             *     else
             *         set state to decelerating
             * else if state is decelerating, then
             *     if (condition that tells us that we are still decelerating), then
             *         decelerate
             *     else
             *         stop the robot and increment nextWaypointIndex
             */
            turn = 0;
            if (state == travelState.accelerating) {
                if (true) {

                } else {
                    state = travelState.cruising;
                }
            } else if (state == travelState.cruising){
                if (true) {

                } else {
                    state = travelState.decelerating;
                }
            } else if (state == travelState.decelerating){
                if (true) {

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

            double tweakThreshold = 0.001;
            lastVelocity = Robot.drive.getEncoderVelocity();
            ControlTimer.start();
            double timerValue = ControlTimer.get();

            if (timerValue > 0 && turnState == spinTurnState.notStarted) {
                turnState = spinTurnState.accelerating;
            } else if (turnState == spinTurnState.accelerating &&
                    timerValue * nextPoint.angularAccelerationDegreesPerSec2 > nextPoint.maxAngularSpeed) {
                turnState = spinTurnState.cruising;
            } else if (turnState == spinTurnState.cruising &&
                    timerValue * nextPoint.maxAngularSpeed  > nextPoint.maxAngularSpeed) {
                turnState = spinTurnState.decelerating;
                lastVelocity = Robot.drive.getEncoderVelocity();
                ControlTimer.reset();
                timerValue = 0;
            } else if (turnState == spinTurnState.decelerating &&
                    timerValue * nextPoint.angularDecelerationDegreesPerSec2 < nextPoint.angularCreepSpeed) {
                turnState = spinTurnState.tweaking;
            } else {
                turnState = spinTurnState.straightDrive;
                ControlTimer.stop();
                ControlTimer.reset();
                Robot.drive.setLeftRight(ControlMode.PercentOutput, 0, 0);
            }

            if (turnState == spinTurnState.accelerating) {
                left = nextPoint.angularAccelerationDegreesPerSec2 * timerValue;
            } else if (turnState == spinTurnState.cruising) {
                left = nextPoint.maxAngularSpeed;
            } else if (turnState == spinTurnState.decelerating) {
                left = lastVelocity - (nextPoint.angularDecelerationDegreesPerSec2 * timerValue);
            } else if (turnState == spinTurnState.tweaking) {
                if (Robot.drive.getAngularPosition() > headingToNextPoint &&
                        Math.abs(Robot.drive.getAngularPosition()-headingToNextPoint) > tweakThreshold) {
                    left = -nextPoint.angularCreepSpeed;
                } else if (Robot.drive.getAngularPosition() < headingToNextPoint &&
                        Math.abs(Robot.drive.getAngularPosition()-headingToNextPoint) > tweakThreshold) {
                    left = nextPoint.angularCreepSpeed;
                } else if (Math.abs(Robot.drive.getAngularPosition()-headingToNextPoint) < tweakThreshold) {
                    Robot.drive.setLeftRight(ControlMode.PercentOutput, 0, 0);
                    ControlTimer.stop();
                    ControlTimer.reset();
                    state = travelState.stopped;
                    turnState = spinTurnState.notStarted;
                }
            } else {
                Robot.drive.setLeftRight(ControlMode.PercentOutput, 0, 0);
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
            Robot.drive.setLeftRight(ControlMode.Velocity, left, -left);
        }

        Robot.drive.setArcade(ControlMode.Velocity, speed, turn);

        // Example output of variables for debugging purposes - adapt as needed
        if (debugMode) {
            double leftVelocity = 0;
            double RightVelocity = 0;
            SmartDashboard.putNumber("Goal left encoder velocity", leftVelocity);
            SmartDashboard.putNumber("Goal right encoder velocity", RightVelocity);
        }

        ControlTimer.stop();
        ControlTimer.reset();
    }

    @Override
    protected boolean isFinished() {
        return done;
    }
}
