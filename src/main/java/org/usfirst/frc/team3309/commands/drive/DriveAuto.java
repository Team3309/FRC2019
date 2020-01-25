package org.usfirst.frc.team3309.commands.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.Util3309;
import org.usfirst.frc.team4322.commandv2.Command;
import edu.wpi.first.wpilibj.Timer;

public class DriveAuto extends Command {

    private enum superState {
        stopped,
        drivingStraight,
        spinTurning,
        mobileTurning
    }

    private enum travelState {
        stopped,
        accelerating, //accelerating to cruise speed
        cruising, //Moving at a set speed
        decelerating //decelerating to approach desired point
    }

    private enum spinTurnState {
        notStarted(0),
        accelerating(1), //accelerating to angular cruise speed
        cruising(2), //angular cruising speed
        decelerating(3), //decelerating to approach tweak speed
        tweaking(4); //speed at which final heading is corrected

        int val;
        private spinTurnState(int val) {this.val = val;}
    }

    double speed = 0;

    private double lastVelocity;
    Timer ControlTimer = new Timer();

    private superState superStateMachine = superState.spinTurning;
    double encoderZeroValue;

    final double kTurnCorrectionConstant = .05;
    final double kDecelerationConstant = 0.1;

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
        ControlTimer.reset();
        ControlTimer.start();
        Robot.drive.setHighGear();
    }

    @Override
    protected void execute() {
        if (done)
            return;

        boolean debugMode = Robot.getDriveDebug();

        double heading = 0;

        Waypoint priorPoint = path[nextWaypointIndex];
        Waypoint nextPoint = path[nextWaypointIndex + 1];
        double headingToNextPoint = Math.toDegrees(Math.atan2(nextPoint.downfieldInches - priorPoint.downfieldInches,
                nextPoint.crossfieldInches - priorPoint.crossfieldInches)) - 90;
        if (headingToNextPoint < -180) {
            headingToNextPoint += 360;
        }

        double inchesBetweenWaypoints = Util3309.distanceFormula(priorPoint.downfieldInches, priorPoint.crossfieldInches,
                nextPoint.downfieldInches, nextPoint.crossfieldInches);

        if (superStateMachine == superState.spinTurning) {
            //Top level state machine for turning in place, driving straight, turning on the move (merge state enums):
            //Use. heading:
            //Change lastVelocity naming --> angular velocity (encoder velocity != angular velocity); remove
            //first two usages:
            //TOP LEVEL STATE MACHINE
            // #check whether it needs to execute spin turn, turn on move, drive straight

            //Turn in place:
            //  Accelerate turning till we reach our turn cruising speed.
            //  Turn at cruising speed till we approach
            //  Decelerate turning speed till we reach tweaking speed.
            //  Tweak heading until it is within a healthy margin of error.
            //  Allow the robot to drive again.
            //  Reset all sensors for next operation.

            final double kTweakThreshold = 2.0;
            double timerValue = ControlTimer.get();
            double left = 0;
            //checks that this is the start of auto; timer should be started and robot should not have
            //been previously started
            if (turnState == spinTurnState.notStarted) {
                ControlTimer.reset();
                turnState = spinTurnState.accelerating;
            }
            if (turnState == spinTurnState.accelerating)   {
                 left = nextPoint.angularAccelerationDegreesPerSec2 * timerValue;
            }
            //checks whether we should start cruising; we should have finished our acceleration phase
            //and we should be approaching our cruise velocity
            if (turnState == spinTurnState.accelerating &&
                    left > nextPoint.maxAngularSpeed) {
                turnState = spinTurnState.cruising;
            }
            if (turnState == spinTurnState.cruising) {
                left = nextPoint.maxAngularSpeed;
            }
            //checks whether we should start decelerating; we should have completed cruising phase
            if (timerValue * nextPoint.maxAngularSpeed > Util3309.headingError(headingToNextPoint)) {
                turnState = spinTurnState.decelerating;
                //separate timer to help us decelerate down from a fixed velocity
                lastVelocity = Robot.drive.getLeftEncoderVelocity() / Constants.ENCODER_COUNTS_PER_DEGREE;
                ControlTimer.reset();
                timerValue = 0;
            }
            if (turnState == spinTurnState.decelerating) {
                left = lastVelocity - (nextPoint.angularDecelerationDegreesPerSec2 * timerValue);
            }
            //checks that we have completed deceleration phase and are approaching our tweaking speed
            if (turnState == spinTurnState.decelerating && left < nextPoint.angularCreepSpeed) {
                turnState = spinTurnState.tweaking;
            }
            if (turnState == spinTurnState.tweaking) {
                //check if correction is needed
                if (Math.abs(Util3309.headingError(headingToNextPoint)) < kTweakThreshold) {
                    //spin Turn complete
                    Robot.drive.setLeftRight(ControlMode.PercentOutput, 0, 0);
                    superStateMachine = superState.drivingStraight;
                    turnState = spinTurnState.notStarted;
                }
                //turn right if we undershot
                else if (Util3309.headingError(headingToNextPoint) < 0) {
                    left = nextPoint.angularCreepSpeed;
                }
                //turn left if we overshot
                else {
                    left = -nextPoint.angularCreepSpeed;
                }
            }

            left *= Constants.ENCODER_COUNTS_PER_DEGREE;
            if (superStateMachine == superState.spinTurning ) {
                Robot.drive.setLeftRight(ControlMode.Velocity, left, -left);
            }

            if (debugMode) {
                SmartDashboard.putNumber("Single-motor velocity:", left);
                SmartDashboard.putNumber("Heading error:", Util3309.headingError(headingToNextPoint));
                SmartDashboard.putNumber("Spin turn state:", turnState.val);
            }
    } else if (superStateMachine == superState.drivingStraight) {
            /*
             * Drive Straight
             *
             * Accelerate until the robot has reached the maximum speed specified for that
             * waypoint. Then remain at a constant speed until robot enters the deceleration
             * zone. Finally, decelerate to the slowest allowed speed until destination has
             * been reached.
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
             * if we are stopped, then
             *     calibrate
             *     set state to accelerating
             * if state is accelerating, then
             *     if we still need to accelerate, then
             *         accelerate
             *     else
             *         set state to cruising
             * if state is cruising, then
             *     if we still need to cruise, then
             *         cruise
             *     else
             *         set state to decelerating
             * if state is decelerating, then
             *     if we still need to decelerate, then
             *         decelerate
             *     else
             *         stop the robot and increment nextWaypointIndex
             */
            double encoderTicks = Robot.drive.getEncoderDistance();
            double encoderTicksTraveled = encoderTicks - encoderZeroValue;
            double inchesTraveled = Robot.drive.encoderCountsToInches(encoderTicksTraveled);

            double turnCorrection = Util3309.headingError(headingToNextPoint) * kTurnCorrectionConstant;

            if (state == travelState.stopped) {
                ControlTimer.reset();
                state = travelState.accelerating;
                encoderZeroValue = encoderTicks;
            }
            if (state == travelState.accelerating) {
                speed = nextPoint.linearAccelerationEncoderCountsPerSec2 * ControlTimer.get();
                if (speed > nextPoint.maxLinearSpeedEncoderCountsPerSec) {
                    state = travelState.cruising;
                }
            }
            if (state == travelState.cruising){
                if (inchesBetweenWaypoints - inchesTraveled < speed * kDecelerationConstant) {
                    speed = nextPoint.maxLinearSpeed;
                } else {
                    state = travelState.decelerating;
                    ControlTimer.reset();
                }
            }
            if (state == travelState.decelerating){
                if (inchesTraveled < inchesBetweenWaypoints - nextPoint.linearToleranceInches) {
                    speed = nextPoint.linearAccelerationEncoderCountsPerSec2 * ControlTimer.get();
                    if (speed < nextPoint.linearCreepSpeedEncoderCountsPerSec) {
                        speed = nextPoint.linearCreepSpeed;
                    }
                } else {
                    if (nextWaypointIndex == path.length - 1 && !endRollout) {
                        //We are done with following the path, we have arrived at the destination
                        //Stop the robot
                        speed = 0;
                    }
                    if (inchesTraveled > inchesBetweenWaypoints + nextPoint.linearToleranceInches) {
                        DriverStation.reportError("Traveled too far", true);
                    }
                    nextWaypointIndex++;
                    superStateMachine = superState.stopped;
                }
            }

            if (speed != 0 ) {
                Robot.drive.setArcade(ControlMode.Velocity, speed, turnCorrection);
            } else {
                //If speed is zero, then use PercentOutput so we don't apply brakes
                Robot.drive.setArcade(ControlMode.PercentOutput, 0,0);
            }

            if (debugMode) {
                SmartDashboard.putString("State:", String.valueOf(state));
                SmartDashboard.putNumber("Heading error:", Util3309.headingError(headingToNextPoint));
                SmartDashboard.putNumber("Throttle:", speed);
            }

            //End of Drive straight code
        } else if (superStateMachine == superState.mobileTurning) {

            //Turn on a circle:

        } else if (superStateMachine == superState.stopped) {
            Robot.drive.setLeftRight(ControlMode.PercentOutput, 0, 0);
        }

        if (nextWaypointIndex == path.length) {
            done = true;
        }

        // Example output of variables for debugging purposes - adapt as needed

    }

    @Override
    protected boolean isFinished() {
        return done;
    }
}