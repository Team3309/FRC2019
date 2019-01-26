package org.usfirst.frc.team3309.commands;

public enum ArmState {

    MOVING,
    DONE;

    private static ArmState mState;

    public static void setState(ArmState state) {
        mState = state;
    }

    public static ArmState getState() {
        return mState;
    }

}