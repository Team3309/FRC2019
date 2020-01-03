package org.usfirst.frc.team3309.lib.util;

import org.usfirst.frc.team3309.Robot;

import java.util.List;

/**
 * Contains basic functions that are used often.
 */
public class Util3309 {

    public static final double kEpsilon = 1e-12;

    /**
     * Prevent this class from being instantiated.
     */
    private Util3309() {
    }

    public static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }

    /**
     * Limits the given input to the given magnitude.
     */
    public static double limit(double v, double maxMagnitude) {
        return limit(v, -maxMagnitude, maxMagnitude);
    }

    public static double limit(double v, double min, double max) {
        return Math.min(max, Math.max(min, v));
    }

    public static double interpolate(double a, double b, double x) {
        x = limit(x, 0.0, 1.0);
        return a + (b - a) * x;
    }

    public static String joinStrings(final String delim, final List<?> strings) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < strings.size(); ++i) {
            sb.append(strings.get(i).toString());
            if (i < strings.size() - 1) {
                sb.append(delim);
            }
        }
        return sb.toString();
    }

    public static boolean epsilonEquals(double a, double b, double epsilon) {
        return (a - epsilon <= b) && (a + epsilon >= b);
    }

    public static boolean epsilonEquals(double a, double b) {
        return epsilonEquals(a, b, kEpsilon);
    }

    public static boolean epsilonEquals(int a, int b, int epsilon) {
        return (a - epsilon <= b) && (a + epsilon >= b);
    }

    public static boolean allCloseTo(final List<Double> list, double value, double epsilon) {
        boolean result = true;
        for (Double value_in : list) {
            result &= epsilonEquals(value_in, value, epsilon);
        }
        return result;
    }

    public static double sum(double[] arr) {
        double total = 0.0;
        for (double i : arr) {
            total += i;
        }
        return total;
    }

    /*
     * @return boolean of whether percent error is within percent tolerance
     * @param value, current value
     * @param goal, desired value
     * @param tolerance, percent tolerance
     * */
    public static boolean withinTolerance(double value, double goal, double tolerance) {
        return Math.abs((goal - value) / goal) <= Math.abs(tolerance);
    }

    /*
     * @return whether value is inclusively within limits
     * @param min, minimum value threshold
     * @param max, maximum value threshold
     * */
    public static boolean within(double value, double min, double max) {
        return (value >= min) && (value <= max);
    }

    // This has nothing to do with a max function.
    // We don't know what it is really doing.
    // If anyone ever figures it out, please rename this method.
    public static double weirdSignedMax(double a, double b, double min) {
        if (a > min) {
            return a;
        } else if (b > min) {
            return -b;
        } else {
            return 0.0;
        }
    }

    public static double overlap1D(double min1, double max1, double min2, double max2) {
        return Math.max(0, Math.min(max1, max2) - Math.max(min1, min2));
    }

    public static double distanceFormula(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    //take argument of desiredHeading (btwn -180 and 180) downfield = 0
    //read NavX, return value of how far off current heading is from desired heading (btwn -180 and 180)

    public static double headingError(double desiredHeading) {

        double heading = ((180 + Robot.drive.getAngularPosition()) % 360) - 180;
        double headingError = desiredHeading - heading;
        if 
        /*if (Robot.drive.getAngularPosition() > 180) {
            heading = Robot.drive.getAngularPosition() - 360;
        } else if (Robot.drive.getAngularPosition() < -180) {
            heading = Robot.drive.getAngularPosition() + 360;
        }
        */

        return headingError;

    }
}



