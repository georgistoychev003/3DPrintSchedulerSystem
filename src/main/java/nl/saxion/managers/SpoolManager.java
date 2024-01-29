package nl.saxion.managers;

import nl.saxion.Models.PrintTask;
import nl.saxion.Models.Spool;
import nl.saxion.adapter.CSVDomainReader;
import nl.saxion.adapter.DomainReader;
import nl.saxion.adapter.JSONDomainReader;

import java.util.ArrayList;
import java.util.List;

public class SpoolManager {

    private static SpoolManager instance;
    private List<Spool> spools = new ArrayList<Spool>();
    private List<Spool> freeSpools = new ArrayList<>();


    private SpoolManager() {

    }

    public static SpoolManager getInstance() {
        if (instance == null) {
            instance = new SpoolManager();
        }
        return instance;
    }

    public List<Spool> getNeededSpools(PrintTask printTask) {
        List<Spool> chosenSpools = new ArrayList<>();
        for (int i = 0; i < printTask.getColors().size(); i++) {
            for (Spool spool : freeSpools) {
                if (spool != null) {
                    if (spool.spoolMatch(printTask.getColors().get(i), printTask.getFilamentType()) && !containsSpool(chosenSpools, printTask.getColors().get(i))) {
                        chosenSpools.add(spool);
                    }
                }
            }
        }
        return chosenSpools;
    }

    public boolean containsSpool(final List<Spool> list, final String color) {
        return list.stream().anyMatch(o -> o.getColor().equals(color));
    }

    public void readSpoolsFromFile(String filename) {
        if (filename == null || filename.isEmpty()) {
            System.out.println("No filename provided for reading spools.");
            return;
        }

        DomainReader fileHandler;
        if (getJsonFileHandler().supportsFileType(filename)) {
            fileHandler = getJsonFileHandler();
        } else if (getCsvFileHandler().supportsFileType(filename)) {
            fileHandler = getCsvFileHandler();
        } else {
            System.out.println("Unsupported file type for filename: " + filename);
            return;
        }

        List<Spool> spoolsFromFile = fileHandler.readSpools(filename);
        for (Spool spool : spoolsFromFile) {
            addNewSpool(spool);
        }
    }

    private DomainReader getJsonFileHandler() {
        return JSONDomainReader.getInstance();
    }

    private DomainReader getCsvFileHandler() {
        return CSVDomainReader.getInstance();
    }

    public void addNewSpool(Spool spool) {
        spools.add(spool);
        freeSpools.add(spool);
    }

    public void addSpool(Spool spool) {
        spools.add(spool);
    }

    public void addFreeSpool(Spool spool) {
        freeSpools.add(spool);
    }

    public void removeFreeSpool(Spool spool) {
        freeSpools.remove(spool);
    }

    public void removeSpool(Spool spool) {
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
        for (Spool s : spools) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

}
