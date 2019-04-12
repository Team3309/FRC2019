package org.usfirst.frc.team3309.commands.drive.auto;

import org.usfirst.frc.team3309.commands.drive.DriveManual;
import org.usfirst.frc.team4322.commandv2.Command;

public class VisionApproach extends Command {

    private boolean approach;

    public VisionApproach(boolean approach) {
        this.approach = approach;
    }

    @Override
    protected void execute() {
        DriveManual.setAutoRun(approach);
    }

    @Override
    protected boolean isFinished() {
        return true;
    }
}
