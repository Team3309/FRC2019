package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.subsystems.PanelHolder
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun PanelHolderGoHome(): Command {
    return group {
        sequential {
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.TelescopeBack)
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.FingerVertical)
        }
    }
}
