package nl.saxion.Models;

import java.util.ArrayList;
import java.util.List;

public class Print {
    private String name;
    private int height;
    private int width;
    private int length;
    //FIXME: Initialize as List(instead of ArrayList) for more flexibility- solution sparwl

    private List<Double> filamentLength;
    private int printTime;

    public Print(String name, int height, int width, int length, List<Double> filamentLength, int printTime) {
        this.name = name;
        this.height = height;
        this.width = width;
        this.length = length;
        this.filamentLength = filamentLength;
        this.printTime = printTime;
    }
    //Fixme ??? is the lineseparator a good use case
    @Override
    public String toString() {
        return "--------" + System.lineSeparator() +
                "- Name: " + name + System.lineSeparator() +
                "- Height: " + height + System.lineSeparator() +
                "- Width: " + width + System.lineSeparator() +
                "- Length: " + length + System.lineSeparator() +
                "- FilamentLength: " + filamentLength + System.lineSeparator() +
                "- Print Time: " + printTime + System.lineSeparator() +
                "--------";
    }
    //Fixme those methods are never used so remove them- dead code
    public String getName() {
        return name;
    }

    //Fixme it returns a double when in reality it is initialized as a integer
    public double getLength() {
        return length;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public List<Double> getFilamentLength() {
        return filamentLength;
    }
}
