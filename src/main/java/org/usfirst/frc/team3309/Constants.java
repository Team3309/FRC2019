package org.usfirst.frc.team3309;


import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Arrays;

import static org.usfirst.frc.team3309.Constants.Robot.*;


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
    private static final byte[] COMPBOT_MAC_ADDR = {0x00, (byte) 0x80, 0x2F, 0x22, (byte) 0xB0, (byte) 0x6C}; // find this at comp

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
                currentRobot = PRACTICE;
            } else if (Arrays.equals(rioMac, COMPBOT_MAC_ADDR)) {
                currentRobot = COMPETITION;
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
    public static final int DRIVE_RIGHT_MASTER_FALCON_ID = 2;
    public static final int DRIVE_RIGHT_SLAVE_FALCON_ID = 7;
    public static final int DRIVE_LEFT_MASTER_FALCON_ID = 3;
    public static final int DRIVE_LEFT_SLAVE_FALCON_ID = 8;
    public static final int DRIVE_SHIFTER_PCM_PORT = 7;

    /**
     * Elevator Mappings
     */
    // max position used for conversions (encoder counts)
    public static final double ELEVATOR_ENCODER_COUNTS_FOR_MAX_HEIGHT = 116000;

    // timeout for Elevate to finish
    public static final double ELEVATOR_TIMEOUT = 2.0;

    // amount elevator drops for placing a panel
    public static final double ELEVATOR_PANEL_DROP_DISTANCE = 0.02;

    // these can NEVER be the same

    /**
     * Cargo intake mappings
     */

    /**
     * Panel intake mappings
     */

    /**
     * Cargo launcher mappings
     */

    /**
     * Panel placer mappings
     */

    /**
     * Climber mappings
     * */

    // Pipeline mappings
    public static final int kVisionCenterPipeline = 0;
    public static final int kDriverPipeline = 1;
    public static final int kVisionLeftPipeline = 2;
    public static final int kVisionRightPipeline = 3;

    /**
     * ROBOT PHYSICAL CONSTANTS
     */
    public static final double DRIVE_ENCODER_COUNTS_PER_REV = 4096 * 9.6;
    public static final double WHEEL_DIAMETER_INCHES = 6.0;
    public static final double WHEEL_RADIUS_INCHES = WHEEL_DIAMETER_INCHES / 2.0;
    public static final double INCHES_PER_REV = (WHEEL_DIAMETER_INCHES*Math.PI);
    public static final double ENCODER_COUNTS_PER_DEGREE = 600;
    public static final double kDriveWheelTrackWidthInches = 24.9;
    public static final double kTrackScrubFactor = 1.0;  // Tune me!

    // Offset of the panel limelight on the bot
    public static final double kPanelLimelightInchesX = -4.5;  // lateral off-center
    public static final double kPanelLimelightPlacementInchesZ = 26.5;  // behind placement point
    public static final double kPanelLimelightRotationCenterInchesZ = 4.0;  // behind bot rotational center
    public static final double kPanelLimelightMountingSkewDegrees = 0; // positive value => skewed to right
    public static final double kPanelHolderBiasInchesX = 0;  // negative value biases placement to the right

    // PDP channels
    public static final int kPdpChannelDriveLeft1 = 13;
    public static final int kPdpChannelDriveLeft2 = 2;
    public static final int kPdpChannelDriveLeft3 = 0;
    public static final int kPdpChannelDriveRight7 = 14;
    public static final int kPdpChannelDriveRight8 = 1;
    public static final int kPdpChannelDriveRight9 = 15;
    public static final int kPdpChannelLift10 = 12;
    public static final int kPdpChannelLift6 = 3;

    /* DRIVEBASE TUNING CONSTANTS */
    public static final double kDriveVelocityP = 0.019;
    public static final double kDriveVelocityD = 0.0006;
    public static final double kDriveVelocityF = 0.002;
    public static final double DRIVE_CLOSED_LOOP_RAMP_RATE = 0.0;
    public static final double DRIVE_OPEN_LOOP_RAMP_RATE = 0.15; // don't go below 0.15, due to rebound power in cheezy

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
