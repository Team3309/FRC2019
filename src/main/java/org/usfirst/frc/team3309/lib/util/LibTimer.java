package org.usfirst.frc.team3309.lib.util;

import edu.wpi.first.wpilibj.Timer;

public class LibTimer extends Timer {

    private double timeToBeCompleted;

    private boolean isConditionMaintained;

    public LibTimer() {
    }

    public LibTimer(double timeToBeCompleted) {
        this.timeToBeCompleted = timeToBeCompleted;
    }

    public boolean isConditionMaintained(boolean isTrue) {
        if (isTrue) {
            if (isConditionMaintained && this.get() > this.timeToBeCompleted) {
                return true;
            } else if (!isConditionMaintained) {
                this.start();
                isConditionMaintained = true;
            }
        } else {
            isConditionMaintained = false;
            this.stop();
            this.reset();
        }
        return false;
    }

    public static void delaySec(double sec) {
        Timer.delay(sec);
    }

    public static void delayMs(double ms) {
        Timer.delay(ms / 1000);
    }

}
