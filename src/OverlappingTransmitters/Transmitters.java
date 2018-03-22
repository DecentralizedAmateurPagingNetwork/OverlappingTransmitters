package OverlappingTransmitters;

import javafx.util.Pair;

import java.io.File;
import java.util.HashMap;

/**
 * Created by wilke on 22.03.2018.
 */
public class Transmitters {
    private HashMap<String, Transmitter> AllTransmitters = new HashMap<String,Transmitter>();
    private HashMap<Pair<Transmitter,Transmitter>, Boolean> PotentialOverlappingTransmitters = new HashMap<Pair<Transmitter,Transmitter>, Boolean>();
    private HashMap<Pair<Transmitter,Transmitter>, Boolean> OverlappingTransmitters = new HashMap<Pair<Transmitter,Transmitter>, Boolean>();

    public void readAllTransmittersMetaData (String Directory) {
        // Get all '.txt' Filenames in Directory
        File dir = new File(Directory);
        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        // Add Transmitter and parse meta data for all Transmitters
        for (int i = 0; i < files.length; i++) {
//        for (int i = 0; i < 50; i++) {
            //Transmitter T = new Transmitter(files[0].getAbsolutePath());
            addTransmitter(new Transmitter(files[i].getAbsolutePath()));
        }
    }

    // Add Transmitter to Hashmap
    public void addTransmitter (Transmitter T) {
        this.AllTransmitters.put(T.getName(),T);
    }

    public void findPotentialOverlappingTransmitters() {
        // Iterator though all Transmitters and check, if coverage images are overlapping
        for (Transmitter T1 : AllTransmitters.values()) {
            for (Transmitter T2 : AllTransmitters.values()) {
                if (T1.equals(T2)) {
                    continue;
                }

                if (this.PotentialOverlappingTransmitters.containsKey(new Pair<Transmitter,Transmitter>(T1,T2)) ||
                        this.PotentialOverlappingTransmitters.containsKey(new Pair<Transmitter,Transmitter>(T2,T1))) {
                    continue;
                }

                if ( intIsBetween(T1.getLongMin(), T1.getLongMax(), T2.getLongMin()) ||
                     intIsBetween(T1.getLongMin(), T1.getLongMax(),T2.getLongMax()) ||
                     intIsBetween(T1.getLatMin(), T1.getLatMax(),T2.getLatMin()) ||
                     intIsBetween(T1.getLatMin(), T1.getLatMax(),T2.getLatMax()) ) {
                    this.PotentialOverlappingTransmitters.put(new Pair<Transmitter,Transmitter>(T1,T2), true);
                }
            }
        }
    }


    public void verifyOverlapping () {
        for (Pair<Transmitter,Transmitter> PairT : this.PotentialOverlappingTransmitters.keySet()) {
            Transmitter T1 = PairT.getKey();
            Transmitter T2 = PairT.getValue();

            System.out.println("Checking " + T1.getName() + " against " + T2.getName() + "...");
            if (T1.isOverlapping(T2)) {
                this.OverlappingTransmitters.put(new Pair<Transmitter,Transmitter>(T1,T2), true);
            }
        }
    }

    public void printPotentialOverlappingTransmitters ()
    {
        for (Pair<Transmitter,Transmitter> PairT : this.PotentialOverlappingTransmitters.keySet()) {
            System.out.println ("Potential overlapping: " + PairT.getKey().getName() + " and " + PairT.getValue().getName());
        }
        System.out.println("Number of pot. overlapping Transmitters: " + this.PotentialOverlappingTransmitters.size());
    }

    public void printOverlappingTransmitters ()
    {
        for (Pair<Transmitter,Transmitter> PairT : this.OverlappingTransmitters.keySet()) {
            System.out.println ("Overlapping: " + PairT.getKey().getName() + " and " + PairT.getValue().getName());
        }
        System.out.println("Number of overlapping Transmitters: " + this.OverlappingTransmitters.size());
    }


    private boolean intIsBetween (int lower, int higher, int value) {
        return (value >= lower && value <= higher);
    }
}
