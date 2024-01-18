package nl.saxion;

import nl.saxion.Models.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class OptimalSpoolUsageStrategy extends StrategyUtilities implements PrintingStrategy{
    @Override
    public void selectPrintTask(Printer printer) {
//        Spool[] spools = printer.getCurrentSpools();
        Spool[] spools = getSpoolManager().getFreeSpools().toArray(new Spool[0]);
        List<Spool> spoolList = Arrays.stream(spools).sorted(Comparator.comparingDouble(Spool::getLength)).toList();
//        spoolList.forEach(System.out::println);
        PrintTask chosenTask = null;

        int counter = 0;
        while (counter < spoolList.size()) {
            List<PrintTask> matchingPrintTasks = new ArrayList<>();
            for (PrintTask printTask : getPrintTaskManager().getPendingPrintTasks()) {
                if (printer.printFits(printTask.getPrint())){
                    if (isTaskMatchingStandardFDMPrinter(printer, printTask, spoolList.get(counter))){
                        matchingPrintTasks.add(printTask);
                    }
                    if (isTaskMatchingHousedPrinter(printer, printTask, spoolList.get(counter))){
                        matchingPrintTasks.add(printTask);
                    }
                    if (isTaskMatchingMultiColorPrinter(printer, printTask, spoolList.get(counter))) {
                        matchingPrintTasks.add(printTask);
                    }

                }
            }

            if (!matchingPrintTasks.isEmpty()){
                matchingPrintTasks = matchingPrintTasks.stream().sorted(Comparator.comparingInt(task -> task.getPrint().getPrintTime())).toList();
                chosenTask = matchingPrintTasks.get(0);
                getPrintTaskManager().addRunningPrintTask(printer,chosenTask);
                getPrintTaskManager().removePendingPrintTask(chosenTask);
                getPrinterManager().removeFreePrinter(printer);
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
                return true;
            }
        }
        return false;
    }

    private boolean isTaskMatchingHousedPrinter(Printer printer, PrintTask printTask, Spool spool) {
        if (printer instanceof StandardFDM && printTask.getFilamentType() == FilamentType.ABS && printTask.getColors().size() == 1) {
            if (spool.spoolMatch(printTask.getColors().get(0), printTask.getFilamentType())) {
                return true;
            }
        }
        return false;
    }

    private boolean isTaskMatchingMultiColorPrinter(Printer printer, PrintTask printTask, Spool spool) {
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
