package nl.saxion.managers;

import nl.saxion.Models.*;
import nl.saxion.strategy.LessSpoolChangesStrategy;
import nl.saxion.strategy.PrintingStrategy;

import java.util.*;

public class PrintTaskManager {
    private static PrintTaskManager instance;

    private List<PrintTask> pendingPrintTasks = new ArrayList<>();
    private Map<Printer, PrintTask> runningPrintTasks = new HashMap();
    private PrintingStrategy printingStrategy = new LessSpoolChangesStrategy();

    private PrintTaskManager() {
    }

    public void selectPrintTask(Printer printer) {
        printingStrategy.selectPrintTask(printer);
    }

    public void changePrintingStrategy(PrintingStrategy printingStrategy) {
        this.printingStrategy = printingStrategy;
    }

    public static PrintTaskManager getInstance() {
        if (instance == null) {
            instance = new PrintTaskManager();
        }
        return instance;
    }

    public void startInitialQueue() {
        for (Printer printer : getPrinterManager().getPrinters()) {
            selectPrintTask(printer);
        }
    }

    public PrintTask getPrinterCurrentTask(Printer printer) {
        if (!runningPrintTasks.containsKey(printer)) {
            return null;
        }
        return runningPrintTasks.get(printer);
    }

    public List<PrintTask> getPendingPrintTasks() {
        return pendingPrintTasks;
    }

    public void addPrintTask(String printName, List<String> colors, FilamentType type) {
        Print print = getPrintManager().findPrint(printName);
        if (print == null) {
            printError("Could not find print with name " + printName);
            return;
        }
        if (colors.isEmpty()) {
            printError("Need at least one color, but none given");
            return;
        }
        for (String color : colors) {
            boolean found = false;
            for (Spool spool : getSpoolManager().getSpools()) {
                if (spool.getColor().equals(color) && spool.getFilamentType() == type) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                printError("Color " + color + " (" + type + ") not found");
                return;
            }
        }

        PrintTask task = new PrintTask(print, colors, type);
        pendingPrintTasks.add(task);
        System.out.println("Added task to queue");

    }


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
        Printer printer = foundEntry.getKey();
        PrintTask task = foundEntry.getValue();
        pendingPrintTasks.add(task); // add the task back to the queue.
        runningPrintTasks.remove(foundEntry.getKey());

        // We free the printer if we are using the second strategy
        if (getPrintingStrategy().toString().equals("Optimal Spool Usage Strategy")) {
            getSpoolManager().addFreeSpool(printer.getCurrentSpools()[0]);
        }

        System.out.println("Task " + task + " removed from printer "
                + foundEntry.getKey().getName());

        getPrinterManager().addFreePrinter(printer);
        Spool[] spools = printer.getCurrentSpools();
        for (int i = 0; i < spools.length && i < task.getColors().size(); i++) {
            spools[i].reduceLength(task.getPrint().getFilamentLength().get(i));
        }
        selectPrintTask(printer);
    }

    //FIXME: code smell // code repetition with the above method
    //FIXME : code smell // name not descriptive
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

        Printer printer = foundEntry.getKey();
        PrintTask task = foundEntry.getValue();

        // Notify the printer that the print task is complete
        printer.onPrintComplete();

        // Removing the completed task
        runningPrintTasks.remove(printer);
        getPrinterManager().addFreePrinter(printer);

        // We free the printer if we are using the second strategy
        if (getPrintingStrategy().toString().equals("Optimal Spool Usage Strategy")) {
            getSpoolManager().addFreeSpool(printer.getCurrentSpools()[0]);
        }

        System.out.println("Task " + task + " removed from printer "
                + foundEntry.getKey().getName());


        Spool[] spools = printer.getCurrentSpools();
        for (int i = 0; i < spools.length && i < task.getColors().size(); i++) {
            if (spools[i] != null) {
                spools[i].reduceLength(task.getPrint().getFilamentLength().get(i));
            }
        }
        selectPrintTask(printer);
    }

    private void printError(String s) {
        System.out.println("---------- Error Message ----------");
        System.out.println("Error: " + s);
        System.out.println("--------------------------------------");
    }


    public void addRunningPrintTask(Printer printer, PrintTask printTask) {
        runningPrintTasks.put(printer, printTask);
    }

    public void removePendingPrintTask(PrintTask printTask) {
        pendingPrintTasks.remove(printTask);
    }

    public PrintingStrategy getPrintingStrategy() {
        return printingStrategy;
    }

    private PrinterManager getPrinterManager() {
        return PrinterManager.getInstance();
    }

    private PrintManager getPrintManager() {
        return PrintManager.getInstance();
    }

    private SpoolManager getSpoolManager() {
        return SpoolManager.getInstance();
    }

}
