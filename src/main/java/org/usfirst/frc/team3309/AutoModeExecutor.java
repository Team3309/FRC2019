package org.usfirst.frc.team3309;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.autos.AutoTestPath;
import org.usfirst.frc.team3309.commands.drive.CharacterizeHighGearTurn;
import org.usfirst.frc.team4322.commandv2.Command;

public class AutoModeExecutor {

    private static SendableChooser<Command> autos = new SendableChooser<>();

    public static void displayAutos() {
//        autos.addOption("Ramsete Path Test", AutoTestPath.to());
//        autos.addOption("Characterize Drive", CharacterizeHighGearTurn.to());
        autos.setDefaultOption("No Auto",null);

        SmartDashboard.putData("Autos: ", autos);
    }

    public static Command getAutoSelected() {
        return autos.getSelected();
    }

}