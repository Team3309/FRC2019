package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.commands.cargoholder.CargoHolderManual
import org.usfirst.frc.team3309.commands.cargoholder.CargoHolderSetRollers
import org.usfirst.frc.team3309.commands.cargointake.CargoIntakeActuate
import org.usfirst.frc.team3309.commands.cargointake.CargoIntakeManual
import org.usfirst.frc.team3309.commands.cargointake.CargoIntakeStopRollers
import org.usfirst.frc.team3309.commands.panelintake.PanelIntakeActuate
import org.usfirst.frc.team3309.commands.panelintake.PanelIntakeStopRollers
import org.usfirst.frc.team3309.subsystems.CargoIntake
import org.usfirst.frc.team3309.subsystems.Elevator
import org.usfirst.frc.team3309.subsystems.PanelIntake
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun IntakeCargoNear(): Command {
    return group {
        parallel {
            +CargoIntakeActuate(CargoIntake.CargoIntakePosition.Extended)
            +CargoIntakeManual()
            +CargoHolderManual()
            +Elevate(Elevate.Level.Home)
            sequential {
                // TODO: add or for operator override
                +WaitUntilCargoIsIn()
                +CargoIntakeStopRollers()
                +CargoHolderSetRollers(3.0 / 12.0)
                +Elevate(Elevator.CarriagePosition.CargoLow)
                +CargoIntakeActuate(CargoIntake.CargoIntakePosition.Stowed)
            }
        }
    }
}