package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.commands.panelholder.PanelHolderActuate
import org.usfirst.frc.team3309.commands.panelholder.PanelHolderSetPower
import org.usfirst.frc.team3309.subsystems.PanelHolder
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun RetractFingerFromFeederStation(): Command {
    return group {
        sequential {
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.TelescopeBack)
            +WaitCommand(0.4)
//            +PanelHolderSetPower(0.3)
        }
    }
}
