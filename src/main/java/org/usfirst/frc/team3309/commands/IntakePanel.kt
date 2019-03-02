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
//            +PanelIntakeManual()
            +Elevate(Elevator.CarriagePosition.PanelLow)
            +PanelHolderActuate(PanelHolder.PanelHolderPosition.Extended)
            +PanelIntakeSetRollers(0.3)

            sequential {
                // TODO: add or for operator override
                +WaitUntilPanelIsInIntake()

                +WaitCommand(0.25)
                +PanelIntakeActuate(PanelIntake.PanelIntakePosition.Up)
                +WaitCommand(1.5)
                +PanelHolderActuate(PanelHolder.PanelHolderPosition.FingerVertical)
                +WaitCommand(0.25)

//                +WaitUntilPanelIsInPanelHolder()
//                +PanelHolderActuate(PanelHolder.PanelHolderPosition.GrabPanel)
//
//                +PanelIntakeSetRollers(-0.2)
//                +Elevate(Elevator.CarriagePosition.PanelClearingPanelIntake)
//                +PanelIntakeStopRollers()
//
//                // hopefully the PanelIntake is now behind the panel
//                +WaitCommand(0.25)
//                +Elevate(Elevator.CarriagePosition.PanelLow)
            }
        }
    }
}