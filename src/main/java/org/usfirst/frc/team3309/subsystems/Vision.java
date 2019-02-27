package org.usfirst.frc.team3309.subsystems;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableInstance;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team4322.commandv2.Subsystem;

public class Vision extends Subsystem {

    public static Limelight cargoLimelight;
    public static Limelight panelLimelight;

    public Vision() {
        cargoLimelight = new Limelight("cargoLimelight");
        panelLimelight = new Limelight("panelLimelight");
        cargoLimelight.setLed(Limelight.LEDMode.Off);
        panelLimelight.setLed(Limelight.LEDMode.Off);
    }

    public double getAngle(Limelight limelight) {
       return limelight.getTx();
    }

    public void setLed(Limelight limelight, Limelight.LEDMode mode) {
        limelight.setLed(mode);
    }

    public static class Limelight {

        private NetworkTable table;

        public Limelight(String tablename) {
            table = NetworkTableInstance.getDefault().getTable(tablename);
        }

        public double getTx() {
            return get("tx");
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

        public enum LEDMode {
            Off(2),
            On(3);

            private int value;

            LEDMode(int value) {
                this.value = value;
            }

            public int get() {
                return value;
            }

        }

    }

}
