package org.usfirst.frc.team3309

import edu.wpi.first.wpilibj.DriverStation
import org.usfirst.frc.team3309.commands.*
import org.usfirst.frc.team3309.commands.cargoholder.CargoHolderSetRollers
import org.usfirst.frc.team3309.commands.cargointake.CargoIntakeActuate
import org.usfirst.frc.team3309.commands.drive.DriveSetHighGear
import org.usfirst.frc.team3309.commands.drive.DriveSetLowGear
import org.usfirst.frc.team3309.subsystems.CargoIntake
import org.usfirst.frc.team3309.subsystems.Climber
import org.usfirst.frc.team3309.subsystems.PanelHolder
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.Trigger
import org.usfirst.frc.team4322.commandv2.router
import org.usfirst.frc.team4322.input.InputThrustmaster
import org.usfirst.frc.team4322.input.InputXbox

object OI {

    @JvmStatic
    var leftJoystick: InputThrustmaster = InputThrustmaster(0, InputThrustmaster.Hand.Left)

    @JvmStatic
    var rightJoystick: InputThrustmaster = InputThrustmaster(1, InputThrustmaster.Hand.Right)

    @JvmStatic
    var rightJoystickRightClusterGroup = Trigger.on {
        rightJoystick.rightCluster.bottomCenter()
                || rightJoystick.rightCluster.bottomLeft()
                || rightJoystick.rightCluster.bottomRight()
                || rightJoystick.rightCluster.topCenter()
                || rightJoystick.rightCluster.topLeft()
                || rightJoystick.rightCluster.topRight()
    }

    @JvmStatic
    var operatorController: InputXbox = InputXbox(2)

    init {
        leftJoystick.trigger.whenPressed(DriveSetLowGear())
        leftJoystick.trigger.whenReleased(DriveSetHighGear())

        rightJoystickRightClusterGroup.whileHeld(router {
            if (DriverStation.getInstance().isDisabled) {
                Command.empty
            } else if (Robot.panelHolder.hasPanel()) {
                PlacePanel()
            } else {
                CargoHolderSetRollers(1.0)
            }
        })
        rightJoystickRightClusterGroup.whenReleased(router {
            if (DriverStation.getInstance().isDisabled) {
                Command.empty
            } else if (Robot.panelHolder.extendedPosition == PanelHolder.ExtendedPosition.ExtendedOutwards) {
                RemoveFinger()
            } else {
                CargoHolderSetRollers(0.0)
            }
        })

        operatorController.dPad.down.whenPressed(Elevate(Elevate.Level.Low))
        operatorController.dPad.right.whenPressed(Elevate(Elevate.Level.Middle))
        operatorController.dPad.up.whenPressed(Elevate(Elevate.Level.High))
        operatorController.dPad.left.whenPressed(Elevate(Elevate.Level.CargoShipCargo))

        operatorController.lb.whenPressed(IntakePanelFromStation())
        operatorController.lb.whenReleased(RetractFingerFromFeederStation())

        operatorController.rb.whileHeld(IntakeCargoNear())
        operatorController.rb.whenReleased(Command.lambda {
            if (!Robot.cargoHolder.hasCargo()) {
                CargoIntakeActuate(CargoIntake.CargoIntakePosition.Stowed)
            }
        })

        operatorController.b.whenPressed(Command.lambda {
            Robot.cargoIntake.position = CargoIntake.CargoIntakePosition.Extended
        })

        operatorController.a.whenPressed(Command.lambda {
            Robot.cargoIntake.position = CargoIntake.CargoIntakePosition.Stowed
        })

    }

}