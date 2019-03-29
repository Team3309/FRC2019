package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.Robot
import org.usfirst.frc.team3309.commands.cargointake.CargoIntakeActuate
import org.usfirst.frc.team3309.commands.panelholder.PanelHolderActuate
import org.usfirst.frc.team3309.commands.panelholder.PanelHolderSetRollers
import org.usfirst.frc.team3309.subsystems.CargoIntake
import org.usfirst.frc.team3309.subsystems.Elevator
import org.usfirst.frc.team3309.subsystems.PanelHolder
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group


fun IntakePanelFromStation(): Command {
    return group {
        parallel {
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.TelescopeForwards)
            +PanelHolderSetRollers(-1.0)

            sequential {
                +CargoIntakeActuate(CargoIntake.CargoIntakePosition.Extended)
                +WaitCommand(2.0)
                +CargoIntakeActuate(CargoIntake.CargoIntakePosition.Stowed)
            }
            sequential {
                +LowerElevatorToLimitSwitch()
                +Elevate(Elevator.CarriagePosition.PanelFeederStation)
            }
            sequential {
                +WaitUntilPanelIsInPanelHolder()
                +RetractFingerFromFeederStation()
            }

        }
    }
}

