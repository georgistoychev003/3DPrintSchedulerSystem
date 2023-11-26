package models;

import java.util.ArrayList;

public class Print implements ParsedString{
    private String name;
    private int height;
    private int width;
    private int length;
    private int printTime;
    private ArrayList<Double> filamentLength =  new ArrayList<>();

    public String getName() {
        return name;
    }

    public Print(String name,  int width, int length, int height, int printTime, double filamentLength) {
        this.name = name;
        this.height = height;
        this.width = width;
        this.length = length;
        this.printTime = printTime;
        this.filamentLength.add(filamentLength);
    }

    public Print(String name, int height, int width, int length, int printTime, double filamentLength, double length2) {
        this(name, height, width, length, printTime, filamentLength);
        this.filamentLength.add(length2);
    }

    public Print(String name, int height, int width, int length, int printTime, double filamentLength, double length2, double length3) {
        this(name, height, width, length, printTime, filamentLength, length2);
        this.filamentLength.add(length3);
    }

    public Print(String name, int height, int width, int length, int printTime, double filamentLength, double length2, double length3, double length4) {
        this(name, height, width, length, printTime, filamentLength, length2, length3);
        this.filamentLength.add(length4);
    }

    public String parsedString() {
        return "- Name: "+name+
                " - Height: " + height +
                " - Width: " + width +
                " - Length: " + length +
                " - FilamentLength: " + filamentLength +
                " - Print Time: " +printTime ;
    }

    @Override
    public String toString() {
        String print = "Name: "+name+ System.lineSeparator() +
                "Height: " + height + System.lineSeparator() +
                "Width: " + width + System.lineSeparator() +
                "Length: " + length + System.lineSeparator() +
                "FilamentLength: " + filamentLength + System.lineSeparator() +
                "Print Time: " +printTime ;
        return print;
    }
}
