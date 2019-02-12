package org.usfirst.frc.team3309.commands

import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.Timer
import org.usfirst.frc.team3309.Constants
import org.usfirst.frc.team3309.Robot
import org.usfirst.frc.team4322.commandv2.Command;
import org.usfirst.frc.team4322.motion.RamseteController;
import org.usfirst.frc.team4322.motion.RobotPositionIntegrator;
import org.usfirst.frc.team4322.motion.Trajectory

class Drive_Ramsete(path: Trajectory, beta: Double, zeta: Double) : Command() {

    var ramseteController = RamseteController(path, Constants.kDriveWheelTrackWidthInches, beta, zeta);

    override fun initialize() {
        RobotPositionIntegrator.reset()
        RobotPositionIntegrator.updateWithGyro(Timer.getFPGATimestamp(), 0.0, 0.0, 0.0)
    }

    override fun execute() {
        val out: Pair<Double, Double> = ramseteController.run()
        Robot.drive.setLeftRight(ControlMode.Velocity, out.first, out.second)
        RobotPositionIntegrator.updateWithGyro(Timer.getFPGATimestamp(), Robot.drive.leftEncoderVelocity,
                Robot.drive.rightEncoderVelocity, Robot.drive.angularVelocity)
    }

    override fun isFinished(): Boolean {
        return ramseteController.isFinished()
    }

}