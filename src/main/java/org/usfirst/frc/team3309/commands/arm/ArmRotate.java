package org.usfirst.frc.team3309.commands.arm;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.commands.ArmState;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team4322.commandv2.Command;

public class ArmRotate extends Command {

    private double goalAngle;

    private double error = Double.POSITIVE_INFINITY;

    public ArmRotate(double goalAngle) {
        this.goalAngle = Util.clamp(
                goalAngle,
                Constants.ARM_MIN_ANGLE,
                Constants.ARM_MAX_ANGLE);
    }

    @Override
    protected void initialize() {
        Robot.arm.setNeutralMode(NeutralMode.Brake);
        ArmState.setState(ArmState.DONE);
    }

    @Override
    protected void execute() {
        double liftPosition = Robot.lift.getEncoderDistance();

        boolean withinSafeZone = Util.within(liftPosition,
                Constants.LIFT_BEGIN_SAFE_ZONE,
                Constants.LIFT_END_SAFE_ZONE);

        if (withinSafeZone) {
            Robot.arm.setGoalAngle(goalAngle);
            error = Robot.arm.getClosedLoopError();
            ArmState.setState(ArmState.MOVING);
        }

    }

    @Override
    protected boolean isFinished() {
        return Util.withinTolerance(error, goalAngle, Constants.ARM_TOLERANCE);
    }

    @Override
    protected void end() {
        ArmState.setState(ArmState.DONE);
    }
}
