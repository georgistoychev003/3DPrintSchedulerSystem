package nl.saxion.Models;

import java.util.Objects;

public class Spool {
    private final int id;
    private final String color;
    private final FilamentType filamentType;
    private double length;

    public Spool(int id, String color, FilamentType filamentType, double length) {
        this.id = id;
        this.color = color;
        this.filamentType = filamentType;
        this.length = length;
    }


    public int getId() {
        return this.id;
    }

    //Fixme this method is never used so remove them- dead code
    public double getLength() {
        return length;
    }

    //fixme is this bad and should this be done simpler , does this make it a code smell
    public boolean spoolMatch(String color, FilamentType type) {
        if(color.equals(this.color) && type == this.getFilamentType()) {
            return true;
        }
        return false;
    }
    /**
     * This method will try to reduce the length of the spool.
     *
     * @param byLength
     * @return boolean which tells you if it is possible or not.
     */

    //fixme incorrect method logic
    public boolean reduceLength(double byLength) {
        boolean success = true;
        this.length -= byLength;
        if (this.length < 0) {
            this.length -= byLength;
            success = false;
        }
        return success;
    }

    public String getColor() {
        return color;
    }

    public FilamentType getFilamentType(){
        return filamentType;
    }

    @Override
    public String toString() {
        return  "<--------" + System.lineSeparator() +
                "- id: " + id + System.lineSeparator() +
                "- color: " + color + System.lineSeparator() +
                "- filamentType: " + filamentType + System.lineSeparator() +
                "- length: " + length + System.lineSeparator() +
                "-------->";
    }
}
