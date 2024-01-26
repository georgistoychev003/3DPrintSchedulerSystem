package nl.saxion;

import nl.saxion.Models.*;

import java.util.*;

public class PrintingFacade implements IFacade{

    private DomainReader jsonFileHandler;
    private DomainReader csvFileHandler;


    public PrintingFacade() {
        this.jsonFileHandler = new JSONDomainReader();
        this.csvFileHandler = new CSVDomainReader();
    }

    @Override
    public List<Printer> printCurrentlyRunningPrinters() {
        List<Printer> runningPrinters = getPrinters().stream().filter(p -> getCurrentTaskOfAPrinter(p) != null).toList();
        return runningPrinters;
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
            throw new IllegalArgumentException("Filament type does not exist");
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
    public PrintTask getCurrentTaskOfAPrinter(Printer printer) {
        return getPrintTaskManager().getPrinterCurrentTask(printer);
    }

    @Override
    public List<PrintTask> getPendingPrintTasks() {
        return getPrintTaskManager().getPendingPrintTasks();
    }

    public void removePendingPrintTask(PrintTask printTask) {
        getPrintTaskManager().removePendingPrintTask(printTask);
    }

//    public List<PrinterDTO> getPrinters() {
//        return getPrinterManager().getPrinters().stream().map(p -> p.asDTO()).collect(Collectors.toList());
//    }

    @Override
    public void readPrintersFromFile(String filename) {
        List<Printer> printers = jsonFileHandler.readPrinters();
        for (Printer printer : printers) {
            getPrinterManager().addPrinter(printer);
            getPrinterManager().addFreePrinter(printer);
        }
    }


    @Override
    public void readPrintsFromFile(String filename) {
        List<Print> prints = jsonFileHandler.readPrints();
        for (Print print : prints) {
            getPrintManager().addPrint(print);
        }
    }

//    @Override
//    public void readSpoolsFromFile(String filename) {
//        List<Spool> spools = fileHandler.readSpools();
//        for (Spool spool : spools) {
//            getSpoolManager().addSpool(spool);
//        }
//    }

    @Override
    public void readSpoolsFromFile(String filename) {
        List<Spool> spools = csvFileHandler.readSpools();
        for (Spool spool : spools) {
            getSpoolManager().addNewSpool(spool);
        }
    }

    public void initializeDashboardObserver() {
        getPrinterManager().initializeDashboard();
    }

    public void displayDashboardStats() {
        getPrinterManager().displayDashboardStats();
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
