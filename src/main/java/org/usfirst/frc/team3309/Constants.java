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
    private static final byte[] COMPBOT_MAC_ADDR = {0x00, (byte)0x80, 0x2F, 0x17, (byte) 0xE4, 0x5E}; // find this at comp

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


    //===========================
    //= DRIVEBASE PORT MAPPINGS =
    //===========================

    // These are the CAN IDs for the DriveBase motor controllers.
    public static final int DRIVE_RIGHT_MASTER_TALON_ID = 11;
    public static final int DRIVE_RIGHT_SLAVE_VICTOR_1_ID = 13;
    public static final int DRIVE_RIGHT_SLAVE_VICTOR_2_ID = 15;
    public static final int DRIVE_LEFT_MASTER_TALON_ID = 10;
    public static final int DRIVE_LEFT_SLAVE_VICTOR_1_ID = 12;
    public static final int DRIVE_LEFT_SLAVE_VICTOR_2_ID = 14;

    // This is the PCM solenoid port that the gearbox shifter is connected
    public static final int DRIVE_SHIFTER_PCM_PORT = 7;


    //==============================
    //= DRIVEBASE TUNING CONSTANTS =
    //==============================

    // There is no logical reason why this is 9.6. it just be like that.
    // Don't question it, and dont touch it!
    // This value being wrong was part of why the robot took a fat L at AVR.
    public static final double DRIVE_ENCODER_COUNTS_PER_REV = 4096*9.6;
    public static final double WHEEL_DIAMETER_INCHES = 6.0;
    public static final double WHEELBASE_INCHES = 3309;
    public static final double DRIVEBASE_P = 0.0;
    public static final double DRIVEBASE_I = 0.0;
    public static final double DRIVEBASE_D = 0.0;
}
