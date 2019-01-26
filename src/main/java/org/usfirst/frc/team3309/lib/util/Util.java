package org.usfirst.frc.team3309.lib.util;

import java.util.List;

/**
 * Contains basic functions that are used often.
 */
public class Util {

    public static final double kEpsilon = 1e-12;

    /**
     * Prevent this class from being instantiated.
     */
    private Util() {
    }

    public static double clamp(double v, double min, double max) {
        return limit(v, min, max);
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
    * @return positive percent error
    * @param error, current closed loop error
    * @param goal, goal in loop
    * */
    public static double errorToPercent(double error, double goal) {
        return error / goal * 100;
    }

    /*
    * @return boolean of whether percent error is within percent tolerance
     * @param error, current closed loop error
     * @param goal, goal in loop
     * @param tolerance, percent tolerance the percent error must within error
    * */
    public static boolean withinTolerance(double error, double goal, double tolerance) {
        return Math.abs(Util.errorToPercent(error, goal)) < Math.abs(tolerance);
    }

    /*
    * @return whether value is inclusively within limits
    * @param min, minimum value threshold
    * @param max, maximum value threshold
    * */
    public static boolean within(double value, double min, double max) {
        return (value >= min) && (value <= max);
    }

}
