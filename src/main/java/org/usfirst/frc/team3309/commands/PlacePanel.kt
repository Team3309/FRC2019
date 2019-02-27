package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.subsystems.Elevator
import org.usfirst.frc.team3309.subsystems.PanelHolder
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun PlacePanel(): Command {
    return group {
        sequential {
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.TelescopeForwards)
            +WaitCommand(0.2)
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.FingerPointingOutwards)
            +WaitCommand(0.2)
            +Elevate(Elevator.CarriagePosition.DropATad)
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.TelescopeBack)
            +WaitCommand(0.5)
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.FingerVertical)
        }
    }
}
