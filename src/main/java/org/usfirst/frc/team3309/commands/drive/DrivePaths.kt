package org.usfirst.frc.team3309.commands.drive

import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group


fun DrivePath1(): Command {

    val path = arrayOf<Waypoint>(
            Waypoint(),
            Waypoint(36F, 0F, 0F, 10000F, 100F, false))

    return group {
        sequential {
            +DriveAuto(path, false)
            //+DriveVisionPlace()
        }
    }
}

fun DrivePath2(): Command {

    val path = arrayOf<Waypoint>(
            Waypoint(),
            Waypoint(72F, 0F, 0F, 10000F, 100F, false))

    return group {
        sequential {
            +DriveAuto(path, false)
            //+DriveVisionLoad()
        }
    }
}

fun DrivePath3(): Command {

    val path = arrayOf<Waypoint>(
            Waypoint(),
            Waypoint(120F, 0F, 0F, 10000F, 100F, false))

    return group {
        sequential {
            +DriveAuto(path, false)
            //+DriveVisionPlace()
        }
    }
}