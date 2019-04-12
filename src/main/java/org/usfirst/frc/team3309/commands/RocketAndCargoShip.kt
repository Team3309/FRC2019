package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.PathLoader
import org.usfirst.frc.team3309.Robot
import org.usfirst.frc.team3309.commands.drive.auto.MoveCommand
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun RocketAndCargoShip(): Command {
    return group {
        sequential {
            +MoveCommand(Robot.autonPaths[PathLoader.Path.HabToRocketLeft])

        }
    }
}
