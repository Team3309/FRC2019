package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.Constants
import org.usfirst.frc.team3309.commands.panelholder.PanelHolderActuate
import org.usfirst.frc.team3309.commands.panelholder.PanelHolderSetRollers
import org.usfirst.frc.team3309.subsystems.PanelHolder
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun RetractFingerFromFeederStation(): Command {
    return group {
        parallel {
            +PanelHolderSetRollers(Constants.PANEL_HOLDER_INTAKE_POWER, 0.1)
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.TelescopeBack)
        }
    }
}
