package nl.saxion;

import nl.saxion.Models.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrintTaskManager {

    private List<PrintTask> pendingPrintTasks = new ArrayList<>();
    private Map<Printer, PrintTask> runningPrintTasks = new HashMap();
    private PrinterManager printerManager = new PrinterManager();
    private SpoolManager spoolManager = new SpoolManager();
    private PrintManager printManager = new PrintManager();


    //FIXME: code smell??? // can be replaced with stream
    public void startInitialQueue() {
        for(Printer printer: printerManager.getPrinters()) {
            selectPrintTask(printer);
        }
    }

    // FIXME: code smell // method has too much functionality it should be reduced to smaller parts and also simplified / there is also a lot of code repetition going on
    public void selectPrintTask(Printer printer) {
        Spool[] spools = printer.getCurrentSpools();
        PrintTask chosenTask = null;
        // First we look if there's a task that matches the current spool on the printer.
        if(spools[0] != null) {
            //todo : move to its own method - refactor -> move to method - matchCurrentSpool
            chosenTask = matchCurrentSpoolWithPrintTask(printer, spools, chosenTask);
        }
        if(chosenTask != null) {
            pendingPrintTasks.remove(chosenTask);
            System.out.println("- Started task: " + chosenTask + " on printer " + printer.getName());
        } else {
            // If we didn't find a print for the current spool we search for a print with the free spools.
            //todo : move to its own method - refactor -> move to method - matchFreeSpools
            chosenTask = matchFreeSpoolsWithPrintTask(printer, chosenTask);
            if(chosenTask != null) {
                pendingPrintTasks.remove(chosenTask);
                System.out.println("- Started task: " + chosenTask + " on printer " + printer.getName());
            }
        }
    }

    private PrintTask matchFreeSpoolsWithPrintTask(Printer printer, PrintTask chosenTask) {
        for(PrintTask printTask: pendingPrintTasks) {
            if(printer.printFits(printTask.getPrint()) && getPrinterCurrentTask(printer) == null) {
                if (printer instanceof StandardFDM && printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() == 1) {
                    chosenTask = getStandardFDMPrintTask(printer, chosenTask, printTask);

                } else if (printer instanceof HousedPrinter && printTask.getColors().size() == 1) {
                    chosenTask = getStandardFDMPrintTask(printer, chosenTask, printTask);

                } else if (printer instanceof MultiColor && printTask.getFilamentType() != FilamentType.ABS
                        && printTask.getColors().size() <= ((MultiColor) printer).getMaxColors()) {
                    List<Spool> chosenSpools = spoolManager.getNeededSpools(printTask);
                    // We assume that if they are the same length that there is a match.
                    if (chosenSpools.size() == printTask.getColors().size()) {
                        chosenTask = getMultiColorPrintTask(printer, printTask, chosenSpools);
                    }

                }
            }
        }
        return chosenTask;
    }

    private PrintTask matchCurrentSpoolWithPrintTask(Printer printer, Spool[] spools, PrintTask chosenTask) {
        for (PrintTask printTask : pendingPrintTasks) {
            if (printer.printFits(printTask.getPrint())) {
                chosenTask = matchStandardFDM(printer, spools, printTask, chosenTask);
                if (chosenTask != null) {
                    break;
                }

                chosenTask = matchHousedPrinter(printer, spools, printTask, chosenTask);
                if (chosenTask != null) {
                    break;
                }

                chosenTask = matchMultiColor(printer, spools, printTask, chosenTask);
                if (chosenTask != null) {
                    break;
                }
            }
        }

        return chosenTask;
    }

    private PrintTask getMultiColorPrintTask(Printer printer, PrintTask printTask, List<Spool> chosenSpools) {
        PrintTask chosenTask;
        runningPrintTasks.put(printer, printTask);
        Spool[] currentSpools = printer.getCurrentSpools();
        for (int i = 0; i < currentSpools.length; i++) {
            spoolManager.addFreeSpool(currentSpools[i]);
        }
        printer.setCurrentSpools(chosenSpools);
        int position = 1;
        for (Spool spool : chosenSpools) {
            System.out.println("- Spool change: Please place spool " + spool.getId() + " in printer " + printer.getName() + " position " + position);
            spoolManager.removeFreeSpool(spool);
            position++;
        }
        printerManager.removeFreePrinter(printer);
        chosenTask = printTask;
        return chosenTask;
    }

    private PrintTask getStandardFDMPrintTask(Printer printer, PrintTask chosenTask, PrintTask printTask) {
        Spool chosenSpool = null;
        for (Spool spool : spoolManager.getFreeSpools()) {
            if (spool.spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                chosenSpool = spool;
            }
        }
        if (chosenSpool != null) {
            runningPrintTasks.put(printer, printTask);
            spoolManager.addFreeSpool(printer.getCurrentSpools()[0]);
            System.out.println("- Spool change: Please place spool " + chosenSpool.getId() + " in printer " + printer.getName());
            spoolManager.removeFreeSpool(chosenSpool);
            ((StandardFDM) printer).setCurrentSpool(chosenSpool);
            printerManager.removeFreePrinter(printer);
            chosenTask = printTask;
        }
        return chosenTask;
    }

    private PrintTask matchStandardFDM(Printer printer, Spool[] spools, PrintTask printTask, PrintTask chosenTask) {
        if (printer instanceof StandardFDM && printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() == 1) {
            if (spools[0].spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                runningPrintTasks.put(printer, printTask);
                printerManager.removeFreePrinter(printer);
                chosenTask = printTask;
            }
            return chosenTask;
        }
        return null;
    }

    private PrintTask matchHousedPrinter(Printer printer, Spool[] spools, PrintTask printTask, PrintTask chosenTask) {
        if (printer instanceof HousedPrinter && printTask.getColors().size() == 1) {
            if (spools[0].spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                runningPrintTasks.put(printer, printTask);
                printerManager.removeFreePrinter(printer);
                chosenTask = printTask;
            }
            return chosenTask;
        }
        return null;
    }

    private PrintTask matchMultiColor(Printer printer, Spool[] spools, PrintTask printTask, PrintTask chosenTask) {
        if (printer instanceof MultiColor && printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() <= ((MultiColor) printer).getMaxColors()) {
            boolean printWorks = true;
            for (int i = 0; i < spools.length && i < printTask.getColors().size(); i++) {
                if (!spools[i].spoolMatch(printTask.getColors().get(i), printTask.getFilamentType())) {
                    printWorks = false;
                    break;
                }
            }
            if (printWorks) {
                runningPrintTasks.put(printer, printTask);
                printerManager.removeFreePrinter(printer);
                chosenTask = printTask;
            }
            return chosenTask;
        }
        return null;
    }
    //TODO: check if get return null if not found which means a code smell the if statement can be avoided
    public PrintTask getPrinterCurrentTask(Printer printer) {
        if(!runningPrintTasks.containsKey(printer)) {
            return null;
        }
        return runningPrintTasks.get(printer);
    }

    public List<PrintTask> getPendingPrintTasks() {return pendingPrintTasks; }

    public void addPrintTask(String printName, List<String> colors, FilamentType type) {
        Print print = printManager.findPrint(printName);
        if (print == null) {
            printError("Could not find print with name " + printName);
            return;
        }
        //FIXME: code smell??? / Can be replaced with .isEmpty()
        if (colors.size() == 0) {
            printError("Need at least one color, but none given");
            return;
        }
        for (String color : colors) {
            boolean found = false;
            for (Spool spool : spoolManager.getSpools()) {
                if (spool.getColor().equals(color) && spool.getFilamentType() == type) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                printError("Color " + color + " (" + type +") not found");
                return;
            }
        }

        PrintTask task = new PrintTask(print, colors, type);
        pendingPrintTasks.add(task);
        System.out.println("Added task to queue");

    }


    //TODO: analyze this method as it looks too complicated
    public void registerPrinterFailure(int printerId) {
        Map.Entry<Printer, PrintTask> foundEntry = null;
        for (Map.Entry<Printer, PrintTask> entry : runningPrintTasks.entrySet()) {
            if (entry.getKey().getId() == printerId) {
                foundEntry = entry;
                break;
            }
        }
        if (foundEntry == null) {
            printError("cannot find a running task on printer with ID " + printerId);
            return;
        }
        PrintTask task = foundEntry.getValue();
        pendingPrintTasks.add(task); // add the task back to the queue.
        runningPrintTasks.remove(foundEntry.getKey());

        System.out.println("Task " + task + " removed from printer "
                + foundEntry.getKey().getName());

        Printer printer = foundEntry.getKey();
        Spool[] spools = printer.getCurrentSpools();
        for(int i=0; i<spools.length && i < task.getColors().size();i++) {
            spools[i].reduceLength(task.getPrint().getFilamentLength().get(i));
        }
        selectPrintTask(printer);
    }
    //FIXME: code smell // code repetition with the above method
    public void registerCompletion(int printerId) {
        Map.Entry<Printer, PrintTask> foundEntry = null;
        for (Map.Entry<Printer, PrintTask> entry : runningPrintTasks.entrySet()) {
            if (entry.getKey().getId() == printerId) {
                foundEntry = entry;
                break;
            }
        }
        if (foundEntry == null) {
            printError("cannot find a running task on printer with ID " + printerId);
            return;
        }
        PrintTask task = foundEntry.getValue();
        runningPrintTasks.remove(foundEntry.getKey());

        System.out.println("Task " + task + " removed from printer "
                + foundEntry.getKey().getName());

        Printer printer = foundEntry.getKey();
        Spool[] spools = printer.getCurrentSpools();
        for(int i=0; i<spools.length && i < task.getColors().size();i++) {
            spools[i].reduceLength(task.getPrint().getFilamentLength().get(i));
        }
        selectPrintTask(printer);


    }

    private void printError(String s) {
        System.out.println("---------- Error Message ----------");
        System.out.println("Error: "+s);
        System.out.println("--------------------------------------");
    }

    public Map<Printer, PrintTask> getRunningPrintTasks() {
        return runningPrintTasks;
    }

    public void removeRunningPrintTask(Printer printTask){
        runningPrintTasks.remove(printTask);
    }

    public PrinterManager getPrinterManager() {
        return printerManager;
    }

    public SpoolManager getSpoolManager() {
        return spoolManager;
    }

    public PrintManager getPrintManager() {
        return printManager;
    }
}
