package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.subsystems.Elevator
import org.usfirst.frc.team3309.subsystems.PanelHolder
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun RetractFingerFromFeederStation(): Command {
    return group {
        sequential {
            +WaitCommand(0.2)
            +Elevate(Elevator.CarriagePosition.PanelLow)
            +WaitCommand(.25)
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.FingerVertical)
            +WaitCommand(0.4)
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.TelescopeBack)
//            +WaitCommand(.55)
//            +Elevate(Elevate.Level.Home)
        }
    }
}
