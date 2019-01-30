package org.usfirst.frc.team3309.subsystems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.usfirst.frc.team4322.commandv2.Subsystem;

public class Vision extends Subsystem {

    private NetworkTable table;

    public Vision() {
        table = NetworkTableInstance.getDefault().getTable("limelight");
    }

    public double getXError() {
        return table.getEntry("tx").getDouble(0.0);
    }

    public void setPipeline(int pipeline) {
        table.getEntry("pipeline").setDouble(Math.abs(pipeline) < 2 ? Math.abs(pipeline) : 0.0);
    }

    public int getPipeline() {
        return (int) table.getEntry("pipeline").getDouble(0.0);
    }

    public void blink() {
        table.getEntry("ledMode").setDouble(2.0);
    }

}
