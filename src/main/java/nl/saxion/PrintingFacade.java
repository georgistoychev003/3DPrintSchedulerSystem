package nl.saxion;

import nl.saxion.Models.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;

public class PrintingFacade implements IFacade{

    private DomainReader fileHandler;


    public PrintingFacade() {
        this.fileHandler = new JSONDomainReader();
    }

    @Override
    public int printCurrentlyRunningPrinters() {
        List<Printer> printers = getPrinterManager().getPrinters();
        System.out.println("---------- Currently Running Printers ----------");
        for(Printer p: printers) {
            PrintTask printerCurrentTask= getPrintTaskManager().getPrinterCurrentTask(p);
            if(printerCurrentTask != null) {
                System.out.println("- " + p.getId() + ": " +p.getName() + " - " + printerCurrentTask);
            }
        }

        return printers.size();
    }

    @Override
    public void startPrintQueue() {
        getPrintTaskManager().startInitialQueue();
    }

    @Override
    public void registerSucceededPrinter(int printerId) {
        getPrintTaskManager().registerCompletion(printerId);
    }

    @Override
    public void registerFailedPrinter(int printerId) {
        getPrintTaskManager().registerPrinterFailure(printerId);
    }

    @Override
    public void createPrintTask(String printName, List<String> colors, FilamentType type) {
        getPrintTaskManager().addPrintTask(printName, colors, type);
    }

    @Override
    public List<String> getAvailableColors(FilamentType filamentType) {
        List<Spool> spools = getSpools();
        Set<String> availableColors = new HashSet<>();
        for (Spool spool : spools) {
            String colorString = spool.getColor();
            if(filamentType == spool.getFilamentType()) {
                availableColors.add(colorString);
            }
        }
        return availableColors.stream().toList();
    }

    @Override
    public Print findSelectedPrint(int printNumber) {
        return getPrintManager().findPrint(printNumber - 1);
    }

    @Override
    public List<FilamentType> getFilamentTypes() {
        return new ArrayList<>(EnumSet.allOf(FilamentType.class));
    }

    @Override
    public FilamentType getSelectedFilamentType(int filamentTypeNumber, List<FilamentType> filamentTypes) {
        if (filamentTypeNumber > filamentTypes.size() || filamentTypeNumber < 1){
            // TODO: throw exception
            System.out.println("Not a valid filament type.");
            return null;
        }
        return filamentTypes.get(filamentTypeNumber-1);
    }

    public PrintingStrategy getPrintingStrategy() {
        return getPrintTaskManager().getPrintingStrategy();
    }

    public void changePrintingStrategy(PrintingStrategy printingStrategy) {
        getPrintTaskManager().changePrintingStrategy(printingStrategy);
    }

    @Override
    public void printPrints() {

    }

    @Override
    public List<Print> getPrints() {
        return getPrintManager().getPrints();
    }

    @Override
    public List<Spool> getSpools() {
        return getSpoolManager().getSpools();
    }

    @Override
    public List<Printer> getPrinters() {
        return getPrinterManager().getPrinters();
    }

    @Override
    public PrintTask getCurrentTaskOfAPrinter(Printer p) {
        return getPrintTaskManager().getPrinterCurrentTask(p);
    }

    @Override
    public List<PrintTask> getPendingPrintTasks() {
        return getPrintTaskManager().getPendingPrintTasks();
    }

//    public List<PrinterDTO> getPrinters() {
//        return getPrinterManager().getPrinters().stream().map(p -> p.asDTO()).collect(Collectors.toList());
//    }

    @Override
    public void readPrintersFromFile(String filename) {
        List<Printer> printers = fileHandler.readPrinters();
        for (Printer printer : printers) {
            getPrinterManager().addPrinter(printer);
        }
    }


    @Override
    public void readPrintsFromFile(String filename) {
        List<Print> prints = fileHandler.readPrints();
        for (Print print : prints) {
            getPrintManager().addPrint(print);
        }
    }

    @Override
    public void readSpoolsFromFile(String filename) {
        List<Spool> spools = fileHandler.readSpools();
        for (Spool spool : spools) {
            getSpoolManager().addSpool(spool);
        }
    }


    private PrinterManager getPrinterManager() {
        return PrinterManager.getInstance();
    }
    private PrintTaskManager getPrintTaskManager() {
        return PrintTaskManager.getInstance();
    }
    private PrintManager getPrintManager() {
        return PrintManager.getInstance();
    }

    private SpoolManager getSpoolManager() {
        return SpoolManager.getInstance();
    }

}
