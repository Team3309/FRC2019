package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.commands.drive.DriveAuto;
import org.usfirst.frc.team3309.subsystems.Drive
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun DriveAuto(mode: Int, encoderCount: Double): Command {
    return group {
        sequential {
            +DriveAuto(mode, encoderCount)
        }
    }
}