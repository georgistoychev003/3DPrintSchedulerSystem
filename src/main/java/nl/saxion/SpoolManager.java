package nl.saxion;

import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Spool;

import java.util.ArrayList;
import java.util.List;

public class SpoolManager {

    private List<Spool> spools = new ArrayList<Spool>(); //TODO use interface
    private List<Spool> freeSpools = new ArrayList<>();

    public List<Spool> getNeededSpools(PrintTask printTask) {
        List<Spool> chosenSpools = new ArrayList<>();
        for (int i = 0; i < printTask.getColors().size(); i++) {
            for (Spool spool : freeSpools) {
                if (spool.spoolMatch(printTask.getColors().get(i), printTask.getFilamentType()) && !containsSpool(chosenSpools, printTask.getColors().get(i))) {
                    chosenSpools.add(spool);
                }
            }
        }
        return chosenSpools;
    }

    // FIXME: code smell??? /// naming can be more meaningful as we search the spool from the color and not a name variable that does not exist in the spool class, also list could be replaced with spools
    public boolean containsSpool(final List<Spool> list, final String name){
        return list.stream().anyMatch(o -> o.getColor().equals(name));
    }

    public void addNewSpool(Spool spool) {
        spools.add(spool);
        freeSpools.add(spool);
    }

    public void addSpool(Spool spool){
        spools.add(spool);
    }

    public void addFreeSpool(Spool spool){
        freeSpools.add(spool);
    }

    public void removeFreeSpool(Spool spool){
        freeSpools.remove(spool);
    }

    public void removeSpool(Spool spool){
        spools.remove(spool);
    }

    public List<Spool> getSpools() {
        return spools;
    }

    public List<Spool> getFreeSpools() {
        return freeSpools;
    }

    //FIXME: code smell // dead code
    public Spool getSpoolByID(int id) {
        for(Spool s: spools) {
            if(s.getId() == id) {
                return s;
            }
        }
        return null;
    }

}
