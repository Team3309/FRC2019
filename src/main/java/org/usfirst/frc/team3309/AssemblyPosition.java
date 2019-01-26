package org.usfirst.frc.team3309;

public enum AssemblyPosition {

    CARGO_LOW(0.0, false),
    CARGO_MIDDLE(0.0, false),
    CARGO_HIGH(0.0, false),
    PANEL_LOW(0.0, false),
    PANEL_MIDDLE(0.0, false),
    PANEL_HIGH(0.0, false),
    HOME(0.0, false);

    private double liftPosition;
    private boolean forwardArm;

    AssemblyPosition(double liftPosition, boolean forwardArm) {
        this.liftPosition = liftPosition;
        this.forwardArm = forwardArm;
    }

    public double getLiftPosition() {
        return liftPosition;
    }

    public boolean getForwardArm() {
        return forwardArm;
    }

}
