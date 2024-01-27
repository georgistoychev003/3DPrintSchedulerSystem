package nl.saxion.Models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
//FIXME Uncommunicative name of the class as the naming conventions of the naming are not met

/* Printer capable of printing multiple colors. */
public class MultiColor extends StandardFDM {
    private int maxColors;
    //Fixme isnt the absense of spool one confusing?????

//    private Spool spool2;
//    private Spool spool3;
//    private Spool spool4;
    private List<Spool> spools;

    private boolean isHoused;

    public MultiColor(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) {
        super(id, printerName, manufacturer, maxX, maxY, maxZ);
        this.maxColors = maxColors;
        spools = new ArrayList<>(maxColors);
    }

//    public void setCurrentSpools(List<Spool> spools) {
//        setCurrentSpool(spools.get(0));
//        if(spools.size() > 1) spool2 = spools.get(1);
//        if(spools.size() > 2) spool3 = spools.get(2);
//        if(spools.size() > 3) spool4 = spools.get(3);
//    }

    public void setCurrentSpools(List<Spool> spools) {
        if (maxColors < spools.size()) {
            throw new IllegalArgumentException("Cannot exceed max colors of Multicolor printer");
        }
        AtomicInteger counter = new AtomicInteger(0);
        this.spools.forEach((spool) -> {
            if (spool != spools.get(counter.get())) {
                counter.incrementAndGet();
                this.spools.set(counter.get(), spools.get(counter.get()));
            }
        });

        if (counter.get() > 0) {
            for (int i = 0; i < counter.get(); i++) {
                notifyObservers("spoolChange", spools); // Notify observers of the spool change
            }
        }
    }
    //FIXME the value of spools is hardcoded-magic numbers

    @Override
    public Spool[] getCurrentSpools() {
        return this.spools.toArray(new Spool[maxColors]);
    }
//Fixme is the toString too complex???

    @Override
    public String toString() {
        String result = super.toString();
        String[] resultArray = result.split("- ");
        String spoolsString = resultArray[resultArray.length - 1];

        if (!spools.isEmpty()) {
            StringBuilder spoolIds = new StringBuilder();
            for (Spool spool : spools) {
                if (!spoolIds.isEmpty()) {
                    spoolIds.append(", ");
                }
                spoolIds.append(spool.getId());
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
