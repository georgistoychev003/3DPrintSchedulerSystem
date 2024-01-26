package nl.saxion.Models;

import java.util.ArrayList;
import java.util.List;
//FIXME Uncommunicative name of the class as the naming conventions of the naming are not met

/* Printer capable of printing multiple colors. */
public class MultiColor extends StandardFDM {
    private int maxColors;
    //Fixme isnt the absense of spool one confusing?????

    private Spool spool2;
    private Spool spool3;
    private Spool spool4;

    public MultiColor(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) {
        super(id, printerName, manufacturer, maxX, maxY, maxZ);
        this.maxColors = maxColors;
    }

//    public void setCurrentSpools(List<Spool> spools) {
//        setCurrentSpool(spools.get(0));
//        if(spools.size() > 1) spool2 = spools.get(1);
//        if(spools.size() > 2) spool3 = spools.get(2);
//        if(spools.size() > 3) spool4 = spools.get(3);
//    }

    public void setCurrentSpools(List<Spool> spools) {

        int changesCount = 0;
        if (this.getCurrentSpool() != spools.get(0)) {
            setCurrentSpool(spools.get(0));
            changesCount++;
        }
        if (spools.size() > 1 && spool2 != spools.get(1)) {
            spool2 = spools.get(1);
            changesCount++;
        }
        if (spools.size() > 2 && spool3 != spools.get(2)) {
            spool3 = spools.get(2);
            changesCount++;
        }
        if (spools.size() > 3 && spool4 != spools.get(3)) {
            spool4 = spools.get(3);
            changesCount++;
        }
        if (changesCount > 0) {
            for (int i = 0; i < changesCount; i++) {
                notifyObservers("spoolChange", spools); // Notify observers of the spool change
            }
        }
    }
    //FIXME the value of spools is hardcoded-magic numbers

    @Override
    public Spool[] getCurrentSpools() {
        Spool[] spools = new Spool[4];
        spools[0] = getCurrentSpool();
        spools[1] = spool2;
        spools[2] = spool3;
        spools[3] = spool4;
        return spools;
    }
//Fixme is the toString too complex???

    @Override
    public String toString() {
        String result = super.toString();
        String[] resultArray = result.split("- ");
        String spools = resultArray[resultArray.length - 1];
        if (spool2 != null) {
            spools = spools.replace(System.lineSeparator(), ", " + spool2.getId() + System.lineSeparator());
        }
        if (spool3 != null) {
            spools = spools.replace(System.lineSeparator(), ", " + spool3.getId() + System.lineSeparator());
        }
        if (spool4 != null) {
            spools = spools.replace(System.lineSeparator(), ", " + spool4.getId() + System.lineSeparator());
        }
        spools = spools.replace("--------", "- maxColors: " + maxColors + System.lineSeparator() +
                "--------");
        resultArray[resultArray.length - 1] = spools;
        result = String.join("- ", resultArray);

        return result;
    }

    public int getMaxColors() {
        return maxColors;
    }
}
