package org.usfirst.frc.team3309.commands.drive;

import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.VisionHelper;
import org.usfirst.frc.team3309.lib.util.DriveSignal;
import org.usfirst.frc.team3309.lib.util.Util3309;
import org.usfirst.frc.team4322.commandv2.Command;

public class DriveVisionPlace extends Command {
    private Timer settleTimer = new Timer();
    private boolean isSettling = false;

    public DriveVisionPlace() {
        require(Robot.drive);
        setInterruptBehavior(InterruptBehavior.Terminate);
    }

    enum AutoStates {
        nothing,
        placingPanel,
        removingFinger
    }
    AutoStates autoState = AutoStates.nothing;

    @Override
    protected void initialize() {
        super.initialize();
        VisionHelper.start();
    }

    @Override
    protected void execute() {
        double area = Robot.vision.getTargetArea();
        Robot.vision.load3D();
        DriveSignal signal;
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
