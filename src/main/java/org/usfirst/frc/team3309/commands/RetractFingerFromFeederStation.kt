package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.subsystems.Elevator
import org.usfirst.frc.team3309.subsystems.PanelHolder
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun RetractFingerFromFeederStation(): Command {
    return group {
        sequential {
            +WaitCommand(0.5)
            +Elevate(Elevator.CarriagePosition.PanelGrab)
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.FingerVertical)
            +WaitCommand(0.2)
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.TelescopeBack)
            +WaitCommand(1.0)
            +Elevate(Elevate.Level.Home)
        }
    }
}
