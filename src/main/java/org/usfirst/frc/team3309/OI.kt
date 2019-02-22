package org.usfirst.frc.team3309

import org.usfirst.frc.team3309.commands.*
import org.usfirst.frc.team3309.commands.drive.DriveSetHighGear
import org.usfirst.frc.team3309.commands.drive.DriveSetLowGear
import org.usfirst.frc.team3309.commands.panelintake.PanelIntakeActuate
import org.usfirst.frc.team3309.subsystems.CargoIntake
import org.usfirst.frc.team3309.subsystems.Climber
import org.usfirst.frc.team3309.subsystems.Elevator
import org.usfirst.frc.team3309.subsystems.PanelHolder
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.input.InputThrustmaster
import org.usfirst.frc.team4322.input.InputXbox

object OI {

    var leftJoystick: InputThrustmaster = InputThrustmaster(0, InputThrustmaster.Hand.Left)
    var rightJoystick: InputThrustmaster = InputThrustmaster(1, InputThrustmaster.Hand.Right)

    var operatorController: InputXbox = InputXbox(2)

    init {
        leftJoystick.trigger.whenPressed(DriveSetLowGear())
        leftJoystick.trigger.whenReleased(DriveSetHighGear())

//        rightJoystick.knobCluster.bottom.whenPressed(TogglePipeline())
//
//        operatorController.dPad.down.whenPressed(Elevate(Elevate.Level.Low))
//        operatorController.dPad.right.whenPressed(Elevate(Elevate.Level.Middle))
//        operatorController.dPad.up.whenPressed(Elevate(Elevate.Level.High))
//        operatorController.dPad.left.whenPressed(Elevate(Elevate.Level.CargoShipCargo))
//
//        operatorController.b.whenPressed(IntakeCargoNear())
//        operatorController.a.whenPressed(IntakePanel())
//        operatorController.y.whenPressed(Elevate(Elevate.Level.Home))
//
        operatorController.rb.whenPressed(Command.lambda {
            Robot.panelHolder.setJointedSolenoid(PanelHolder.JointedPosition.PointingOutwards)
        })
        operatorController.rb.whenReleased(Command.lambda {
            Robot.panelHolder.setJointedSolenoid(PanelHolder.JointedPosition.Vertical)
        })

        operatorController.lb.whenPressed(Command.lambda {
            Robot.panelHolder.setExtendingSolenoid(PanelHolder.ExtendedPosition.ExtendedOutwards)
        })
        operatorController.lb.whenReleased(Command.lambda {
            Robot.panelHolder.setExtendingSolenoid(PanelHolder.ExtendedPosition.RetractedInwards)
        })

        operatorController.b.whenPressed(Command.lambda {
            Robot.cargoIntake.setPosition(CargoIntake.CargoIntakePosition.Extended)
        })
        operatorController.b.whenReleased(Command.lambda {
            Robot.cargoIntake.setPosition(CargoIntake.CargoIntakePosition.Stowed)
        })
        //        operatorController.rb.whenPressed(PanelHolderActuate(PanelHolder.PanelHolderPosition.PlacePanel))
//        operatorController.rb.whenReleased(PanelHolderActuate(PanelHolder.PanelHolderPosition.ReleasePanel))
////
//        operatorController.start.whenPressed(ReleaseLatch(Climber.ClimberLatchPosition.Released))
//        operatorController.back.whenPressed(WinchClimber(Climber.ClimberAngle.Extended))
    }

}