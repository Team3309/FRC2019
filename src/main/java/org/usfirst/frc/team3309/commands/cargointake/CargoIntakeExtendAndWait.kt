package org.usfirst.frc.team3309.commands.cargointake

import org.usfirst.frc.team3309.commands.WaitCommand
import org.usfirst.frc.team3309.subsystems.CargoIntake
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

object CargoIntakeExtendAndWait {

    @JvmStatic
    fun to(): Command {
        return group {
            parallel {
                sequential {
                    +CargoIntakeActuate(CargoIntake.CargoIntakePosition.Extended)
                    +WaitCommand(0.5)
                }
            }
        }
    }

}


