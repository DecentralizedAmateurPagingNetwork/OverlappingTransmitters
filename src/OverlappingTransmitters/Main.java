package OverlappingTransmitters;

public class Main {

    public static void main(String[] args) {
    Transmitters Ts = new Transmitters();
    Ts.readAllTransmittersMetaData("Coverage");
    Ts.findPotentialOverlappingTransmitters();
    Ts.printPotentialOverlappingTransmitters();
    Ts.verifyOverlapping();
    Ts.printOverlappingTransmitters();
    System.out.println("MB: " + (double) (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1024 / 1024);
    }
}
