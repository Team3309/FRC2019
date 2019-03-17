package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.commands.panelholder.PanelHolderActuate
import org.usfirst.frc.team3309.commands.panelholder.PanelHolderSetRollers
import org.usfirst.frc.team3309.subsystems.PanelHolder
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun RetractFingerFromFeederStation(): Command {
    return group {
        sequential {
            +PanelHolderSetRollers(-1.0, 0.4)
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.TelescopeBack)
        }
    }
}
