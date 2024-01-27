package nl.saxion.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
//FIXME Uncommunicative name of the class as the naming conventions of the naming are not met

/* Printer capable of printing multiple colors. */
public class MultiColor extends StandardFDM {
    private int maxColors;
    private Spool[] spools;

    private boolean isHoused;

    public MultiColor(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) {
        super(id, printerName, manufacturer, maxX, maxY, maxZ);
        this.maxColors = maxColors;
        spools = new Spool[maxColors];
    }

    @Override
    public void setCurrentSpools(List<Spool> spools) {
        if (maxColors < spools.size()) {
            throw new IllegalArgumentException("Cannot exceed max colors of Multicolor printer");
        }


        int counter = 0;
        for (int i = 0; i < maxColors && i < spools.size(); i++) {
            if (this.spools[i] != spools.get(i)) {
                this.spools[i] = spools.get(i);
                counter++;
            }
        }

        if (counter > 0) {
            for (int i = 0; i < counter; i++) {
                notifyObservers("spoolChange", spools); // Notify observers of the spool change
            }
        }
    }

    @Override
    public Spool[] getCurrentSpools() {
        return spools;
    }

    @Override
    public String toString() {
        String result = super.toString();
        String[] resultArray = result.split("- ");
        String spoolsString = resultArray[resultArray.length - 1];

        if (spools.length != 0) {
            StringBuilder spoolIds = new StringBuilder();
            for (int i = 0; i < spools.length; i++) {
                if (spools[i] != null) {
                    if (!spoolIds.isEmpty()) {
                        spoolIds.append(", ");
                    }
                    spoolIds.append(spools[i].getId());
                }
            }

            spoolsString = spoolsString.replace("--------", "- spoolIds: " + spoolIds.toString() + System.lineSeparator() +
                    "--------");
            resultArray[resultArray.length - 1] = spoolsString;
            result = String.join("- ", resultArray);
        }

        return result;
    }

    @Override
    public boolean isHoused() {
        return isHoused;
    }

    @Override
    public void setHoused(boolean housed) {
        isHoused = housed;
    }

    public int getMaxColors() {
        return maxColors;
    }
}
