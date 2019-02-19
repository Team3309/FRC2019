package org.usfirst.frc.team3309.subsystems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Subsystem;

public class Vision extends Subsystem {

    private NetworkTable cargoLimelight;
    private NetworkTable panelLimelight;

    public Vision() {
        cargoLimelight = NetworkTableInstance.getDefault().getTable("cargoLimelight");
        panelLimelight = NetworkTableInstance.getDefault().getTable("panelLimelight");
    }

    public double getXError() {
        if (Robot.panelIntake.hasPanel()) {
            return panelLimelight.getEntry("tx").getDouble(0.0);
        } else if (Robot.cargoHolder.hasCargo()) {
            return cargoLimelight.getEntry("tx").getDouble(0.0);
        } else {
            return 0.0;
        }
    }

    public void setPipeline(int pipeline) {
        cargoLimelight.getEntry("pipeline").setDouble(Math.abs(pipeline) < 2 ? Math.abs(pipeline) : 0.0);
    }

    public int getPipeline() {
        return (int) cargoLimelight.getEntry("pipeline").getDouble(0.0);
    }

}
