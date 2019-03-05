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
import org.usfirst.frc.team4322.commandv2.Router
import org.usfirst.frc.team4322.commandv2.Trigger
import org.usfirst.frc.team4322.commandv2.group
import org.usfirst.frc.team4322.input.InputThrustmaster
import org.usfirst.frc.team4322.input.InputXbox

object OI {

    var leftJoystick: InputThrustmaster = InputThrustmaster(0, InputThrustmaster.Hand.Left)
    var rightJoystick: InputThrustmaster = InputThrustmaster(1, InputThrustmaster.Hand.Right)


    var rightJoystickRightClusterGroup : Trigger = object : Trigger() {
        override fun get(): Boolean {
            return rightJoystick.leftCluster.bottomCenter()
                    || rightJoystick.leftCluster.bottomLeft()
                    || rightJoystick.leftCluster.bottomRight()
                    || rightJoystick.leftCluster.topCenter()
                    || rightJoystick.leftCluster.topLeft()
                    || rightJoystick.leftCluster.topRight()
        }
    }
    var operatorController: InputXbox = InputXbox(2)

    private var hadCargo: Boolean = false

    init {
        leftJoystick.trigger.whenPressed(DriveSetLowGear())
        leftJoystick.trigger.whenReleased(DriveSetHighGear())

//        rightJoystick.knobCluster.bottom.whileHeld(PlacePanel())
//        rightJoystick.knobCluster.bottom.whenReleased(RemoveFinger())

        rightJoystickRightClusterGroup.whileHeld(group {
            sequential {
                router {
                    if (DriverStation.getInstance().isDisabled) {
                        lambda {  }
                    }
                    else if (Robot.panelHolder.hasPanel() || Robot.panelHolder.extendedPosition == PanelHolder.ExtendedPosition.ExtendedOutwards)
                    {
                        PlacePanel()
                    }
                    else
                    {
                        CargoHolderSetRollers(1.0)
                    }
                }
            }
        })
        rightJoystickRightClusterGroup.whenReleased(group {
            sequential{
                router{
                    if (DriverStation.getInstance().isDisabled) {
                        lambda {  }
                    }
                    else if (Robot.panelHolder.extendedPosition == PanelHolder.ExtendedPosition.ExtendedOutwards)
                    {
                        RemoveFinger()
                    } else {
                        CargoHolderSetRollers(0.0)
                    }
                }
            }
        })

        operatorController.dPad.down.whenPressed(Elevate(Elevate.Level.Low))
        operatorController.dPad.right.whenPressed(Elevate(Elevate.Level.Middle))
        operatorController.dPad.up.whenPressed(Elevate(Elevate.Level.High))
        operatorController.dPad.left.whenPressed(Elevate(Elevate.Level.CargoShipCargo))

//        operatorController.start.whenPressed(ReleaseLatch(Climber.ClimberLatchPosition.Released))

        operatorController.lb.whenPressed(IntakePanelFromStation())
        operatorController.lb.whenReleased(RetractFingerFromFeederStation())

        operatorController.rb.whileHeld(IntakeCargoNear())
        operatorController.rb.whenReleased(Command.lambda {
            if (!Robot.cargoHolder.hasCargo()) {
                CargoIntakeActuate(CargoIntake.CargoIntakePosition.Stowed)
            }
        })

        operatorController.b.whenPressed(Command.lambda {
            Robot.cargoIntake.setPosition(CargoIntake.CargoIntakePosition.Extended)
        })

        operatorController.a.whenPressed(Command.lambda {
            Robot.cargoIntake.setPosition(CargoIntake.CargoIntakePosition.Stowed)
        })

        //        operatorController.start.whenPressed(ReleaseLatch(Climber.ClimberLatchPosition.Released))
//        operatorController.back.whenPressed(WinchClimber(Climber.ClimberAngle.Extended))

/*            operatorController.rb.whenPressed(Command.lambda {
                Robot.panelHolder.setJointedSolenoid(PanelHolder.JointedPosition.PointingOutwards)
            })
            operatorController.rb.whenReleased(Command.lambda {
                Robot.panelHolder.setJointedSolenoid(PanelHolder.JointedPosition.Vertical)
            })*/
/*
            operatorController.lb.whenPressed(Command.lambda {
                Robot.panelHolder.setExtendingSolenoid(PanelHolder.ExtendedPosition.ExtendedOutwards)
            })
            operatorController.lb.whenReleased(Command.lambda {
                Robot.panelHolder.setExtendingSolenoid(PanelHolder.ExtendedPosition.RetractedInwards)
            })
*/
        /*        operatorController.dPad.down.whenPressed(Elevate(Elevate.Level.Low))
        operatorController.dPad.right.whenPressed(Elevate(Elevate.Level.Middle))
        operatorController.dPad.up.whenPressed(Elevate(Elevate.Level.High))
        operatorController.dPad.left.whenPressed(Elevate(Elevate.Level.Home))

        operatorController.x.whenPressed(IntakeCargoNear())

        operatorController.y.whileHeld(IntakePanelFromStation())
        operatorController.y.whenReleased(Command.lambda {
            if (!Robot.panelHolder.hasPanel())
                PanelHolderGoHome().start()
        })
        operatorController.rb.whenPressed(Extended())

        operatorController.lb.whenPressed(Elevate(Elevate.Level.Test))*/
    }

}