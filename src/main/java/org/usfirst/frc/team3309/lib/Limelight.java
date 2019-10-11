package org.usfirst.frc.team3309.lib;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.Constants;

import static java.lang.Math.*;
import static org.usfirst.frc.team3309.Constants.kPanelHolderBiasInchesX;

public class Limelight {

    private NetworkTable table;
    private double xOffsetInches;
    private double zPlacementOffsetInches;
    private double zRotationOffsetInches;
    private String limelightName;
    private Timer latency3D = new Timer();

    public Limelight(String limelightName, double xOffsetInches,
                     double zPlacementOffsetInches, double zRotationOffsetInches)
    {
        table = NetworkTableInstance.getDefault().getTable(limelightName);
        this.limelightName = limelightName;
        this.xOffsetInches = xOffsetInches;
        this.zPlacementOffsetInches = zPlacementOffsetInches;
        this.zRotationOffsetInches = zRotationOffsetInches;
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

    private boolean had3D = false;

    public boolean getHad3D() { return had3D; }

    // Cache all 3D values simultaneously and check for validity.
    // Only call once each time through the processing loop.
    public boolean has3D()
    {
        boolean dataValid = false;

        double[] defaultValue = new double [0];
        double[] newPos = table.getEntry("camtran").getDoubleArray(defaultValue);

        // check that we are locked on a target and have a full set of 3D values
        if (hasTarget() && newPos.length == 6)
        {
            // Check for non-zero 3D values.
            // The values can be zero when the target is partially obscured,
            // such as at the loading station where the bottom corners of the tape
            // are blocked by the panel.
            // The values can also be zero when the target is too far away (~90 inches).
            for (int i = 0; i < newPos.length; i++)
            {
                if (newPos[i] != 0)
                {
                    dataValid = true;
                    break;
                }
            }

            if (dataValid)
            {
                // check if 3D values have been refreshed so we know how much encoder history to use
                for (int i = 0; i < newPos.length; i++) {
                    if (i >= lastPos.length || newPos[i] != lastPos[i]) {
                        latency3D.reset();
                        latency3D.start();
                        break;
                    }
                }
            }
        }

        lastPos = newPos;
        had3D = dataValid;

        if (dataValid)
        {
            return true;
        }
        else
        {
            latency3D.reset();
            return false;
        }
    }

    // These methods are only valid after getting a true result from has3D()
    public double getLatency3D() { return latency3D.get(); }
    private double getRaw3DxInches() { return lastPos[0]; }
    private double getRaw3DzInches() { return lastPos[2]; }
    private double limelightToTargetRad() { return toRadians(lastPos[4]); }

    private double limelightCenterInchesX() {
        return getRaw3DxInches() + cos(limelightToTargetRad()) * xOffsetInches;
    }

    private double limelightCenterInchesZ() {
        return getRaw3DzInches() + sin(limelightToTargetRad()) * xOffsetInches;
    }

    private double rotationCenterInchesX() {
        return limelightCenterInchesX() - sin(limelightToTargetRad()) * zRotationOffsetInches;
    }

    private double rotationCenterInchesZ() {
        return limelightCenterInchesZ() + cos(limelightToTargetRad()) * zRotationOffsetInches;
    }

    public double rotationCenterToTargetRad() {
        return limelightToTargetRad() -
                atan2(rotationCenterInchesX() + kPanelHolderBiasInchesX, -rotationCenterInchesZ());
    }

    private double panelInchesX() {
        return limelightCenterInchesX() - sin(limelightToTargetRad()) * zPlacementOffsetInches;
    }

    private double panelInchesZ() {
        return limelightCenterInchesZ() + cos(limelightToTargetRad()) * zPlacementOffsetInches;
    }

    // Straight line distance to the target
    public double targetDistInches3D()
    {
        double panelX = panelInchesX();
        double panelZ = panelInchesZ();
        return -sqrt(panelX * panelX + panelZ * panelZ) * Math.signum(panelZ);
    }

    public void outputToDashboard()
    {
        if (has3D())
        {
            SmartDashboard.putNumber(limelightName + " panelInchesZ", panelInchesZ());
            SmartDashboard.putNumber(limelightName + " targetDistInches3D", targetDistInches3D());
            SmartDashboard.putNumber(limelightName + " limelightToTargetDegrees",
                    toDegrees(limelightToTargetRad()));
            SmartDashboard.putNumber(limelightName + " rotationCenterToTargetDegrees",
                    toDegrees(rotationCenterToTargetRad()));
            SmartDashboard.putNumber(limelightName + " limelightCenterInchesX", limelightCenterInchesX());
            SmartDashboard.putNumber(limelightName + " limelightCenterInchesZ", limelightCenterInchesZ());
            SmartDashboard.putNumber(limelightName + " rotationCenterInchesX", rotationCenterInchesX());
            SmartDashboard.putNumber(limelightName + " rotationCenterInchesZ", rotationCenterInchesZ());
        }
    }
}
