package org.usfirst.frc.team3309.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team4322.commandv2.Subsystem;

public class Lift extends Subsystem {

    private WPI_TalonSRX master;
    private WPI_VictorSPX slave;

    public Lift() {
        master = new WPI_TalonSRX(Constants.LIFT_MASTER_TALON_ID);
        slave = new WPI_VictorSPX(Constants.LIFT_SLAVE_VICTOR_ID);

        master.configFactoryDefault();
        master.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,
                0, 0);

        master.config_kP(0, Constants.LIFT_P);
        master.config_kI(0, Constants.LIFT_I);
        master.config_kD(0, Constants.LIFT_D);
        master.setNeutralMode(NeutralMode.Brake);

        slave.follow(master);

        addChild(master);
    }

    public void setGoalPosition(double goalPosition) {
        master.set(ControlMode.Position, goalPosition);
    }

    public double getClosedLoopError() {
        return master.getClosedLoopError();
    }

    public double getEncoderDistance() {
        return master.getSelectedSensorPosition();
    }

    public void setNeutralMode(NeutralMode mode) {
        master.setNeutralMode(mode);
    }

}
