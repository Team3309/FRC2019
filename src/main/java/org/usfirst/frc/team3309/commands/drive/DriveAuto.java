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
        ControlTimer.reset();
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
             * decelerate to the slowest allowed speed until destination has been reached.
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
             *     if we are still accelerating, then
             *         accelerate
             *     else
             *         set state to cruising
             * else if state is cruising, then
             *     if we are still cruising, then
             *         cruise
             *     else
             *         set state to decelerating
             * else if state is decelerating, then
             *     if we are still decelerating, then
             *         decelerate
             *     else
             *         stop the robot and increment nextWaypointIndex
             */
            turn = 0;
            if (state == travelState.accelerating) {
                if (speed < nextPoint.maxLinearSpeedEncoderCountsPerSec) {
                    speed += nextPoint.linearAccelerationEncoderCountsPerSec2 * ControlTimer.get();
                } else {
                    state = travelState.cruising;
                }
            } else if (state == travelState.cruising){
                if (inchesFromWaypoints - inchesTraveled < speed * nextPoint.decelerationConstant) {
                    speed = nextPoint.maxLinearSpeed;
                } else {
                    state = travelState.decelerating;
                    ControlTimer.reset();
                }
            } else if (state == travelState.decelerating){
                if (inchesTraveled < inchesFromWaypoints) {
                    speed -= nextPoint.linearAccelerationEncoderCountsPerSec2 * ControlTimer.get();
                    if (speed < nextPoint.linearCreepSpeedEncoderCountsPerSec) {
                        speed = nextPoint.linearCreepSpeed;
                    }
                } else {
                    //Stop robot and start moving to next waypoint
                    speed = 0;
                    //nextWaypointIndex++;
                    state = travelState.turningInPlace;
                    Robot.drive.zeroEncoders();
                    ControlTimer.reset();
                }
            }

        } else if (nextPoint.turnRadiusInches !=0 && nextPoint.crossfieldInches + nextPoint.downfieldInches != 0) {
            state = travelState.turning;

            //Turn to the next point. We may be able to use the Cheesy Drive algorithm for this.


            //Turn on a circle:

        } else if (nextPoint.turnRadiusInches == 0 && nextPoint.crossfieldInches + nextPoint.downfieldInches == 0) {
            //Turn in place:
            //  Accelerate turning till we reach our turn cruising speed.
            //  Turn at cruising speed till we approach
            //  Decelerate turning speed till we reach tweaking speed.
            //  Tweak heading until it is within a healthy margin of error.
            //  Allow the robot to drive again.
            //  Reset all sensors for next operation.

            state = travelState.turningInPlace;

            double tweakThreshold = 0.001;
            lastVelocity = Robot.drive.getEncoderVelocity();
            ControlTimer.start();
            double timerValue = ControlTimer.get();

            //checks that this is the start of auto; timer should be started and robot should not have
            //been previously started
            if (timerValue > 0 && turnState == spinTurnState.notStarted) {
                turnState = spinTurnState.accelerating;
            }
            //checks whether we should start cruising; we should have finished our acceleration phase
            //and we should be approaching our cruise velocity
            else if (turnState == spinTurnState.accelerating &&
                    timerValue * nextPoint.angularAccelerationDegreesPerSec2 > nextPoint.maxAngularSpeed) {
                turnState = spinTurnState.cruising;
            }
            //checks whether we should start decelerating; we should have completed cruising phase
            else if (turnState == spinTurnState.cruising &&
                    timerValue * nextPoint.maxAngularSpeed  > nextPoint.maxAngularSpeed) {
                turnState = spinTurnState.decelerating;
                //separate timer to help us decelerate down from a fixed velocity
                lastVelocity = Robot.drive.getEncoderVelocity();
                ControlTimer.reset();
                timerValue = 0;
            }
            //checks that we have completed deceleration phase and are approaching our tweaking speed
            else if (turnState == spinTurnState.decelerating &&
                    timerValue * nextPoint.angularDecelerationDegreesPerSec2 < nextPoint.angularCreepSpeed) {
                turnState = spinTurnState.tweaking;
            }
            //defaults to straight driving, making sure that the timer is primed for next spin turn
            else {
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
                //turn left if we overshot
                if (Robot.drive.getAngularPosition() > headingToNextPoint &&
                        Math.abs(Robot.drive.getAngularPosition()-headingToNextPoint) > tweakThreshold) {
                    left = -nextPoint.angularCreepSpeed;
                }
                //turn right if we undershot
                else if (Robot.drive.getAngularPosition() < headingToNextPoint &&
                        Math.abs(Robot.drive.getAngularPosition()-headingToNextPoint) > tweakThreshold) {
                    left = nextPoint.angularCreepSpeed;
                }
                //stop correcting
                else if (Math.abs(Robot.drive.getAngularPosition()-headingToNextPoint) < tweakThreshold) {
                    Robot.drive.setLeftRight(ControlMode.PercentOutput, 0, 0);
                    ControlTimer.stop();
                    ControlTimer.reset();
                    state = travelState.stopped;
                    turnState = spinTurnState.notStarted;
                }
            } else {
                Robot.drive.setLeftRight(ControlMode.PercentOutput, 0, 0);
            }

            Robot.drive.setLeftRight(ControlMode.Velocity, left, -left);
            Robot.drive.zeroNavx();
        
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
