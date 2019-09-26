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

    private double[] lastPos = new double [0];
    private double lastArea = 0;

    // Cache all 3D values simultaneously and check for validity.
    // Only call once each time through the processing loop.
    public boolean has3D()
    {
        boolean dataValid = false;

        double[] defaultValue = new double [0];
        double[] newPos = table.getEntry("camtran").getDoubleArray(defaultValue);
        double newArea = getArea();

        // check that we are locked on a target and have a full set of 3D values
        if (hasTarget() && newPos.length == 6)
        {
            if (newArea == lastArea)
            {
                // no frame update since last call, use prior 3D position
                dataValid = true;
            }
            else
            {
                // frame update received, check that 3D values are not frozen
                for (int i = 0; i < newPos.length; i++)
                {
                    if (i >= lastPos.length || newPos[i] != lastPos[i])
                    {
                        dataValid = true;
                        break;
                    }
                }
            }
        }
        if (dataValid)
        {
            lastPos = newPos;
            lastArea = newArea;
            return true;
        }
        else
        {
            lastPos = new double [0];
            lastArea = 0;
            return false;
        }
    }

    // These methods are only valid after getting a true result from has3D()
    public double get3dX() { return lastPos[0]; }
    public double get3dZ() { return lastPos[2]; }
    public double get3dDegrees() { return lastPos[4]; }
}
