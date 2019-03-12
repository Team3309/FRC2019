package org.usfirst.frc.team3309.lib;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class PIDController {

    private String name;

    private double kP;
    private double kI;
    private double kD;

    private double prevError;
    private double totalError;

    private double integralCeiling = Double.POSITIVE_INFINITY;

    public PIDController(String name, double p, double i, double d) {
        this.name = name;
        kP = p;
        kI = i;
        kD = d;
    }

    public PIDController(double p, double i, double d) {
        this("", p, i, d);
    }

    public double update(double current, double setpoint) {
        double error = setpoint - current;

        double proportional = kP * error;
        totalError += error;

        double integral = kI * totalError;
        if ((kI * Math.abs(integral)) > integralCeiling) {
            integral = Math.signum(integral) * integralCeiling / kI;
        }

        double derivative = kD * (prevError - error);

        prevError = error;
        return proportional + integral + derivative;
    }

    public double update(double error) {
        return update(0, error);
    }

    public void setIntegralCeiling(double ceiling) {
        integralCeiling = ceiling;
    }

    public void reset() {
        totalError = 0.0;
    }

    public void outputToDashboard() {
        SmartDashboard.putNumber(name + " kP", kP);
        SmartDashboard.putNumber(name + " kI", kI);
        SmartDashboard.putNumber(name + " kD", kD);
    }

    public void readDashboard() {
        kP = SmartDashboard.getNumber(name + " kP", 0.0);
        kI = SmartDashboard.getNumber(name + " kI", 0.0);
        kD = SmartDashboard.getNumber(name + " kD", 0.0);
    }

}
