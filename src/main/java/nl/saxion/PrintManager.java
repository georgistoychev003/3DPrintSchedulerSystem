package nl.saxion;

import nl.saxion.Models.Print;
import nl.saxion.Models.Printer;

import java.util.ArrayList;
import java.util.List;

public class PrintManager {

    private static PrintManager instance;
    private List<Print> prints = new ArrayList<Print>(); //TODO use interface

    private PrintManager() {

    }

    public static PrintManager getInstance() {
        if (instance == null){
            instance = new PrintManager();
        }
        return instance;
    }

    //FIXME: code smell // too many arguments / all can be replaced by a Print object
//    public void addPrint(String name, int height, int width, int length, ArrayList<Double> filamentLength, int printTime) {
//        Print p = new Print(name, height, width, length, filamentLength, printTime);
//        prints.add(p);
//    }

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

        List<Print> printsFromFile = fileHandler.readPrints();
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
    //FIXME: code smell // make methods that are used only within this class private
    //FIXME: code smell??? // can be replaced with stream ( i think)
    public Print findPrint(String printName) {
        for (Print p : prints) {
            if (p.getName().equals(printName)) {
                return p;
            }
        }
        return null;
    }


    public Print findPrint(int index) {
        if(index > prints.size() -1) {
            return null;
        }
        return prints.get(index);
    }


}
