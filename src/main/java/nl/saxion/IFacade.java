package nl.saxion;

import nl.saxion.Models.FilamentType;
import nl.saxion.Models.Print;
import nl.saxion.Models.Printer;

import java.util.List;

public interface IFacade {

    public int printCurrentlyRunningPrinters();
    void startPrintQueue();
    void registerSucceededPrinter(int printerId);
    void registerFailedPrinter(int printerId);
    void createPrintTask(String printName, List<String> colors, FilamentType type);
    List<String> getAvailableColors(FilamentType filamentType);
    Print findSelectedPrint(int printNumber);
    List<FilamentType> showFilamentTypes();
    FilamentType getSelectedFilamentType(int filamentTypeNumber, List<FilamentType> filamentTypes);
    void printPrints();
    List<Print> showPrints();
    void showSpools();
    void showPrinters();
    void showPendingPrintTasks();
    void readPrintsFromFile(String filename);
    void readPrintersFromFile(String filename);
    void readSpoolsFromFile(String filename);
}
