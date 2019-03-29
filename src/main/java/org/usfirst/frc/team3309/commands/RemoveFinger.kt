package org.usfirst.frc.team3309.commands

import edu.wpi.first.wpilibj.DriverStation
import org.usfirst.frc.team3309.commands.panelholder.PanelHolderActuate
import org.usfirst.frc.team3309.commands.panelholder.PanelHolderSetRollers
import org.usfirst.frc.team3309.subsystems.PanelHolder
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun RemoveFinger(): Command {
    return group {
        sequential {
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.TelescopeBack)
            +PanelHolderSetRollers(1.0, 1.0)
        }
    }
}

