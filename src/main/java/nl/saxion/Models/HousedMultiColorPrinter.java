package nl.saxion.Models;

import java.util.List;

public class HousedMultiColorPrinter extends MultiColor{

    private Spool spool5;
    private Spool spool6;
    public HousedMultiColorPrinter(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) {
        super(id, printerName, manufacturer, maxX, maxY, maxZ, maxColors);
    }

    @Override
    public void setCurrentSpools(List<Spool> spools) {
        super.setCurrentSpools(spools.subList(0, Math.min(spools.size(), 4)));
        int changesCount = 0;

        if (spools.size() > 5 && spool5 != spools.get(4)) {
            spool5 = spools.get(4);
            changesCount++;
        }
        if (spools.size() > 6 && spool6 != spools.get(5)) {
            spool6 = spools.get(5);
            changesCount++;
        }
        if (changesCount > 0) {
            for (int i = 0; i < changesCount; i++) {
                notifyObservers("spoolChange", spools); // Notify observers of the spool change
            }
        }
    }

    @Override
    public Spool[] getCurrentSpools() {
        Spool[] spools = new Spool[super.getCurrentSpools().length + 2];
        for (int i = 0; i < super.getCurrentSpools().length; i++) {
            spools[i] = super.getCurrentSpools()[i];
        }
        spools[4] = spool5;
        spools[5] = spool6;
        return spools;
    }

    @Override
    public String toString() {
        String result = super.toString(); // Get the string representation from the parent class
        String[] resultArray = result.split("- ");
        String spoolsString = resultArray[resultArray.length - 1];

        if (spool5 != null) {
            spoolsString += ", " + spool5.getId();
        }
        if (spool6 != null) {
            spoolsString += ", " + spool6.getId();
        }

        spoolsString = spoolsString.replace("--------", "- maxColors: " + getMaxColors());
        resultArray[resultArray.length - 1] = spoolsString;
        result = String.join("- ", resultArray);

        return result;

    }

    @Override
    public int getMaxColors() {
        return super.getMaxColors();
    }
}
