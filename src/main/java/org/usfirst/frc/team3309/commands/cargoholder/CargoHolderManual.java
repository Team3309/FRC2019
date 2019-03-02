package org.usfirst.frc.team3309.commands.cargoholder;

import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.OI;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.util.Util;
import org.usfirst.frc.team4322.commandv2.Command;

public class CargoHolderManual extends Command {

    private double power;
    private boolean hasCargo;

    public CargoHolderManual() {
        require(Robot.cargoHolder);
    }

    @Override
    protected void execute() {
//        double powerOut = OI.INSTANCE.getOperatorController().lt();
        double powerIn = OI.INSTANCE.getOperatorController().rt();

        double power = Util.signedMax(0.0, powerIn, Constants.CARGO_LAUNCHER_ROLLERS_MIN_POWER);

        boolean rightClusterPressed = OI.INSTANCE.getRightJoystick().getLeftCluster().getBottomRight().get()
                || OI.INSTANCE.getRightJoystick().getLeftCluster().getBottomLeft().get()
                || OI.INSTANCE.getRightJoystick().getLeftCluster().getBottomCenter().get()
                || OI.INSTANCE.getRightJoystick().getLeftCluster().getTopRight().get()
                || OI.INSTANCE.getRightJoystick().getLeftCluster().getTopLeft().get()
                || OI.INSTANCE.getRightJoystick().getLeftCluster().getTopCenter().get();

        if (rightClusterPressed) {
            power = 1.0;
        } else if (Robot.cargoHolder.hasCargo() && !(power > 0)) {
            power = -3.0 / 12;
        }

        Robot.cargoHolder.setPower(power);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }

}
