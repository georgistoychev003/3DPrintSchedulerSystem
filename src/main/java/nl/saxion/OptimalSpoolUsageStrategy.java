package nl.saxion;

import nl.saxion.Models.*;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class OptimalSpoolUsageStrategy extends StrategyUtilities implements PrintingStrategy{
    @Override
    public void selectPrintTask(Printer printer) {
        Spool[] spools = printer.getCurrentSpools();
        System.out.println("Lng: " + spools.length);
        System.out.println("Spools array: " + Arrays.toString(spools));
        if (spools != null) {

            // Find the spool with the least filament left.
            Spool spool = Arrays.stream(spools)
                    .min(Comparator.comparingDouble(Spool::getLength))
                    .orElse(null);

            // Select a print task that matches the spool with the least filament left.
            PrintTask chosenTask = null;
            if (spool != null) {
                chosenTask = matchCurrentSpoolWithPrintTask(printer, spools, chosenTask);
            }

            // If no print task was found, select any print task that fits on the printer.
            if (chosenTask == null) {
                for (PrintTask printTask : getPrintTaskManager().getPendingPrintTasks()) {
                    if (printer.printFits(printTask.getPrint())) {
                        chosenTask = printTask;
                        break;
                    }
                }
            }

            // Start the print task.
            if (chosenTask != null) {
                getPrintTaskManager().addRunningPrintTask(printer, chosenTask);
            }
        }
    }


    @Override
    public String toString() {
        return "Optimal Spool Usage Strategy";
    }
}
