package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.commands.panelholder.PanelHolderSetRollers
import org.usfirst.frc.team3309.commands.panelintake.PanelIntakeActuate
import org.usfirst.frc.team3309.commands.panelintake.PanelIntakeSetRollers
import org.usfirst.frc.team3309.subsystems.Elevator
import org.usfirst.frc.team3309.subsystems.PanelIntake
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun MovePanelFromIntakeToPanelHolder(): Command {
    return group {
        sequential {
            sequential {
                parallel {
                    +PanelHolderSetRollers(-1.0, 0.5)
                    +PanelIntakeActuate(PanelIntake.PanelIntakePosition.Up)
                }
            }
            +PanelIntakeSetRollers(-0.1)

            +WaitUntilPanelIsInPanelHolder()
            +WaitCommand(0.25)
            +PanelIntakeSetRollers(1.0)

            +Elevate(Elevator.CarriagePosition.PanelClearingPanelIntake)
            +WaitCommand(0.25)//maybe use sharp?
            +PanelIntakeSetRollers(0.0)
            +Elevate(Elevator.CarriagePosition.PanelLow)
        }
    }
}