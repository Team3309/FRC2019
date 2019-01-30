package org.usfirst.frc.team3309

import org.usfirst.frc.team3309.commands.TogglePipeline
import org.usfirst.frc.team3309.commands.drive.DriveSetHighGear
import org.usfirst.frc.team3309.commands.drive.DriveSetLowGear
import org.usfirst.frc.team4322.input.InputThrustmaster
import org.usfirst.frc.team4322.input.InputXbox

object OI {

    var leftJoystick: InputThrustmaster = InputThrustmaster(0, InputThrustmaster.Hand.Left)
    var rightJoystick: InputThrustmaster = InputThrustmaster(1, InputThrustmaster.Hand.Right)

    var operatorController: InputXbox = InputXbox(2)

    init {
        leftJoystick.trigger.whenPressed(DriveSetLowGear())
        leftJoystick.trigger.whenReleased(DriveSetHighGear())

        rightJoystick.knobCluster.bottom.whenPressed(TogglePipeline())
    }

}