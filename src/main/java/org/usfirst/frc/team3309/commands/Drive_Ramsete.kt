package org.usfirst.frc.team3309.commands

import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.usfirst.frc.team3309.Constants
import org.usfirst.frc.team3309.Robot
import org.usfirst.frc.team4322.commandv2.Command;
import org.usfirst.frc.team4322.motion.RamseteController;
import org.usfirst.frc.team4322.motion.RobotPositionIntegrator;
import org.usfirst.frc.team4322.motion.Trajectory

class Drive_Ramsete(path: Trajectory, beta: Double, zeta: Double) : Command() {

    var ramseteController = RamseteController(path, Constants.kDriveWheelTrackWidthInches, beta, zeta)

    init {
        require(Robot.drive)
    }

    override fun initialize() {
        System.out.println("Starting ramsete...")
        RobotPositionIntegrator.reset()
        RobotPositionIntegrator.updateWithGyro(Timer.getFPGATimestamp(), 0.0, 0.0, 0.0)
    }

    override fun execute() {
        val out: Pair<Double, Double> = ramseteController.run()
        SmartDashboard.putNumber("Left vel: ", out.first)
        SmartDashboard.putNumber("Right vel: ", out.second)
        Robot.drive.setLeftRight(ControlMode.Velocity, out.first, out.second)
        RobotPositionIntegrator.updateWithGyro(Timer.getFPGATimestamp(),
                Robot.drive.encoderCountsToInches(Robot.drive.leftEncoderVelocity),
                Robot.drive.encoderCountsToInches(Robot.drive.rightEncoderVelocity),
                Robot.drive.angularVelocity)

        SmartDashboard.putNumber("X ", RobotPositionIntegrator.getCurrentPose().x)
        SmartDashboard.putNumber("Y ", RobotPositionIntegrator.getCurrentPose().y)
    }

    override fun isFinished(): Boolean {
        return ramseteController.isFinished()
    }

    override fun end() {
        System.out.println("Finished ramsete")
    }

}