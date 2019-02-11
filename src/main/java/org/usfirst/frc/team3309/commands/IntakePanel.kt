package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.commands.panelintake.PanelIntakeActuate
import org.usfirst.frc.team3309.commands.panelintake.PanelIntakeManual
import org.usfirst.frc.team3309.commands.panelintake.PanelIntakeSetRollers
import org.usfirst.frc.team3309.commands.panelintake.PanelIntakeStopRollers
import org.usfirst.frc.team3309.subsystems.Elevator
import org.usfirst.frc.team3309.subsystems.PanelHolder
import org.usfirst.frc.team3309.subsystems.PanelIntake
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun IntakePanel(): Command {
    return group {
        parallel {
            +PanelIntakeActuate(PanelIntake.PanelIntakePosition.Down)
            +PanelIntakeManual()
            sequential {
                +WaitUntilPanelIsIn()
                +PanelIntakeActuate(PanelIntake.PanelIntakePosition.Up)
                +PanelHolderActuate(PanelHolder.PanelHolderPosition.GrabPanel)
                parallel {
                    +PanelIntakeSetRollers(-0.2)
                    +Elevate(Elevator.CarriagePosition.PanelLow)
                }
                +PanelIntakeStopRollers()
            }
        }
    }
}