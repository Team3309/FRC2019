package org.usfirst.frc.team3309.subsystems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.usfirst.frc.team3309.lib.PIDController;
import org.usfirst.frc.team4322.commandv2.Subsystem;

public class Vision extends Subsystem {

    private NetworkTable table;

    private PIDController turnController = new PIDController("turn", 0.0, 0.0, 0.0);

    public Vision() {
        table = NetworkTableInstance.getDefault().getTable("limelight");
    }

    public double getAngularPower() {
        return turnController.update(getX());
    }

    public double getX() {
        return table.getEntry("tx").getDouble(0.0);
    }

    public void setPipeline(int pipeline) {
        table.getEntry("pipeline").setDouble(pipeline);
    }

    public int getPipeline() {
        return (int) table.getEntry("pipeline").getDouble(0.0);
    }


}