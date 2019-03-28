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
            +PanelHolderSetRollers(-1.0)
            +WaitCommand(0.0)
            +PanelIntakeActuate(PanelIntake.PanelIntakePosition.Up)

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
