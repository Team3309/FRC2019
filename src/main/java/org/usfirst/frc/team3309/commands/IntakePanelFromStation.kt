package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.subsystems.Elevator
import org.usfirst.frc.team3309.subsystems.PanelHolder
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun IntakePanelFromStation(): Command {
    return group {
        parallel {
            +Elevate(Elevate.Level.Home)
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.PlacePanel)
            sequential {
                +WaitUntilPanelIsInPanelHolder()
                +RetractFingerFromFeederStation()
            }
        }
    }
}
