package org.usfirst.frc.team3309.lib;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

import static java.lang.Math.*;

public class Limelight {

    private NetworkTable table;
    private double xOffsetInches;
    private double zOffsetInches;
    private String limelightName;

    public Limelight(String limelightName, double xOffsetInches, double zOffsetInches)
    {
        table = NetworkTableInstance.getDefault().getTable(limelightName);
        this.limelightName = limelightName;
        this.xOffsetInches = xOffsetInches;
        this.zOffsetInches = zOffsetInches;
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

        // Don't clear the last position when we drop out of 3D mode because
        // the position from the limelight freezes until we regain 3D mode
        // and we need to detect the freeze on subsequent updates.
        lastPos = newPos;

        if (dataValid)
        {
            lastArea = newArea;
            return true;
        }
        else
        {
            lastArea = 0;
            return false;
        }
    }

    // These methods are only valid after getting a true result from has3D()
    private double getRaw3DxInches() { return lastPos[0]; }
    private double getRaw3DzInches() { return lastPos[2]; }
    private double getRaw3DyDegrees() { return lastPos[4]; }
    private double getAdj3DxInches() { return getRaw3DxInches() + xOffsetInches; }
    private double getAdj3DzInches() { return getRaw3DzInches() + zOffsetInches; }

    // Straight line distance to the target
    public double targetInches3D()
    {
        return sqrt(getAdj3DxInches() * getAdj3DxInches() + getAdj3DzInches() * getAdj3DzInches());
    }

    // Angle to the target (negative value means the bot needs to turn to the left)
    public double targetDegrees3D()
    {
        return getRaw3DyDegrees() - toDegrees(atan2(-getAdj3DzInches(), getAdj3DxInches()));
    }

    public void outputToDashboard()
    {
        if (lastArea != 0)
        {
            SmartDashboard.putNumber(limelightName + " targetInches3D", targetInches3D());
            SmartDashboard.putNumber(limelightName + " targetDegrees3D", targetDegrees3D());
        }
    }
}
