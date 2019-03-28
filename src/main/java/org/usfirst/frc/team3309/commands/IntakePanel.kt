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

fun IntakePanelFromGround(): Command {
    return group {
        parallel {
            +PanelIntakeActuate(PanelIntake.PanelIntakePosition.Down)
            +Elevate(Elevator.CarriagePosition.PanelLow)
            +PanelIntakeSetRollers(-1.0)
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.TelescopeBack)

            sequential {
                +WaitUntilPanelIsInIntake()
                +MovePanelFromIntakeToPanelHolder()
            }
        }
    }
}