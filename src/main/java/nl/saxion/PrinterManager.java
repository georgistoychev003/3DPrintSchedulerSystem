package nl.saxion;

import nl.saxion.Models.*;
import org.json.simple.JSONArray;

import java.util.*;

public class PrinterManager {
    //FIXME: code smell // no ERROR HANDLING anywhere in the project !!!!!!!!!!!!!
    // TODO: create SpoolManager and PrintTaskManager
    // FIXME: code smell // class has too much functionality and the name states that is PrinterManager, however it handles prints and spools as well so different manager classes should be created for them
    // FIXME: code smell // all lists are directly given an ArrayList variable type, this reduces flexibility so it can be substituted with List<?> / same for the hashmap
    private List<Printer> printers = new ArrayList<Printer>(); //TODO use interface
    private List<Print> prints = new ArrayList<Print>(); //TODO use interface
    private List<Spool> spools = new ArrayList<Spool>(); //TODO use interface

    private List<Spool> freeSpools = new ArrayList<>(); // TODO: Decide if this should be used at all.
    private List<Printer> freePrinters = new ArrayList<>();
    private List<PrintTask> pendingPrintTasks = new ArrayList<>();
    private Map<Printer, PrintTask> runningPrintTasks = new HashMap();

    //FIXME: reduce addPrinter method args (create a class to pass or
    // FIXME: code smell / too many arguments / method can be reduced and simplified by substituing half of the args with a Printer object which can also make the method shorter

    public void addPrinter(int id, int printerType, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) {
        if (printerType == 1) {
            StandardFDM printer = new StandardFDM(id, printerName, manufacturer, maxX, maxY, maxZ);
            printers.add(printer);
            freePrinters.add(printer);
        } else if (printerType == 2) {
            HousedPrinter printer = new HousedPrinter(id, printerName, manufacturer, maxX, maxY, maxZ);
            printers.add(printer);
            freePrinters.add(printer);
        } else if (printerType == 3) {
            MultiColor printer = new MultiColor(id, printerName, manufacturer, maxX, maxY, maxZ, maxColors);
            printers.add(printer);
            freePrinters.add(printer);
        }
    }

    // FIXME: code smell??? /// naming can be more meaningful as we search the spool from the color and not a name variable that does not exist in the spool class, also list could be replaced with spools
    public boolean containsSpool(final List<Spool> list, final String name){
        return list.stream().anyMatch(o -> o.getColor().equals(name));
    }

    // FIXME: code smell // method has too much functionality it should be reduced to smaller parts and also simplified / there is also a lot of code repetition going on
    //TODO : analyze this method to find all problems/code smells
    public void selectPrintTask(Printer printer) {
        Spool[] spools = printer.getCurrentSpools();
        PrintTask chosenTask = null;
        // First we look if there's a task that matches the current spool on the printer.
        if(spools[0] != null) {
            //todo : move to its own method - refactor -> move to method - matchCurrentSpool
            for (PrintTask printTask : pendingPrintTasks) {
                if (printer.printFits(printTask.getPrint())) {
                    if (printer instanceof StandardFDM && printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() == 1) {
                        if (spools[0].spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                            runningPrintTasks.put(printer, printTask);
                            freePrinters.remove(printer);
                            chosenTask = printTask;
                            break;
                        }
                        // The housed printer is the only one that can print ABS, but it can also print the others.
                    } else if (printer instanceof HousedPrinter && printTask.getColors().size() == 1) {
                        if (spools[0].spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                            runningPrintTasks.put(printer, printTask);
                            freePrinters.remove(printer);
                            chosenTask = printTask;
                            break;
                        }
                        // For multicolor the order of spools does matter, so they have to match.
                    } else if (printer instanceof MultiColor && printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() <= ((MultiColor) printer).getMaxColors()) {
                        boolean printWorks = true;
                        for (int i = 0; i < spools.length && i < printTask.getColors().size(); i++) {
                            if (!spools[i].spoolMatch(printTask.getColors().get(i), printTask.getFilamentType())) {
                                printWorks = false;
                            }
                        }
                        if (printWorks) {
                            runningPrintTasks.put(printer, printTask);
                            freePrinters.remove(printer);
                            chosenTask = printTask;
                            break;
                        }
                    }
                }
            }
        }
        if(chosenTask != null) {
            pendingPrintTasks.remove(chosenTask);
            System.out.println("- Started task: " + chosenTask + " on printer " + printer.getName());
        } else {
            // If we didn't find a print for the current spool we search for a print with the free spools.
            //todo : move to its own method - refactor -> move to method - matchFreeSpools
            for(PrintTask printTask: pendingPrintTasks) {
                if(printer.printFits(printTask.getPrint()) && getPrinterCurrentTask(printer) == null) {
                    if (printer instanceof StandardFDM && printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() == 1) {
                        Spool chosenSpool = null;
                        for (Spool spool : freeSpools) {
                            if (spool.spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                                chosenSpool = spool;
                            }
                        }
                        if (chosenSpool != null) {
                            runningPrintTasks.put(printer, printTask);
                            freeSpools.add(printer.getCurrentSpools()[0]);
                            System.out.println("- Spool change: Please place spool " + chosenSpool.getId() + " in printer " + printer.getName());
                            freeSpools.remove(chosenSpool);
                            ((StandardFDM) printer).setCurrentSpool(chosenSpool);
                            freePrinters.remove(printer);
                            chosenTask = printTask;
                        }
                    } else if (printer instanceof HousedPrinter && printTask.getColors().size() == 1) {
                        Spool chosenSpool = null;
                        for (Spool spool : freeSpools) {
                            if (spool.spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                                chosenSpool = spool;
                            }
                        }
                        if (chosenSpool != null) {
                            runningPrintTasks.put(printer, printTask);
                            freeSpools.add(printer.getCurrentSpools()[0]);
                            System.out.println("- Spool change: Please place spool " + chosenSpool.getId() + " in printer " + printer.getName());
                            freeSpools.remove(chosenSpool);
                            ((StandardFDM) printer).setCurrentSpool(chosenSpool);
                            freePrinters.remove(printer);
                            chosenTask = printTask;
                        }
                    } else if (printer instanceof MultiColor && printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() <= ((MultiColor) printer).getMaxColors()) {
                        ArrayList<Spool> chosenSpools = new ArrayList<>();
                        for (int i = 0; i < printTask.getColors().size(); i++) {
                            for (Spool spool : freeSpools) {
                                if (spool.spoolMatch(printTask.getColors().get(i), printTask.getFilamentType()) && !containsSpool(chosenSpools, printTask.getColors().get(i))) {
                                    chosenSpools.add(spool);
                                }
                            }
                        }
                        // We assume that if they are the same length that there is a match.
                        if (chosenSpools.size() == printTask.getColors().size()) {
                            runningPrintTasks.put(printer, printTask);
                            for (Spool spool : printer.getCurrentSpools()) {
                                freeSpools.add(spool);
                            }
                            printer.setCurrentSpools(chosenSpools);
                            int position = 1;
                            for (Spool spool : chosenSpools) {
                                System.out.println("- Spool change: Please place spool " + spool.getId() + " in printer " + printer.getName() + " position " + position);
                                freeSpools.remove(spool);
                                position++;
                            }
                            freePrinters.remove(printer);
                            chosenTask = printTask;
                        }
                    }
                }
            }
            if(chosenTask != null) {
                pendingPrintTasks.remove(chosenTask);
                System.out.println("- Started task: " + chosenTask + " on printer " + printer.getName());
            }
        }
    }

    //FIXME: code smell??? // can be replaced with stream
    public void startInitialQueue() {
        for(Printer printer: printers) {
            selectPrintTask(printer);
        }
    }

    //FIXME: code smell // too many arguments / all can be replaced by a Print object
    public void addPrint(String name, int height, int width, int length, ArrayList<Double> filamentLength, int printTime) {
        Print p = new Print(name, height, width, length, filamentLength, printTime);
        prints.add(p);
    }

    public List<Print> getPrints() {
        return prints;
    }

    public List<Printer> getPrinters() {
        return printers;
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
        Print print = findPrint(printName);
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
            for (Spool spool : spools) {
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

    public void addSpool(Spool spool) {
        spools.add(spool);
        freeSpools.add(spool);
    }

    public List<Spool> getSpools() {
        return spools;
    }

    //FIXME: code smell // dead code
    public Spool getSpoolByID(int id) {
        for(Spool s: spools) {
            if(s.getId() == id) {
                return s;
            }
        }
        return null;
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

}
