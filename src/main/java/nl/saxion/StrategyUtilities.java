package nl.saxion;

import nl.saxion.Models.*;

import java.util.List;

public abstract class StrategyUtilities {

    public PrintTask matchFreeSpoolsWithPrintTask(Printer printer, PrintTask chosenTask) {
        for(PrintTask printTask: getPrintTaskManager().getPendingPrintTasks()) {
            if(printer.printFits(printTask.getPrint()) && getPrintTaskManager().getPrinterCurrentTask(printer) == null) {
                if (printer instanceof StandardFDM && printTask.getColors().size() == 1) {
                    chosenTask = getStandardFDMPrintTask(printer, chosenTask, printTask);

                } else if (printer instanceof MultiColor && printTask.getFilamentType() != FilamentType.ABS
                        && printTask.getColors().size() <= ((MultiColor) printer).getMaxColors()) {
                    List<Spool> chosenSpools = getSpoolManager().getNeededSpools(printTask);
                    // We assume that if they are the same length that there is a match.
                    if (chosenSpools.size() == printTask.getColors().size()) {
                        chosenTask = getMultiColorPrintTask(printer, printTask, chosenSpools);
                    }

                }
            }
        }
        return chosenTask;
    }

    public PrintTask matchCurrentSpoolWithPrintTask(Printer printer, Spool[] spools, PrintTask chosenTask) {
        for (PrintTask printTask : getPrintTaskManager().getPendingPrintTasks()) {
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
        getPrintTaskManager().addRunningPrintTask(printer, printTask);
        Spool[] currentSpools = printer.getCurrentSpools();
        for (int i = 0; i < currentSpools.length; i++) {
            getSpoolManager().addFreeSpool(currentSpools[i]);
        }
        printer.setCurrentSpools(chosenSpools);
        int position = 1;
        for (Spool spool : chosenSpools) {
            System.out.println("- Spool change: Please place spool " + spool.getId() + " in printer " + printer.getName() + " position " + position);
            getSpoolManager().removeFreeSpool(spool);
            position++;
        }
        getPrinterManager().removeFreePrinter(printer);
        chosenTask = printTask;
        return chosenTask;
    }

    private PrintTask getStandardFDMPrintTask(Printer printer, PrintTask chosenTask, PrintTask printTask) {
        Spool chosenSpool = null;
        for (Spool spool : getSpoolManager().getFreeSpools()) {
            if (spool.spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                chosenSpool = spool;
            }
        }
        if (chosenSpool != null) {
            getPrintTaskManager().addRunningPrintTask(printer,printTask);
            getSpoolManager().addFreeSpool(printer.getCurrentSpools()[0]);
            System.out.println("- Spool change: Please place spool " + chosenSpool.getId() + " in printer " + printer.getName());
            getSpoolManager().removeFreeSpool(chosenSpool);
            ((StandardFDM) printer).setCurrentSpool(chosenSpool);
            getPrinterManager().removeFreePrinter(printer);
            chosenTask = printTask;
        }
        return chosenTask;
    }

    private PrintTask matchStandardFDM(Printer printer, Spool[] spools, PrintTask printTask, PrintTask chosenTask) {
        if (printer instanceof StandardFDM && printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() == 1) {
            if (spools[0].spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                getPrintTaskManager().addRunningPrintTask(printer,printTask);
                getPrinterManager().removeFreePrinter(printer);
                chosenTask = printTask;
            }
            return chosenTask;
        }
        return null;
    }

    private PrintTask matchHousedPrinter(Printer printer, Spool[] spools, PrintTask printTask, PrintTask chosenTask) {
        if (printer instanceof StandardFDM && printTask.getFilamentType() == FilamentType.ABS && printTask.getColors().size() == 1) {
            if (spools[0].spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                getPrintTaskManager().addRunningPrintTask(printer,printTask);
                getPrinterManager().removeFreePrinter(printer);
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
                getPrintTaskManager().addRunningPrintTask(printer,printTask);
                getPrinterManager().removeFreePrinter(printer);
                chosenTask = printTask;
            }
            return chosenTask;
        }
        return null;
    }

    @Override
    public abstract String toString();

    public PrintTaskManager getPrintTaskManager() {
        return PrintTaskManager.getInstance();
    }
    public PrinterManager getPrinterManager() {
        return PrinterManager.getInstance();
    }
    public SpoolManager getSpoolManager() {
        return SpoolManager.getInstance();
    }
}
