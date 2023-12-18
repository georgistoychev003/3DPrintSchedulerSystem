package nl.saxion.Models;

import java.util.List;

public class ConcretePrinter extends Printer{

    private int maxX;
    private int maxY;
    private int maxZ;
    private int maxColors;
    public ConcretePrinter(int id, String name, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) {
        super(id, name, manufacturer);
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.maxColors = maxColors;
    }

    @Override
    public int CalculatePrintTime(String filename) {
        return 0;
    }

    @Override
    public Spool[] getCurrentSpools() {
        return new Spool[0];
    }

    @Override
    public void setCurrentSpools(List<Spool> spools) {

    }

    @Override
    public boolean printFits(Print print) {
        return false;
    }
}
