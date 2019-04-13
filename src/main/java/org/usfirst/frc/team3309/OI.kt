package org.usfirst.frc.team3309

import org.usfirst.frc.team3309.commands.Elevate
import org.usfirst.frc.team3309.commands.climber.ClimberManual
import org.usfirst.frc.team3309.commands.drive.DriveSetHighGear
import org.usfirst.frc.team3309.commands.drive.DriveSetLowGear
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
    var leftJoystickLeftClusterGroup = Trigger.on {
        leftJoystick.leftCluster.bottomCenter()
                || leftJoystick.leftCluster.bottomLeft()
                || leftJoystick.leftCluster.bottomRight()
                || leftJoystick.leftCluster.topCenter()
                || leftJoystick.leftCluster.topLeft()
                || leftJoystick.leftCluster.topRight()
    }

    @JvmStatic
    var operatorController: InputXbox = InputXbox(2)

    @JvmStatic
    var operatorCargoIntakeButton = Trigger.on {
        operatorController.rb.get()
    }

    @JvmStatic
    var operatorPanelIntakeButton = Trigger.on {
        operatorController.lb.get()
    }

    init {
        leftJoystick.trigger.whenPressed(DriveSetLowGear())
        leftJoystick.trigger.whenReleased(DriveSetHighGear())

//        rightJoystickRightClusterGroup.whenPressed(router {
//            if (DriverStation.getInstance().isDisabled) {
//                Command.empty
//            } else if (!Robot.cargoHolder.hasCargo() && Robot.panelHolder.hasPanel()) {
//                PlacePanel()
//            } else {
//                Command.empty
//            }
//        })
//        rightJoystickRightClusterGroup.whenReleased(router {
//            if (DriverStation.getInstance().isDisabled) {
//                Command.empty
//            } else if (Robot.panelHolder.extendedPosition == PanelHolder.ExtendedPosition.ExtendedOutwards) {
//                RemoveFinger()
//            } else {
//                Command.empty
//            }
//        })

        operatorController.dPad.down.whenPressed(Elevate(Elevate.Level.Low))
        operatorController.dPad.right.whenPressed(Elevate(Elevate.Level.Middle))
        operatorController.dPad.up.whenPressed(Elevate(Elevate.Level.High))
        operatorController.dPad.left.whenPressed(Elevate(Elevate.Level.CargoShipCargo))

//        operatorPanelIntakeButton.whenPressed(IntakePanelFromStation())
//        operatorPanelIntakeButton.whenReleased(RetractFingerFromFeederStation())

     /*   operatorController.y.whenPressed(IntakePanelFromGround())
        operatorController.y.whenReleased(Command.lambda {
            if (!Robot.panelIntake.hasPanel()) {
               PanelIntakeStopRollersAndBringUp().start()
            }
        })

        operatorCargoIntakeButton.whenPressed(IntakeCargoNear())
        operatorCargoIntakeButton.whenReleased(Command.lambda {
            if (!Robot.cargoHolder.hasCargo()) {
                CargoIntakeActuate(CargoIntake.CargoIntakePosition.Stowed)
            }
        })

        operatorController.b.whenPressed(Command.lambda {
            Robot.cargoIntake.position = CargoIntake.CargoIntakePosition.Extended
        })

        operatorController.a.whenPressed(Command.lambda {
            Robot.cargoIntake.position = CargoIntake.CargoIntakePosition.Stowed
        })*/

        operatorController.leftStick.whenPressed(router {
            if (operatorController.rightStick.get()) {
                ClimberManual()
            } else {
                Command.empty
            }
        })
    }

   /* fun PanelIntakeStopRollersAndBringUp(): Command {
        return group {
            parallel {
                +PanelIntakeSetRollers(0.0)
                +PanelIntakeActuate(PanelIntake.PanelIntakePosition.Up)
            }
        }
    }*/

}
