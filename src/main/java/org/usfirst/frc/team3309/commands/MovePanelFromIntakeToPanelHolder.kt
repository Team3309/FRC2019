package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.Constants
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
            +WaitCommand(0.1)
            parallel {
                +PanelIntakeActuate(PanelIntake.PanelIntakePosition.Up)
                +PanelHolderSetRollers(Constants.PANEL_HOLDER_INTAKE_POWER, 0.7)
            }
            +PanelIntakeSetRollers(Constants.PANEL_HOLDER_INTAKE_POWER)
            +WaitUntilPanelIsInPanelHolder()
//            +WaitCommand(0.05)
            +PanelIntakeSetRollers(Constants.PANEL_HOLDER_EJECT_POWER)

            +Elevate(Elevator.CarriagePosition.PanelClearingPanelIntake)
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.TelescopeForwards)
            +WaitCommand(0.2)
            +PanelIntakeSetRollers(0.0)
            +Elevate(Elevator.CarriagePosition.PanelLow)
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.TelescopeBack)

        }
    }
}