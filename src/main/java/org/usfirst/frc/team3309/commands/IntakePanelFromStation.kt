package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.subsystems.Elevator
import org.usfirst.frc.team3309.subsystems.PanelHolder
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun IntakePanelFromStation(): Command {
    return group {
        parallel {
            +Elevate(Elevate.Level.Home)
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.PlacePanel)
            sequential {
                +WaitUntilPanelIsInPanelHolder()
                +WaitCommand(0.2)
                +Elevate(Elevator.CarriagePosition.PanelGrab)
                +PanelHolderActuate(PanelHolder.PanelHolderPosition.FingerVertical)
                +WaitCommand(0.2)
                +PanelHolderActuate(PanelHolder.PanelHolderPosition.TelescopeBack)
                +WaitCommand(1.0)
                +Elevate(Elevate.Level.Home)
            }
        }
    }
}
