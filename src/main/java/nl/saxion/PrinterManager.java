package nl.saxion;

import nl.saxion.Models.*;
import org.json.simple.JSONArray;

import java.util.*;

public class PrinterManager {
    //FIXME: code smell // no ERROR HANDLING anywhere in the project !!!!!!!!!!!!!
    // TODO: create SpoolManager and PrintTaskManager
    // FIXME: code smell // class has too much functionality and the name states that is PrinterManager, however it handles prints and spools as well so different manager classes should be created for them
    // FIXME: code smell // all lists are directly given an ArrayList variable type, this reduces flexibility so it can be substituted with List<?> / same for the hashmap
    private static PrinterManager instance;
    private List<Printer> printers = new ArrayList<Printer>(); //TODO use interface
    private List<Printer> freePrinters = new ArrayList<>();




    public static PrinterManager getInstance() {
        if (instance == null){
            instance = new PrinterManager();
        }
        return instance;
    }
    //FIXME: reduce addPrinter method args (create a class to pass or
    // FIXME: code smell / too many arguments / method can be reduced and simplified by substituing half of the args with a Printer object which can also make the method shorter
    public void addPrinter(int id, int printerType, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) {
        Printer newPrinter = PrinterFactory.createPrinterInstance(id,printerType, printerName, manufacturer, maxX, maxY, maxZ, maxColors);
        if (newPrinter != null) {
            printers.add(newPrinter);
            freePrinters.add(newPrinter);
        }
    }




    public List<Printer> getPrinters() {
        return printers;
    }

    public void addPrinter(Printer printer){
        printers.add(printer);
    }

    public void addFreePrinter(Printer printer){
        freePrinters.add(printer);
    }

    public void removePrinter(Printer printer){
        printers.remove(printer);
    }

    public void removeFreePrinter(Printer printer){
        freePrinters.remove(printer);
    }

}
