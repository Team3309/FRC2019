package org.usfirst.frc.team3309.commands.cargoholder;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.Util3309;
import org.usfirst.frc.team4322.commandv2.Command;

public class CargoHolderManual extends Command {

    public CargoHolderManual() {
        require(Robot.cargoHolder);
        setInterruptBehavior(InterruptBehavior.Suspend);
    }

    @Override
    protected void execute() {
        double powerOut = OI.getOperatorController().getLt().axis();
        double powerIn = OI.getOperatorController().getRt().axis();

        double power = Util3309.weirdSignedMax(powerOut, powerIn, Constants.CARGO_LAUNCHER_ROLLERS_MIN_POWER);

        // Hold onto cargo while in holder
        if (Robot.cargoHolder.hasCargo() && !(power > 0)
                && !OI.getRightJoystickRightClusterGroup().get()) {
            power = -0.3;
        } else  if (OI.getRightJoystickRightClusterGroup().get()) {
            power = 1.0;
        }

//        SmartDashboard.putNumber("Cargo holder power", power);
        Robot.cargoHolder.setPower(power);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

}
