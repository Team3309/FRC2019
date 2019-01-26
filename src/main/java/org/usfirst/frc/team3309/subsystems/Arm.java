package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team4322.commandv2.Subsystem;

public class Arm extends Subsystem {

    private WPI_TalonSRX master;

    public Arm() {
        master = new WPI_TalonSRX(Constants.ARM_TALON_ID);

        master.configFactoryDefault();
        master.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,
                0, 0);
        master.config_kP(0, Constants.ARM_P);
        master.config_kI(0, Constants.ARM_I);
        master.config_kD(0, Constants.ARM_D);
        master.setNeutralMode(NeutralMode.Brake);

        addChild(master);
    }

    public void setGoalAngle(double position) {
        master.set(ControlMode.Position, position);
    }

    public double getClosedLoopError() {
        return master.getClosedLoopError();
    }

    public void setNeutralMode(NeutralMode mode) {
        master.setNeutralMode(mode);
    }


}
