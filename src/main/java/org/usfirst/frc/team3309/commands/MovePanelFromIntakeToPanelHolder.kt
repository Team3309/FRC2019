package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.commands.panelholder.PanelHolderActuate
import org.usfirst.frc.team3309.commands.panelholder.PanelHolderSetRollers
import org.usfirst.frc.team3309.commands.panelintake.PanelIntakeActuate
import org.usfirst.frc.team3309.commands.panelintake.PanelIntakeSetRollers
import org.usfirst.frc.team3309.subsystems.Elevator
import org.usfirst.frc.team3309.subsystems.PanelHolder
import org.usfirst.frc.team3309.subsystems.PanelIntake
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun MovePanelFromIntakeToPanelHolder(): Command {
    return group {
        sequential {
            +WaitCommand(0.4)
            parallel {
                +PanelIntakeActuate(PanelIntake.PanelIntakePosition.Up)
                +PanelHolderSetRollers(-1.0, 1.6)
            }
            +PanelIntakeSetRollers(-1.0)
            +WaitUntilPanelIsInPanelHolder()
            +WaitCommand(0.25)
            +PanelIntakeSetRollers(1.0)

            +Elevate(Elevator.CarriagePosition.PanelClearingPanelIntake)
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.TelescopeForwards)
            +WaitCommand(0.1)
            +PanelIntakeSetRollers(0.0)
            +Elevate(Elevator.CarriagePosition.PanelLow)
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.TelescopeBack)

        }
    }
}