package nl.saxion;

import nl.saxion.Models.*;

import java.util.List;

public interface IFacade {

    List<Printer> printCurrentlyRunningPrinters();
    void startPrintQueue();
    void registerSucceededPrinter(int printerId);
    void registerFailedPrinter(int printerId);
    void createPrintTask(String printName, List<String> colors, FilamentType type);
    Print findSelectedPrint(int printNumber);
    List<FilamentType> getFilamentTypes();
    FilamentType getSelectedFilamentType(int filamentTypeNumber, List<FilamentType> filamentTypes);
    List<Print> getPrints();
    List<Spool> getSpools();
    List<Printer> getPrinters();
    List<PrintTask> getPendingPrintTasks();
    List<String> getAvailableColors(FilamentType filamentType);
    PrintTask getCurrentTaskOfAPrinter(Printer printer);
    void readPrintsFromFile(String filename);
    void readPrintersFromFile(String filename);
    void readSpoolsFromFile(String filename);
}
