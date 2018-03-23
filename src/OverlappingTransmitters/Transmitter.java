package OverlappingTransmitters;

import javafx.util.Pair;
import org.apache.commons.io.FilenameUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Locale;
import java.util.Scanner;

import static java.lang.Math.floor;
import static java.lang.Math.floorDiv;
import static org.apache.commons.io.FilenameUtils.removeExtension;

public class Transmitter {

    private HashMap<Pair<Integer,Integer>, Integer> CoverageHMap = new HashMap<Pair<Integer,Integer>, Integer>();

    private int LatMax = 0;
    private int LatMin = 0;
    private int LongMax = 0;
    private int LongMin = 0;
    private String ImageFilename = null;
    private String Filename = null;
    private String Name = null;
    private int width = 0;
    private int height = 0;

    private int width_reduced = 0;
    private int height_reduced = 0;

    private static final int PixelStepSize = 10;
    private static final int CoordinateStepSize = 100;
    private static final int CoordianteDecimalCorrection = 10000;



    public Transmitter (String Filename) {
        this.Filename = Filename;
        this.Name = FilenameUtils.getName(removeExtension(this.Filename));
        this.ImageFilename = FilenameUtils.getFullPath(this.Filename).concat(this.Name).concat(".png");
        ReadMetaData();
        getCoverageImage(false);
    }

    public void getCoverageImage(boolean SaveProcessedImages)
    {
        BufferedImage img = null;

        try {
            img = ImageIO.read(new File(this.ImageFilename));
        } catch (IOException e) {
        }

        this.width = img.getWidth();
        this.height = img.getHeight();
        this.width_reduced = floorDiv(this.width, this.PixelStepSize);
        this.height_reduced = floorDiv(this.height, this.PixelStepSize);

        BufferedImage img_reduced = new BufferedImage(this.width_reduced, this.height_reduced, BufferedImage.TYPE_INT_RGB);
        int ColorGray = Color.LIGHT_GRAY.getRGB();


        int h = 0;
        int w = 0;

        while (h < (this.height_reduced * this.PixelStepSize)) {
            w = 0;
            boolean Skiph = false;
            int h_reduced = 0;

            while (w < (this.width_reduced * this.PixelStepSize)) {
                int argb = img.getRGB(w, h);
                int alpha = (argb >> 24) & 0xff;
                if (alpha != 0) {
                    h_reduced = floorDiv(h, this.PixelStepSize);
                    int w_reduced = floorDiv(w, this.PixelStepSize);
                    this.CoverageHMap.put(Pixel2Coordinates(h_reduced, w_reduced), alpha);

                    if (SaveProcessedImages) {
                        img_reduced.setRGB(w_reduced, h_reduced, ColorGray);
                    }

                    w = (w_reduced + 1) * this.PixelStepSize;
                    Skiph = true;
                } else {
                    w++;
                }
            }
            if (Skiph) {
                h = (h_reduced + 1 ) * this.PixelStepSize;
            } else {
                h++;
            }
        }
        img = null;

        if (SaveProcessedImages) {
            try {
                ImageIO.write(img_reduced, "png", new File("Coverage/" + this.getName() + "_reduced.png"));
                System.out.println("Processed image of " + this.getName() + " written");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println("Finished loading " + this.getName() + ". Number of coordinate pairs: " + this.CoverageHMap.size());
    }

    public String getName (){
        return this.Name;
    }

    private void ReadMetaData() {
        Scanner scanner;
        try {
            scanner = new Scanner(new BufferedReader(new FileReader(this.Filename)));
            // Decimals have . as decimal separator
            scanner.useLocale(Locale.ENGLISH);

            // ignore first line for now; Example content: 200318_14:00
            if (scanner.hasNext()) {
                scanner.next();
            }
            double d;
            if (scanner.hasNextDouble()) {
                d = scanner.nextDouble();
                this.LatMax = (int) (d * this.CoordianteDecimalCorrection);
            }
            if (scanner.hasNextDouble()) {
                d = scanner.nextDouble();
                this.LatMin = (int) (d * this.CoordianteDecimalCorrection);
            }
            if (scanner.hasNextDouble()) {
                d = scanner.nextDouble();
                this.LongMin = (int) (d * this.CoordianteDecimalCorrection);
            }
            if (scanner.hasNextDouble()) {
                d = scanner.nextDouble();
                this.LongMax = (int) (d * this.CoordianteDecimalCorrection);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Pair<Integer,Integer> Pixel2Coordinates(int h, int w) {
        double long_interpol = LinearInterpolation(w,0, this.LongMin, this.width_reduced, this.LongMax);
        double lat_interpol = LinearInterpolation(h,0, this.LatMax, this.height_reduced, this.LatMin);

        int long_reduced = (int) floor(long_interpol/this.CoordinateStepSize) * this.CoordinateStepSize;
        int lat_reduced = (int) floor(lat_interpol/this.CoordinateStepSize) * this.CoordinateStepSize;

        Pair<Integer,Integer> result = new Pair<Integer, Integer>(long_reduced,lat_reduced);
        return result;
    }

    public boolean isOverlapping (Transmitter T) {
        for (Pair<Integer,Integer> PairThisT : this.CoverageHMap.keySet()) {
            if (T.getCoverageHMap().containsKey(PairThisT)) {
                System.out.print("Found overlap of " + this.getName() + " and " + T.getName() + " at ");
                System.out.print((double) PairThisT.getValue() / this.CoordianteDecimalCorrection);
                System.out.print(", ");
                System.out.println((double) PairThisT.getKey() /  this.CoordianteDecimalCorrection);
                return true;
            }
        }
        return false;
    }

    private double LinearInterpolation (int x, int x1, int y1, int x2, int y2) {
/*        double m = ((y2-y1)/(x2-x1));
        double b = y2 - (m * x2);
        return ((m * x) + b);
*/
        return ((y2-y1)/(x2-x1))*(x-x1)+y1;
    }

    public int getLatMax() {
        return LatMax;
    }

    public int getLatMin() {
        return LatMin;
    }

    public int getLongMax() {
        return LongMax;
    }

    public int getLongMin() {
        return LongMin;
    }

    public HashMap<Pair<Integer, Integer>, Integer> getCoverageHMap() {
        return CoverageHMap;
    }
}