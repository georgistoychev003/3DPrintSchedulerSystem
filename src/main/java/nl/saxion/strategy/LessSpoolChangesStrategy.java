package nl.saxion.strategy;

import nl.saxion.Models.*;

public class LessSpoolChangesStrategy extends StrategyUtilities implements PrintingStrategy {

    public void selectPrintTask(Printer printer) {
            if (getPrinterManager().getFreePrinters().contains(printer)) {
            Spool[] spools = printer.getCurrentSpools();
            PrintTask chosenTask = null;
            // First we look if there's a task that matches the current spool/spools on the printer.
            if(spools[0] != null) {
                chosenTask = matchCurrentSpoolWithPrintTask(printer, spools);
            }
            if(chosenTask != null) {
                getPrintTaskManager().removePendingPrintTask(chosenTask);
                System.out.println("- Started task: " + chosenTask + " on printer " + printer.getName());
            } else {
                // If we didn't find a print for the current spool we search for a print with the free spools.
                chosenTask = matchFreeSpoolsWithPrintTask(printer);
                if(chosenTask != null) {
                    getPrintTaskManager().removePendingPrintTask(chosenTask);
                    System.out.println("- Started task: " + chosenTask + " on printer " + printer.getName());
                }
            }
        }
    }


    @Override
    public String toString() {
        return "Less Spool Changes Strategy";
    }

}
