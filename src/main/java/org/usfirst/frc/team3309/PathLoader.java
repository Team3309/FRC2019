package org.usfirst.frc.team3309;

import jaci.pathfinder.Pathfinder;
import jaci.pathfinder.Trajectory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

public class PathLoader {

    public enum Path {

        HabToRocketLeft("HAB_To_FR_F"),
        RocketToTurnLeft("FR_To_FRTurn_B"),
        TurnToFeederLeft("FRTurn_To_Feeder_F"),
        FeederToBR("Feeder_To_BR_B"),
        ForwardToCargoShipTest("Hab_To_FCS_F"),
        BackFromCargoShipTest("FCS_To_HAB_B");

        public String name;

        Path(String name) {
            this.name = name;
        }

    }

    private static String kPathLocation = "/home/lvuser/deploy/output/output/";
    private static String kPathSuffix = ".pf1.csv";

    public static LinkedHashMap<Path, Trajectory> loadPaths(){
        LinkedHashMap<Path, Trajectory> paths = new LinkedHashMap<>();

        for (Path path : Path.values()) {
            paths.put(path, getTraj(path.name));
        }

        return paths;
    }

    private static Trajectory getTraj(String name){
        try{
            return Pathfinder.readFromCSV(
                new File(
                    kPathLocation + name + kPathSuffix
                )
            );
        }catch(IOException e){
            System.out.println("!!!!!!!!!! IO Exception on Reading Traj !!!!!!!!!!");
            return null;
        }
    }
}