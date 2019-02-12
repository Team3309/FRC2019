package org.usfirst.frc.team3309.autos

import org.usfirst.frc.team3309.commands.Drive_Ramsete
import org.usfirst.frc.team4322.commandv2.group
import org.usfirst.frc.team4322.motion.Trajectory

object RamseteTestPath {

    @JvmStatic
    fun to(): org.usfirst.frc.team4322.commandv2.Command {
        return group {
            sequential {
                +Drive_Ramsete(Trajectory.load("/home/lvuser/paths/Straightline.csv")!!, 1.81*10, 0.87)
            }
        }
    }

}