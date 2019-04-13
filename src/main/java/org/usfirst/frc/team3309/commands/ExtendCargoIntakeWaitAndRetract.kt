/*
package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.OI
import org.usfirst.frc.team3309.commands.cargointake.CargoIntakeActuate
import org.usfirst.frc.team3309.subsystems.CargoIntake
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun ExtendCargoIntakeWaitAndRetract(waitTime: Double): Command {
    return group {
        sequential {
            +CargoIntakeActuate(CargoIntake.CargoIntakePosition.Extended)
            +WaitCommand(waitTime)
            router {
                if (!OI.operatorCargoIntakeButton.get()) {
                    CargoIntakeActuate(CargoIntake.CargoIntakePosition.Stowed)
                } else {
                    Command.empty
                }
            }
        }
    }
}*/
