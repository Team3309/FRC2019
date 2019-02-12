package org.usfirst.frc.team3309;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.autos.RamseteTestPath;
import org.usfirst.frc.team4322.commandv2.Command;

public class AutoModeExecutor {

    private static SendableChooser<Command> autos = new SendableChooser<>();

    public static void displayAutos() {

        autos.setDefaultOption("Ramsete Path Test", RamseteTestPath.INSTANCE.to());
        autos.addOption("No Auto", new Command() {
            @Override
            protected boolean isFinished() {
                return false;
            }
        });
        SmartDashboard.putData("Autos: ", autos);
    }

    public static Command getAutoSelected() {
        return autos.getSelected();
    }

}
