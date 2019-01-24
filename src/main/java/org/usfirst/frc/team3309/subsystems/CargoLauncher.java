package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team4322.commandv2.Subsystem;

public class CargoLauncher extends Subsystem {

    private WPI_VictorSPX launcherMotor;

    public CargoLauncher() {
        launcherMotor = new WPI_VictorSPX(Constants.CARGO_LAUNCHER_VICTOR_ID);

        launcherMotor.configFactoryDefault();
    }

    public void setPower(double power) {
        launcherMotor.set(ControlMode.PercentOutput, power);
    }

}
