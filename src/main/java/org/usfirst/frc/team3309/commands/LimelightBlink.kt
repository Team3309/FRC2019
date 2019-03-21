package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.Robot
import org.usfirst.frc.team3309.lib.Limelight
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun blinkLimelight(): Command {
    return     Command.lambda {
        Robot.vision.setLed(Limelight.LEDMode.Blink)
    }
}

fun LimelightBlink(): Command {
    return group {
        sequential {
            +blinkLimelight()
            +WaitCommand(0.15)
            +blinkLimelight()
            +WaitCommand(0.15)
            +Command.lambda {
                Robot.vision.setLed(Limelight.LEDMode.Off)
            }
        }
    }
}
