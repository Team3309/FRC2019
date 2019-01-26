package org.usfirst.frc.team3309;


import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;


/*
 * This class holds all the key values that control how the robot functions.
 * The values are kept in one place so its easy to find them when you need to make a change.
 */
public class Constants {


    /*
     * These MAC_ADDR values are just unique IDs for each of the roboRIO's used in 2018
     * They are used to identify which robot the code is running on, because some values are specific to each robot.
     */
    private static final byte[] PRACTICEBOT_MAC_ADDR = {0x00, (byte) 0x80, 0x2F, 0x17, (byte) 0x85, (byte) 0xD3};
    private static final byte[] COMPBOT_MAC_ADDR = {0x00, (byte) 0x80, 0x2F, 0x17, (byte) 0xE4, 0x5E}; // find this at comp

    /*
     * This enum defines a type with 2 values, PRACTICE and COMPETITION
     * These values represent our COMPETITION robot and PRACTICE robot.
     */
    public enum Robot {
        PRACTICE,
        COMPETITION
    }

    /*
     * This holds an instance of the type we defined above.
     * The static block beneath it just handles fetching the address of the rio we are running on
     * and sets currentRobot to the appropriate value
     */
    public static Robot currentRobot;

    static {
        try {
            byte[] rioMac = NetworkInterface.getByName("eth0").getHardwareAddress();
            if (Arrays.equals(rioMac, PRACTICEBOT_MAC_ADDR)) {
                currentRobot = Robot.PRACTICE;
            } else if (Arrays.equals(rioMac, COMPBOT_MAC_ADDR)) {
                currentRobot = Robot.COMPETITION;
            } else {
                currentRobot = null;
                System.err.println("Oh no! Unknown robot! Did somebody install a new rio?");
            }
        } catch (SocketException ex) {
            ex.printStackTrace();
        }
    }


    /*
     * Drive mappings
     * */
    // These are the CAN IDs for the Drive motor controllers.
    public static final int DRIVE_RIGHT_MASTER_TALON_ID = 11;
    public static final int DRIVE_RIGHT_SLAVE_VICTOR_1_ID = 13;
    public static final int DRIVE_RIGHT_SLAVE_VICTOR_2_ID = 15;
    public static final int DRIVE_LEFT_MASTER_TALON_ID = 10;
    public static final int DRIVE_LEFT_SLAVE_VICTOR_1_ID = 12;
    public static final int DRIVE_LEFT_SLAVE_VICTOR_2_ID = 14;

    // This is the PCM solenoid port that the gearbox shifter is connected
    public static final int DRIVE_SHIFTER_PCM_PORT = 7;

    /**
     * Lift Mappings
     */
    public static final int LIFT_MASTER_TALON_ID = 0;
    public static final int LIFT_SLAVE_VICTOR_ID = 0;

    public static final double LIFT_P = 0.0;
    public static final double LIFT_I = 0.0;
    public static final double LIFT_D = 0.0;

    public static final double LIFT_MAX_POSITION = Double.POSITIVE_INFINITY;
    public static final double LIFT_MIN_POSITION = Double.NEGATIVE_INFINITY;

    public static final double LIFT_BEGIN_SAFE_ZONE = 0;
    public static final double LIFT_END_SAFE_ZONE = 0;

    public static final double LIFT_NUDGE_SPEED = 0; // manual lift speed

    /**
     * Cargo intake mappings
     */
    public static final int CARGO_INTAKE_VICTOR_ID = 0;
    public static final int CARGO_INTAKE_SOLENOID_A = 0;
    public static final int CARGO_INTAKE_SOLENOID_B = 0;

    public static final double CARGO_INTAKE_ROLLERS_MIN_POWER = 0.1;
    /**
     * Panel intake mappings
     */
    public static final int PANEL_INTAKE_VICTOR_ID = 0;
    public static final int PANEL_INTAKE_SOLENOID_ID = 0;

    public static final double PANEL_INTAKE_MIN_POWER = 0.1;

    /**
     * Cargo launcher mappings
     */
    public static final int CARGO_LAUNCHER_VICTOR_ID = 0;

    public static final double CARGO_LAUNCHER_ROLLERS_MIN_POWER = 0.1;

    /**
     * Panel placer mappings
     */
    public static final int PANEL_PLACER_FINGER_SOLENOID_ID = 0;
    public static final int PANEL_PLACER_TELESCOPING_SOLENOID_ID = 0;

    /**
     * Arm mappings and turning constants
     */
    public static final int ARM_TALON_ID = 0;
    public static final double ARM_P = 0.0;
    public static final double ARM_I = 0.0;
    public static final double ARM_D = 0.0;

    // TODO: tune forward and back goal positions
    public static final double ARM_FORWARD_POSITION = 0.0;
    public static final double ARM_BACK_POSITION = 0.0;

    public static final double ARM_TOLERANCE = 2; // percent tolerance

    // TODO: tune min and max
    public static final double ARM_MAX_ANGLE = Double.POSITIVE_INFINITY;
    public static final double ARM_MIN_ANGLE = Double.NEGATIVE_INFINITY;

    /**
     * ROBOT PHYSICAL CONSTANTS
     */
    public static final double DRIVE_ENCODER_COUNTS_PER_REV = 4096 * 9.6;
    public static final double WHEEL_DIAMETER_INCHES = 6.0; // 6.0
    public static final double WHEEL_RADIUS_INCHES = WHEEL_DIAMETER_INCHES / 2.0;
    public static final double kDriveWheelTrackWidthInches = 24.9;
    public static final double kTrackScrubFactor = 1.0;  // Tune me!

    /* DRIVEBASE TUNING CONSTANTS */
    public static final double DRIVE_P = 0.019;
    public static final double DRIVE_D = 0.0006;
    public static final double DRIVE_F = 0.002;
    public static final double DRIVE_CLOSED_LOOP_RAMP_RATE = 0.0;

    // Tuned dynamics
    public static final double kRobotMass = 60.0;  // kg
    public static final double kRobotMomentOfInertia = 10.0;  // kg m^2
    public static final double kRobotAngularDrag = 12.0;  // N*m / (rad/sec)

    private static final boolean isMarios = true;
    public static final double kDriveVIntercept = isMarios ? 1.055 : 1.223;  // V
    public static final double kDriveKv = isMarios ? 0.135 : 0.022;  // V per rad/s
    public static final double kDriveKa = isMarios ? 0.012 : 0.0068;  // V per rad/s^2

    // Trajectory constants
    public static final double kMaxVelocity = 190.0; // in / s
    public static final double kMaxAccel = 190.0;  // in / s^2
    public static final double kMaxCentripetalAccel = 160.0;
    public static final double kMaxVoltage = 9.0;

    // Pure pursuit constants
    public static final double kPathKX = 4.0;  // units/s per unit of error
    public static final double kPathLookaheadTime = 0.4;  // seconds to look ahead along the path for steering
    public static final double kPathMinLookaheadDistance = 24.0;  // inches

}
