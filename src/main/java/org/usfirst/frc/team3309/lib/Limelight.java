package org.usfirst.frc.team3309.lib;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;

public class Limelight {

    private NetworkTable table;

    public Limelight(String tablename) {
        table = NetworkTableInstance.getDefault().getTable(tablename);
    }

    public boolean hasTarget() {
        return get("tv") == 1.0;
    }

    public double getTx() {
        return get("tx");
    }

    public double getTy() {
        return get("ty");
    }

    public double getSkew() {
        return get("ts");
    }

    public double getArea() {
        return get("ta");
    }

    public double get(String entryName) {
        return table.getEntry(entryName).getDouble(0.0);
    }

    public void setPipeline(int pipeline) {
        table.getEntry("pipeline").setDouble(pipeline);
    }

    public int getPipeline() {
        return (int) table.getEntry("pipeline").getDouble(0.0);
    }

    public void setLed(LEDMode mode) {
        table.getEntry("ledMode").setDouble(mode.value);
    }

    public void setCamMode(CamMode camMode) {
        if (camMode == CamMode.VisionProcessor) {
            table.getEntry("camMode").setDouble(0.0);
        } else if (camMode == CamMode.DriverCamera) {
            table.getEntry("camMode").setDouble(1.0);
        }
    }

    public enum LEDMode {
        Off(1),
        Blink(2),
        On(3);

        private int value;

        LEDMode(int value) {
            this.value = value;
        }

        public int get() {
            return value;
        }

    }

    public enum CamMode {
        VisionProcessor,
        DriverCamera
    }

}