package nl.saxion;

import nl.saxion.Models.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class OptimalSpoolUsageStrategy extends StrategyUtilities implements PrintingStrategy{
    @Override
    public void selectPrintTask(Printer printer) {

        // Since the strategy is regarding optimal spool usage, and no more details are given it is considered that
        // we won't be checking for the spools that are currently on the printer as that won't lead to the most optimal
        // spool usage. A printer is emptied of all spools after completion.

        Spool[] spools = getSpoolManager().getFreeSpools().toArray(new Spool[0]);
        List<Spool> spoolList = Arrays.stream(spools)
                .sorted(Comparator.comparingDouble(spool -> spool == null ? Double.MAX_VALUE : spool.getLength()))
                .toList();
        PrintTask chosenTask = null;

        int counter = 0;
        while (counter < spoolList.size()) {
            List<PrintTask> matchingPrintTasks = new ArrayList<>();
            for (PrintTask printTask : getPrintTaskManager().getPendingPrintTasks()) {
                if (printer.printFits(printTask.getPrint()) && getPrinterManager().getFreePrinters().contains(printer)){
                    if (spoolList.get(counter) != null) {
                        if (isTaskMatchingStandardFDMPrinter(printer, printTask, spoolList.get(counter))) {
                            matchingPrintTasks.add(printTask);
                        }
                        if (isTaskMatchingHousedPrinter(printer, printTask, spoolList.get(counter))) {
                            matchingPrintTasks.add(printTask);
                        }
                        if (isTaskMatchingMultiColorPrinter(printer, printTask, spoolList.get(counter))) {
                            matchingPrintTasks.add(printTask);
                        }
                    }
                }
            }

            if (!matchingPrintTasks.isEmpty()){
                matchingPrintTasks = matchingPrintTasks.stream().sorted(Comparator.comparingInt(task -> task.getPrint().getPrintTime())).toList();
                chosenTask = matchingPrintTasks.get(0);

                getPrintTaskManager().addRunningPrintTask(printer,chosenTask);
                getPrintTaskManager().removePendingPrintTask(chosenTask);

                getPrinterManager().removeFreePrinter(printer);

                getSpoolManager().removeFreeSpool(spoolList.get(counter));
                System.out.println("- Spool change: Please place spool " + spoolList.get(counter).getId() + " in printer " + printer.getName());
                if (printer instanceof StandardFDM) {
                    ((StandardFDM) printer).setCurrentSpool(spoolList.get(counter));
                }

                System.out.println("- Started task: " + chosenTask + " on printer " + printer.getName());
            }

            counter++;
        }
    }

    private boolean isTaskMatchingStandardFDMPrinter(Printer printer, PrintTask printTask, Spool spool) {
        if (printer instanceof StandardFDM && printTask.getFilamentType() != FilamentType.ABS && printTask.getColors().size() == 1) {
            if (spool.spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                getSpoolManager().addFreeSpool(printer.getCurrentSpools()[0]);
                return true;
            }
        }
        return false;
    }

    private boolean isTaskMatchingHousedPrinter(Printer printer, PrintTask printTask, Spool spool) {
        if (printer instanceof StandardFDM && printTask.getFilamentType() == FilamentType.ABS && printTask.getColors().size() == 1) {
            if (spool.spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                getSpoolManager().addFreeSpool(printer.getCurrentSpools()[0]);
                return true;
            }
        }
        return false;
    }

    private boolean isTaskMatchingMultiColorPrinter(Printer printer, PrintTask printTask, Spool spool) {
        //FIXME: multicolor functionality not implemented / code bellow duplicated with isTaskMatchingHousedPrinter method
        if (printer instanceof StandardFDM && printTask.getFilamentType() == FilamentType.ABS && printTask.getColors().size() == 1) {
            if (spool.spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        return "Optimal Spool Usage Strategy";
    }
}
