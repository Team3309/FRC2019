package org.usfirst.frc.team3309.commands.drive

import org.usfirst.frc.team3309.commands.WaitCommand
import org.usfirst.frc.team3309.lib.physics.DriveCharacterization
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

object CharacterizeHighGearTurn {

    class CalculateConstants(private val velocityData: ArrayList<DriveCharacterization.VelocityDataPoint>,
                             private val accelerationData: ArrayList<DriveCharacterization.AccelerationDataPoint>) : Command() {

        override fun execute() {
            val constants = DriveCharacterization.characterizeDrive(velocityData, accelerationData)
            System.out.println("ks: " + constants.ks)
            System.out.println("kv: " + constants.kv)
            System.out.println("ka: " + constants.ka)
        }

        override fun isFinished(): Boolean = true
    }

    @JvmStatic
    fun to(): Command {
        val velocityData = ArrayList<DriveCharacterization.VelocityDataPoint>()
        val accelerationData = ArrayList<DriveCharacterization.AccelerationDataPoint>()

        return group {
            sequential {
                +CollectVelocityData(velocityData, true, false, true)
                +WaitCommand(5.0)
                +CollectAccelerationData(accelerationData, true, false, true)
                +CalculateConstants(velocityData, accelerationData)
            }

        }

    }
}
