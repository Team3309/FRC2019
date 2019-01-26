package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.AssemblyPosition
import org.usfirst.frc.team3309.Constants
import org.usfirst.frc.team3309.commands.arm.ArmRotate
import org.usfirst.frc.team3309.commands.lift.LiftElevate
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.Router
import org.usfirst.frc.team4322.commandv2.group

fun MoveAssembly(position: AssemblyPosition): Command {
    return group {
        parallel {
            +Router {
                if (position.forwardArm) {
                    ArmRotate(Constants.ARM_FORWARD_POSITION)
                } else {
                    ArmRotate(Constants.ARM_BACK_POSITION)
                }
            }
            +LiftElevate(position.liftPosition)
        }
    }
}