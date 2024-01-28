package nl.saxion;

import nl.saxion.Models.*;
import nl.saxion.Models.Observer;
import org.json.simple.JSONArray;

import java.util.*;

public class PrinterManager {
    private static PrinterManager instance;
    private List<Printer> printers = new ArrayList<Printer>();
    private List<Printer> freePrinters = new ArrayList<>();
    private Dashboard dashboard = new Dashboard();


    public static PrinterManager getInstance() {
        if (instance == null) {
            instance = new PrinterManager();
        }
        return instance;
    }

    //FIXME: reduce addPrinter method args (create a class to pass)
    // FIXME: code smell / too many arguments / method can be reduced and simplified by substituing half of the args with a Printer object which can also make the method shorter
    public void addPrinter(int id, int printerType, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) {
        Printer newPrinter = PrinterFactory.createPrinterInstance(id, printerType, printerName, manufacturer, maxX, maxY, maxZ, maxColors);
        if (newPrinter != null) {
            printers.add(newPrinter);
            freePrinters.add(newPrinter);
        }
    }

    public void addPrinter(Printer printer) {
        printers.add(printer);
    }

    public void addFreePrinter(Printer printer) {
        freePrinters.add(printer);
    }

    public void removePrinter(Printer printer) {
        printers.remove(printer);
    }

    public void removeFreePrinter(Printer printer) {
        freePrinters.remove(printer);
    }

    public void initializeDashboard() {
        for (Printer printer : printers) {
            printer.registerObserver(dashboard);
        }
    }

    public void readPrintersFromFile(String filename) {
        if (filename == null || filename.isEmpty()) {
            System.out.println("No filename provided for reading printers.");
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

        List<Printer> printersFromFile = fileHandler.readPrinters(filename);
        for (Printer printer : printersFromFile) {
            addPrinter(printer);
            addFreePrinter(printer);
        }
    }

    private DomainReader getJsonFileHandler() {
        return JSONDomainReader.getInstance();
    }

    private DomainReader getCsvFileHandler() {
        return CSVDomainReader.getInstance();
    }

    public void displayDashboardStats() {
        dashboard.displayStats();
    }

    public List<Printer> getPrinters() {
        return printers;
    }

    public List<Printer> getFreePrinters() {
        return freePrinters;
    }

}
