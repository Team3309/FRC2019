package org.usfirst.frc.team3309.commands

import com.ctre.phoenix.motorcontrol.ControlMode
import org.usfirst.frc.team3309.commands.drive.DriveAuto;
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun DriveAuto(mode: ControlMode, left: Double, right: Double): Command {
    return group {
        sequential {
            +DriveAuto(mode, left, right)
        }
    }
}