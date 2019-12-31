package org.usfirst.frc.team3309.commands.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.CheesyDriveHelper;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team3309.subsystems.Drive;
import org.usfirst.frc.team4322.commandv2.Command;



public class DriveAuto extends Command {

    private CheesyDriveHelper cheesyDrive = new CheesyDriveHelper();

    private enum travelState {
        stopped,
        accelerating,
        cruising, //Moving at a set speed
        decelerating,
        rolling, //Moving through momentum
        turning,
        turningInPlace,
    }

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
    }

    @Override
    protected void execute() {
        double heading = Robot.drive.getAngularPosition() % 360;

        Waypoint priorPoint = path[nextWaypointIndex - 1];
        Waypoint nextPoint = path[nextWaypointIndex];

        double headingToNextPoint = Math.toDegrees(Math.tan((priorPoint.downfieldInches - nextPoint.downfieldInches)/
                (priorPoint.crossfieldInches - nextPoint.crossfieldInches)));

        double inchesFromWaypoints = Util.distanceFormula(priorPoint.downfieldInches, priorPoint.crossfieldInches,
                nextPoint.downfieldInches, nextPoint.crossfieldInches);

        double inchesTraveled = Robot.drive.encoderCountsToInches(Robot.drive.getEncoderDistance());

        double speed = 0;
        double turn = 0;

        if (nextPoint.turnRadiusInches == 0) {
            turn = 0;
            if (state == travelState.accelerating) {
                if (inchesTraveled < inchesFromWaypoints / 10) {
                    if (speed <= nextPoint.maxTravelSpeed) {
                        speed += nextPoint.maxSpeedChange;
                    }
                } else {
                    state = travelState.cruising;
                }
            } else if (state == travelState.cruising){
                if (inchesTraveled < inchesFromWaypoints / 10 * 9) {
                    speed = nextPoint.maxTravelSpeed;
                } else {
                    state = travelState.decelerating;
                }
            } else if (state == travelState.decelerating){
                if (inchesTraveled <= inchesFromWaypoints) {
                    speed -= nextPoint.maxSpeedChange;
                } else {
                    speed = 0;
                    nextWaypointIndex++;
                    state = travelState.stopped;
                }
            }
        } else if (nextPoint.turnRadiusInches !=0 && nextPoint.crossfieldInches + nextPoint.downfieldInches != 0) {
            state = travelState.turning;
            //Turn to the next point. We may be able to use the Cheesy Drive algorithm for this.
        } else if (nextPoint.turnRadiusInches !=0 && nextPoint.crossfieldInches + nextPoint.downfieldInches == 0) {
            //Turn in place
        }

        Robot.drive.setArcade(ControlMode.Velocity, speed, turn);
    }

    @Override
    protected boolean isFinished() {
        return done;
    }
}
