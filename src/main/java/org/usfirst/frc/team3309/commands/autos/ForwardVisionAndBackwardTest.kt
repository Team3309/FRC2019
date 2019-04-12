package org.usfirst.frc.team3309.commands.autos;

import org.usfirst.frc.team3309.PathLoader
import org.usfirst.frc.team3309.Robot
import org.usfirst.frc.team3309.commands.WaitCommand
import org.usfirst.frc.team3309.commands.drive.auto.MoveCommand
import org.usfirst.frc.team3309.commands.drive.auto.VisionApproach
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun ForwardVisionAndBackwardTest(): Command {
    return group {
        sequential {
            +MoveCommand(Robot.autonPaths.get(PathLoader.Path.ForwardToCargoShipTest), Robot.Side.PANEL, MoveCommand.VisionCancel.CANCEL_ON_VISION)
            +VisionApproach(true)
            +WaitCommand(2.0)
            first {
                +Command.waitFor {
                    !Robot.panelHolder.hasPanel()
                }
                +WaitCommand(1.0)
            }
            +VisionApproach(false)
            +WaitCommand(0.1)
            +MoveCommand(Robot.autonPaths.get(PathLoader.Path.BackFromCargoShipTest), Robot.Side.BALL, MoveCommand.VisionCancel.RUN_FULL_PATH)
        }
    }
}