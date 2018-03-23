package OverlappingTransmitters;

import static java.lang.Math.round;

public class Main {

    public static void main(String[] args) {
    Transmitters Ts = new Transmitters();
    Ts.readAllTransmittersMetaData("Coverage");
    Ts.findPotentialOverlappingTransmitters();
    Ts.printPotentialOverlappingTransmitters();
    Ts.verifyOverlapping();
    Ts.printOverlappingTransmitters();
    Ts.saveOverlappingTransmitters("overlap.txt");
    System.out.println("Memory needed: " + round ((double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024) + " MB");
    }
}
