package org.usfirst.frc.team3309.commands.lift;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.commands.ArmState;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team4322.commandv2.Command;

public class LiftElevate extends Command {

    private double goalPosition;

    private double prevTime;

    public LiftElevate(double goalPosition) {
        this.goalPosition = goalPosition;
    }

    @Override
    protected void initialize() {
        Robot.lift.setNeutralMode(NeutralMode.Brake);
        prevTime = Timer.getFPGATimestamp();
    }

    @Override
    protected void execute() {
        double curTime = Timer.getFPGATimestamp();

        double deltaTime = curTime - prevTime;

        double offset = Constants.LIFT_NUDGE_SPEED * deltaTime *
                OI.INSTANCE.getOperatorController().getLeftStick().y();

        double adjustedGoalPosition = Util.clamp(
                goalPosition + offset,
                Constants.LIFT_MIN_POSITION,
                Constants.LIFT_MAX_POSITION);

        if (ArmState.getState() == ArmState.MOVING) {
            adjustedGoalPosition = Util.clamp(adjustedGoalPosition,
                    Constants.LIFT_BEGIN_SAFE_ZONE,
                    Constants.LIFT_END_SAFE_ZONE);
        }

        Robot.lift.setGoalPosition(adjustedGoalPosition);

        prevTime = curTime;
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

}
