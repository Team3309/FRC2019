package org.usfirst.frc.team3309.commands.drive

import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group


fun DrivePath1(): Command {

    val path = arrayOf<Waypoint>(
            Waypoint(0, 0, 0, false),
            Waypoint(36, 0, 0, false))

    return group {
        sequential {
            +DriveAuto(path, false)
            //+DriveVisionPlace()
        }
    }
}

fun DrivePath2(): Command {

    val path = arrayOf<Waypoint>(
            Waypoint(0, 0, 0, false),
            Waypoint(72, 0, 0, false))

    return group {
        sequential {
            +DriveAuto(path, false)
            //+DriveVisionLoad()
        }
    }
}

fun DrivePath3(): Command {

    val path = arrayOf<Waypoint>(
            Waypoint(0, 0, 0, false),
            Waypoint(120, 0, 0, false))

    return group {
        sequential {
            +DriveAuto(path, false)
            //+DriveVisionPlace()
        }
    }
}