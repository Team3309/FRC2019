package org.usfirst.frc.team3309;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import org.usfirst.frc.team3309.commands.drive.auto.MoveCommand;
import org.usfirst.frc.team4322.commandv2.Command;

public class AutoModeExecutor {

    private static SendableChooser<MoveCommand.FieldSide> autos = new SendableChooser<>();

    public static void displayAutos() {
        autos.setDefaultOption("LeftSide", MoveCommand.FieldSide.LEFT);
        autos.addOption("RightSide", MoveCommand.FieldSide.RIGHT);
        SmartDashboard.putData("Autos: ", autos);
    }

    public static MoveCommand.FieldSide getSideSelected() {
        return autos.getSelected();
    }

}