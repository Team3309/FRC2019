package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.commands.cargointake.CargoIntakeActuate
import org.usfirst.frc.team3309.subsystems.CargoIntake
import org.usfirst.frc.team3309.subsystems.Elevator
import org.usfirst.frc.team3309.subsystems.PanelHolder
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun IntakePanelFromStation(): Command {
    return group {
        parallel {
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.Extended)
            +CargoIntakeActuate(CargoIntake.CargoIntakePosition.Stowed)
            sequential {
                +Elevate(Elevator.CarriagePosition.PanelFeederStation)
                +LowerElevatorToLimitSwitch()
            }
            sequential {
                +WaitUntilPanelIsInPanelHolder()
                +RetractFingerFromFeederStation()
            }
        }
    }
}
