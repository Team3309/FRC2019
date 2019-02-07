package org.usfirst.frc.team3309.commands;

import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team3309.subsystems.Elevator;
import org.usfirst.frc.team4322.commandv2.Command;

public class Elevate extends Command {

    private double prevTime;

    private Elevator.CarriagePosition carriagePosition;
    private Elevator.WristFacing wristFacing;

    public Elevate(Elevator.CarriagePosition carriagePosition,
                   Elevator.WristFacing wristFacing) {
        require(Robot.elevator);
        this.carriagePosition = carriagePosition;
        this.wristFacing = wristFacing;
    }

    public Elevate(Elevator.CarriagePosition carriagePosition) {
        this(carriagePosition, null);
    }

    @Override
    protected void initialize() {
        prevTime = Timer.getFPGATimestamp();
    }

    @Override
    protected void execute() {
        double curTime = Timer.getFPGATimestamp();
        double deltaTime = curTime - prevTime;

        // clamp to a slightly lower level
        double offset = Constants.LIFT_NUDGE_SPEED * deltaTime * OI.INSTANCE.getOperatorController().getLeftStick().y();
        double goalPosition = Util.clamp(carriagePosition.getLiftPosition() + offset, 0.0, 0.8);

        Robot.elevator.setPosition(goalPosition, wristFacing);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

}
