package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.commands.panelholder.PanelHolderActuate
import org.usfirst.frc.team3309.commands.panelholder.PanelHolderManual
import org.usfirst.frc.team3309.commands.panelholder.PanelHolderSetRollers
import org.usfirst.frc.team3309.commands.panelholder.PanelHolderSetRollersInstantly
import org.usfirst.frc.team3309.subsystems.PanelHolder
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun RemoveFinger(): Command {
    return group {
        parallel {
            +PanelHolderSetRollersInstantly(1.0)
            sequential {
                +WaitCommand(0.25)
                +PanelHolderActuate(PanelHolder.PanelHolderPosition.TelescopeBack)
                +WaitCommand(0.2)
                +PanelHolderManual()
            }

        }
    }
}

