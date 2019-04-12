package org.usfirst.frc.team3309.commands

import org.usfirst.frc.team3309.Robot
import org.usfirst.frc.team3309.lib.Limelight
import org.usfirst.frc.team4322.commandv2.Command
import org.usfirst.frc.team4322.commandv2.group

fun LimelightFlash(): Command {
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

fun LimelightBlink(waitTime: Double): Command {
    return group {
        sequential {
            +limelightMode(Limelight.LEDMode.On)
            +WaitCommand(waitTime)
            +limelightMode(Limelight.LEDMode.Off)
            +WaitCommand(0.3)
        }
    }
}

private fun blinkLimelight(): Command {
    return Command.lambda {
        Robot.vision.setLed(Limelight.LEDMode.Blink)
    }
}

private fun limelightMode(ledMode: Limelight.LEDMode): Command {
    return Command.lambda {
        Robot.vision.setLed(ledMode)
    }
}

