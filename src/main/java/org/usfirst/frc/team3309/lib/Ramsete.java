package org.usfirst.frc.team3309.lib;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;
import org.usfirst.frc.team3309.Constants;
import org.usfirst.frc.team3309.Robot;
import org.usfirst.frc.team3309.lib.logging.ILoggable;
import org.usfirst.frc.team3309.lib.logging.Loggable;

public class Ramsete extends RamseteUtil implements Runnable, ILoggable {

    private static Ramsete instance;
    private static boolean isRunning;

    private static final double kRamseteTimestep = 0.02;

    public static Ramsete getInstance(){
        if (instance == null)
            instance = new Ramsete(kRamseteTimestep);
        return instance;
    }

    private Notifier mNotifier;

    private final double kTimestep;

    public Ramsete(double timestep){
        super(Constants.kWheelBase, timestep);
        mNotifier = new Notifier(this);
        setupLogger();
        kTimestep = timestep;
        isRunning = false;
    }

    public void start(){
        isRunning = true;
        mNotifier.startPeriodic(kTimestep);
        forceStateUpdate();
    }

    public void stop(){
        isRunning = false;
        mNotifier.stop();
        Robot.drive.setRawSpeed(0, 0);
    }

    public static boolean isRunning(){
        return isRunning;
    }

    @Override
    public RobotPos getPose2d(){
        return Robot.drive.getRobotPos();
    }

    @Override
    public void run() {
        this.update();

        Robot.drive.setVelocity(this.getVels().getLeft(), this.getVels().getRight());
    }

    @Override
    public Loggable setupLogger(){
        return new Loggable("PathLog"){
            @Override
            protected LogObject[] collectData() {
                return new LogObject[]{
                    new LogObject("Time", Timer.getFPGATimestamp()),
                    new LogObject("Type", "P"),
                    new LogObject("XPos", getGoalX()),
                    new LogObject("YPos", getGoalY()),
                    new LogObject("Heading", getGoalTheta()),
                };
            }
        };
    }
}