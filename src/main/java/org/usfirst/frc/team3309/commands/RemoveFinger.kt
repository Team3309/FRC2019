package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.commands.panelholder.PanelHolderActuate
import org.usfirst.frc.team3309.commands.panelholder.PanelHolderSetPower
import org.usfirst.frc.team3309.subsystems.Elevator
import org.usfirst.frc.team3309.subsystems.PanelHolder
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun RemoveFinger(): Command {
    return group {
        sequential {
            +PanelHolderSetPower(-0.5)
            +WaitCommand(0.2)
            +Elevate(Elevator.CarriagePosition.DropATad)
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.TelescopeBack)
        }
    }
}
