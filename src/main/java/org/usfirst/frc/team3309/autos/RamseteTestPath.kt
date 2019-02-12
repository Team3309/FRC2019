package org.usfirst.frc.team3309.autos

import edu.wpi.first.wpilibj.command.Command
import org.usfirst.frc.team3309.commands.Drive_Ramsete
import org.usfirst.frc.team4322.commandv2.group
import org.usfirst.frc.team4322.motion.Trajectory

object RamseteTestPath {

    fun to(): org.usfirst.frc.team4322.commandv2.Command {
        return group {
            sequential {
                +Drive_Ramsete(Trajectory.load("Straightline.pf1.csv")!!, 0.0, 0.0)
            }
        }
    }

}