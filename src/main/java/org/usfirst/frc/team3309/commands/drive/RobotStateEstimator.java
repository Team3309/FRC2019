package org.usfirst.frc.team3309.commands.drive;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.geometry.Pose2d;
import org.usfirst.frc.team3309.lib.geometry.Rotation2d;
import org.usfirst.frc.team3309.lib.geometry.Twist2d;
import org.usfirst.frc.team3309.lib.physics.Kinematics;
import org.usfirst.frc.team3309.lib.util.InterpolatingDouble;
import org.usfirst.frc.team3309.lib.util.InterpolatingTreeMap;
import org.usfirst.frc.team4322.commandv2.Command;

import java.util.Map;

/*
 * TODO: fix odometry conversions, under true position
 * */
public class RobotStateEstimator extends Command {

    private static final int kObservationBufferSize = 100;

    // FPGATimestamp -> RigidTransform2d or Rotation2d
    private InterpolatingTreeMap<InterpolatingDouble, Pose2d> field_to_vehicle_;
    private Twist2d vehicle_velocity_predicted_;
    private Twist2d vehicle_velocity_measured_;
    private double distance_driven_;


    private double left_encoder_prev_distance_ = 0.0;
    private double right_encoder_prev_distance_ = 0.0;

    public RobotStateEstimator() {
        reset(0, new Pose2d());
    }

    @Override
    protected void initialize() {
        left_encoder_prev_distance_ = Robot.drive.getLeftEncoderDistance();
        right_encoder_prev_distance_ = Robot.drive.getRightEncoderDistance();
    }

    @Override
    protected void execute() {
        double timestamp = Timer.getFPGATimestamp();
        final double left_distance = Robot.drive.getLeftEncoderDistance();
        final double right_distance = Robot.drive.getRightEncoderDistance();
        final double delta_left = Robot.drive.encoderCountsToInches(left_distance - left_encoder_prev_distance_);
        final double delta_right = Robot.drive.encoderCountsToInches(right_distance - right_encoder_prev_distance_);
        final Rotation2d gyro_angle = Rotation2d.fromDegrees(Robot.drive.getAngularPosition());
        final Twist2d odometry_velocity = generateOdometryFromSensors(
                delta_left, delta_right, gyro_angle);

        final double leftLinearVelocity = Robot.drive.encoderVelocityToInchesPerSecond(Robot.drive.getLeftEncoderVelocity());
        final double rightLinearVelocity = Robot.drive.encoderVelocityToInchesPerSecond(Robot.drive.getRightEncoderVelocity());

        final Twist2d predicted_velocity = Kinematics.forwardKinematics(leftLinearVelocity, rightLinearVelocity);
        addObservations(timestamp, odometry_velocity,
                predicted_velocity);

        left_encoder_prev_distance_ = left_distance;
        right_encoder_prev_distance_ = right_distance;
    }

    /**
     * Resets the field to robot transform (robot's position on the field)
     */
    public synchronized void reset(double start_time, Pose2d initial_field_to_vehicle) {
        field_to_vehicle_ = new InterpolatingTreeMap<>(kObservationBufferSize);
        field_to_vehicle_.put(new InterpolatingDouble(start_time), initial_field_to_vehicle);
        vehicle_velocity_predicted_ = Twist2d.identity();
        vehicle_velocity_measured_ = Twist2d.identity();
        distance_driven_ = 0.0;
    }

    public synchronized void resetDistanceDriven() {
        distance_driven_ = 0.0;
    }

    /**
     * Returns the robot's position on the field at a certain time. Linearly interpolates between stored robot positions
     * to fill in the gaps.
     */
    public synchronized Pose2d getFieldToVehicle(double timestamp) {
        return field_to_vehicle_.getInterpolated(new InterpolatingDouble(timestamp));
    }

    public synchronized Map.Entry<InterpolatingDouble, Pose2d> getLatestFieldToVehicle() {
        return field_to_vehicle_.lastEntry();
    }

    public synchronized Pose2d getPredictedFieldToVehicle(double lookahead_time) {
        return getLatestFieldToVehicle().getValue()
                .transformBy(Pose2d.exp(vehicle_velocity_predicted_.scaled(lookahead_time)));
    }

    public synchronized void addFieldToVehicleObservation(double timestamp, Pose2d observation) {
        field_to_vehicle_.put(new InterpolatingDouble(timestamp), observation);
    }

    public synchronized void addObservations(double timestamp, Twist2d measured_velocity,
                                             Twist2d predicted_velocity) {
        addFieldToVehicleObservation(timestamp,
                Kinematics.integrateForwardKinematics(getLatestFieldToVehicle().getValue(), measured_velocity));
        vehicle_velocity_measured_ = measured_velocity;
        vehicle_velocity_predicted_ = predicted_velocity;
    }

    public synchronized Twist2d generateOdometryFromSensors(double left_encoder_delta_distance, double
            right_encoder_delta_distance, Rotation2d current_gyro_angle) {
        final Pose2d last_measurement = getLatestFieldToVehicle().getValue();
        final Twist2d delta = Kinematics.forwardKinematics(last_measurement.getRotation(),
                left_encoder_delta_distance, right_encoder_delta_distance,
                current_gyro_angle);
        distance_driven_ += delta.dx; //do we care about dy here?
        return delta;
    }

    public synchronized double getDistanceDriven() {
        return distance_driven_;
    }

    public synchronized Twist2d getPredictedVelocity() {
        return vehicle_velocity_predicted_;
    }

    public synchronized Twist2d getMeasuredVelocity() {
        return vehicle_velocity_measured_;
    }

    public void outputToSmartDashboard() {
        Pose2d odometry = getLatestFieldToVehicle().getValue();
        SmartDashboard.putNumber("Robot Pose X", odometry.getTranslation().x());
        SmartDashboard.putNumber("Robot Pose Y", odometry.getTranslation().y());
        SmartDashboard.putNumber("Robot Pose Theta", odometry.getRotation().getDegrees());
        SmartDashboard.putNumber("Robot Linear Velocity", vehicle_velocity_measured_.dx);
    }

    @Override
    protected boolean isFinished() {
        return false;
    }
}
