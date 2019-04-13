/*
package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.Robot
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
            sequential {
                +Elevate(Elevate.Level.Home)
                +LowerElevatorToLimitSwitch()
            }
            sequential {
                +WaitUntilCargoIsIn()
                +Elevate(Elevator.CarriagePosition.CargoLow)
                +WaitCommand(0.15)
                +CargoIntakeActuate(CargoIntake.CargoIntakePosition.Stowed)
            }

        }
    }
}*/
