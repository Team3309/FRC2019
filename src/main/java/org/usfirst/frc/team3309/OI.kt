package org.usfirst.frc.team3309

import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.DriverStation
import org.usfirst.frc.team3309.commands.*
import org.usfirst.frc.team3309.commands.drive.*
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

        leftJoystickLeftClusterGroup.whenReleased(router {
            DriveConstant(ControlMode.PercentOutput, 0.0, 0.0)
        })

        // For tuning drive velocity mode PIDF controller
        leftJoystick.rightCluster.topLeft.whenPressed(router {
            if (DriverStation.getInstance().isDisabled) {
                Command.empty
            } else {
                DriveConstant(ControlMode.Velocity,12000.0, 12000.0)
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
                DriveConstant(ControlMode.Velocity, 20000.0, 20000.0)
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
                DriveConstant(ControlMode.Velocity,30000.0, 30000.0)
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
        // When the button is released, the intake command is cancelled, which can
        // briefly allow a return to holding power due to the default command
        // sneaking in before the retract command begins. This is now well
        // managed by PanelHolder() even though it can generate an extra ramp down cycle.
    }

}
