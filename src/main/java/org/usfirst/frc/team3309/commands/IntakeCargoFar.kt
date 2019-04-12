package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.commands.cargoholder.CargoHolderManual
import org.usfirst.frc.team3309.commands.cargoholder.CargoHolderSetRollers
import org.usfirst.frc.team3309.commands.cargoholder.WaitUntilCargoIsIn
import org.usfirst.frc.team3309.commands.cargointake.CargoIntakeActuate
import org.usfirst.frc.team3309.commands.cargointake.CargoIntakeManual
import org.usfirst.frc.team3309.commands.cargointake.CargoIntakeStopRollers
import org.usfirst.frc.team3309.commands.elevator.Elevate
import org.usfirst.frc.team3309.subsystems.CargoIntake
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun IntakeCargoFar(): Command {
    return group {
        parallel {
            +CargoIntakeActuate(CargoIntake.CargoIntakePosition.Stowed)
            +CargoIntakeManual()
            +CargoHolderManual()
            +Elevate(Elevate.Level.Home)
            sequential {
                +WaitUntilCargoIsIn()
                +CargoIntakeStopRollers()
                +CargoHolderSetRollers(3.0 / 12.0)
            }
        }
    }
}
