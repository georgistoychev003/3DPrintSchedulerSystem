package nl.saxion;

import nl.saxion.Models.Print;
import nl.saxion.Models.Printer;

import java.util.ArrayList;
import java.util.List;

public class PrintManager {

    private static PrintManager instance;
    private List<Print> prints = new ArrayList<Print>();

    private PrintManager() {

    }

    public static PrintManager getInstance() {
        if (instance == null) {
            instance = new PrintManager();
        }
        return instance;
    }


    public void addPrint(Print print) {
        if (print != null) {
            prints.add(print);
        }
    }

    public void readPrintsFromFile(String filename) {
        if (filename == null || filename.isEmpty()) {
            System.out.println("No filename provided for reading prints.");
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

        List<Print> printsFromFile = fileHandler.readPrints(filename);
        for (Print print : printsFromFile) {
            addPrint(print);
        }
    }


    private DomainReader getJsonFileHandler() {
        return JSONDomainReader.getInstance();
    }

    private DomainReader getCsvFileHandler() {
        return CSVDomainReader.getInstance();
    }

    public List<Print> getPrints() {
        return prints;
    }

    //FIXME: code smell??? // can be replaced with stream
    public Print findPrint(String printName) {
        for (Print p : prints) {
            if (p.getName().equals(printName)) {
                return p;
            }
        }
        return null;
    }


    public Print findPrint(int index) {
        if (index > prints.size() - 1) {
            return null;
        }
        return prints.get(index);
    }


}
