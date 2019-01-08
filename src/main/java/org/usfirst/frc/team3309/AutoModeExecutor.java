package org.usfirst.frc.team3309;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.autos.TestPath;

public class AutoModeExecutor {

    private static SendableChooser<Command> autos = new SendableChooser<>();

    public static void displayAutos() {

        autos.addOption("Ramsete Path Test", new TestPath());

        SmartDashboard.putData("Autos: ", autos);
    }

    public static Command getAutoSelected() {
        return autos.getSelected();
    }

}
