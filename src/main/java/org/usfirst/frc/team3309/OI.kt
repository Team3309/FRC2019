package org.usfirst.frc.team3309

import org.usfirst.frc.team3309.commands.*
import org.usfirst.frc.team4322.input.InputXbox

class OI {
//    var leftJoystick: InputThrustmaster = InputThrustmaster(0, InputThrustmaster.Hand.Left)
//    var rightJoystick: InputThrustmaster = InputThrustmaster(1, InputThrustmaster.Hand.Right)

    // Converted to Xbox controller for convenience with testing
    var driverController: InputXbox = InputXbox(0)

    init {
        driverController.b.whenPressed(CharacterizeHighGearTurn.to())

        driverController.rb.whenPressed(Drive_SetLowGear())
        driverController.rb.whenReleased(Drive_SetHighGear())
    }


}