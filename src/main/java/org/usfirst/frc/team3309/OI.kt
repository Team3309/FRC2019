package org.usfirst.frc.team3309

import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.DriverStation
import org.usfirst.frc.team3309.commands.*
import org.usfirst.frc.team3309.commands.cargointake.CargoIntakeActuate
import org.usfirst.frc.team3309.commands.climber.ClimberManual
import org.usfirst.frc.team3309.commands.drive.*
import org.usfirst.frc.team3309.subsystems.CargoIntake
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
    var rightJoystickLeftClusterGroup = Trigger.on {
        rightJoystick.leftCluster.bottomCenter()
                || rightJoystick.leftCluster.bottomLeft()
                || rightJoystick.leftCluster.bottomRight()
                || rightJoystick.leftCluster.topCenter()
                || rightJoystick.leftCluster.topLeft()
                || rightJoystick.leftCluster.topRight()
    }

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
    var leftJoystickRightClusterGroup = Trigger.on {
        leftJoystick.rightCluster.bottomCenter()
                || leftJoystick.rightCluster.bottomLeft()
                || leftJoystick.rightCluster.bottomRight()
                || leftJoystick.rightCluster.topCenter()
                || leftJoystick.rightCluster.topLeft()
                || leftJoystick.rightCluster.topRight()
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

        rightJoystickRightClusterGroup.whenPressed(router {
            if (DriverStation.getInstance().isDisabled) {
                Command.empty
            } else if (!Robot.cargoHolder.hasCargo() && Robot.panelHolder.hasPanel()) {
                PlacePanel()
            } else {
                Command.empty
            }
        })

        rightJoystickRightClusterGroup.whenReleased(router {
            if (DriverStation.getInstance().isDisabled) {
                Command.empty
            } else if (Robot.panelHolder.extendedPosition == PanelHolder.ExtendedPosition.ExtendedOutwards) {
                RemoveFinger()
            } else {
                Command.empty
            }
        })

        leftJoystickLeftClusterGroup.whenPressed(router {
            if (DriverStation.getInstance().isDisabled || Robot.isGuestDriver()) {
                Command.empty
            } else if (Robot.panelHolder.hasPanel()) {
                DriveVisionPlace()
            } else {
                DriveVisionLoad()
            }
        })

        leftJoystickLeftClusterGroup.whenReleased(router {
            DriveConstant(ControlMode.PercentOutput, 0.0, 0.0)
        })

        // For tuning drive velocity mode PIDF controller
        leftJoystick.rightCluster.topLeft.whenPressed(router {
            if (DriverStation.getInstance().isDisabled) {
                Command.empty
            } else {
                DriveConstant(ControlMode.Velocity,3000.0, 3000.0)
            }
        })

        leftJoystick.rightCluster.topLeft.whenReleased(router {
            DriveConstant(ControlMode.PercentOutput, 0.0, 0.0)
        })

        // For tuning drive velocity mode PIDF controller
        leftJoystick.rightCluster.topCenter.whenPressed(router {
            if (DriverStation.getInstance().isDisabled) {
                Command.empty
            } else {
                DriveConstant(ControlMode.Velocity, 6000.0, 6000.0)
            }
        })

        leftJoystick.rightCluster.topCenter.whenReleased(router {
            DriveConstant(ControlMode.PercentOutput, 0.0, 0.0)
        })

        // For tuning drive velocity mode PIDF controller
        leftJoystick.rightCluster.topRight.whenPressed(router {
            if (DriverStation.getInstance().isDisabled) {
                Command.empty
            } else {
                DriveConstant(ControlMode.Velocity,12000.0, 12000.0)
            }
        })

        leftJoystick.rightCluster.topRight.whenReleased(router {
            DriveConstant(ControlMode.PercentOutput, 0.0, 0.0)
        })

        leftJoystick.rightCluster.bottomLeft.whenPressed(router {
            if (DriverStation.getInstance().isDisabled) {
                Command.empty
            } else {
                DrivePath1()
            }
        })

        leftJoystick.rightCluster.bottomLeft.whenReleased(router {
            DriveConstant(ControlMode.PercentOutput, 0.0, 0.0)
        })

        leftJoystick.rightCluster.bottomCenter.whenPressed(router {
            if (DriverStation.getInstance().isDisabled) {
                Command.empty
            } else {
                DrivePath2()
            }
        })

        leftJoystick.rightCluster.bottomCenter.whenReleased(router {
            DriveConstant(ControlMode.PercentOutput, 0.0, 0.0)
        })

        leftJoystick.rightCluster.bottomRight.whenPressed(router {
            if (DriverStation.getInstance().isDisabled) {
                Command.empty
            } else {
                DrivePath3()
            }
        })

        leftJoystick.rightCluster.bottomRight.whenReleased(router {
            DriveConstant(ControlMode.PercentOutput, 0.0, 0.0)
        })
        operatorController.dPad.down.whenPressed(Elevate(Elevate.Level.Low))
        operatorController.dPad.right.whenPressed(Elevate(Elevate.Level.Middle))
        operatorController.dPad.up.whenPressed(Elevate(Elevate.Level.High))
        operatorController.dPad.left.whenPressed(Elevate(Elevate.Level.CargoShipCargo))

        operatorPanelIntakeButton.whenPressed(IntakePanelFromStation())
        // When the button is released, the intake command is cancelled, which can
        // briefly allow a return to holding power due to the default command
        // sneaking in before the retract command begins. This is now well
        // managed by PanelHolder() even though it can generate an extra ramp down cycle.
        operatorPanelIntakeButton.whenReleased(RetractFingerFromFeederStation())

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
        })

        operatorController.leftStick.whenPressed(router {
            if (operatorController.rightStick.get()) {
                ClimberManual()
            } else {
                Command.empty
            }
        })
    }

}
