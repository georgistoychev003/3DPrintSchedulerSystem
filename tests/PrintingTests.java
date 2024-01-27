import nl.saxion.LessSpoolChangesStrategy;
import nl.saxion.Models.*;
import nl.saxion.OptimalSpoolUsageStrategy;
import nl.saxion.PrintingFacade;
import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class PrintingTests {

    private PrintingFacade printingFacade;
    @BeforeEach
    public void createPrintingFacade() {
        printingFacade = new PrintingFacade();
        printingFacade.readPrintsFromFile("src/main/resources/prints.json");
        printingFacade.readSpoolsFromFile("src/main/resources/spools.csv");
        printingFacade.readPrintersFromFile("src/main/resources/printers.json");
    }

    public void selectFirstPrintingStrategy() {
        printingFacade.changePrintingStrategy(new LessSpoolChangesStrategy());
    }

    @Test
    public void gettingAllPrintsReturnsAListOfPrints() {
        List<Print> prints = printingFacade.getPrints();
        assertFalse(prints.isEmpty());
    }
    @Test
    public void gettingAllPrintersReturnsAListOfPrinters() {
        List<Printer> printers = printingFacade.getPrinters();
        assertFalse(printers.isEmpty());
    }
    @Test
    public void gettingAllSpoolsReturnsAListOfSpools() {
        List<Spool> spools = printingFacade.getSpools();
        assertFalse(spools.isEmpty());
    }

    @Test
    public void addingAPrintTaskProvidingCorrectParametersIsSuccessful() {
        Print print = printingFacade.getPrints().get(0);
        printingFacade.createPrintTask(print.getName(), List.of("Red"), FilamentType.PLA);
        List<PrintTask> printTasks =  printingFacade.getPendingPrintTasks();
        assertNotNull(printTasks.get(0));
        assertTrue(printTasks.get(0).toString().contains(print.getName()));

        printingFacade.removePendingPrintTask(printTasks.get(0));
    }

    @Test
    public void startingPrintProcessOfAPrintTaskIsSuccessfulAndCompletingIt() {
        Print print = printingFacade.getPrints().get(0);
        printingFacade.createPrintTask(print.getName(), List.of("Red"), FilamentType.PLA);
        printingFacade.startPrintQueue();
        PrintTask printTask = printingFacade.getCurrentTaskOfAPrinter(printingFacade.getPrinters().get(0));
        assertEquals(print.getName(), printTask.getPrint().getName());

        printingFacade.registerSucceededPrinter(printingFacade.getPrinters().get(0).getId());

        assertNull(printingFacade.getCurrentTaskOfAPrinter(printingFacade.getPrinters().get(0)));
    }

    @Test
    public void startingPrintProcessOfAPrintTaskAndRegisterPrinterFailure() {
        Print print = printingFacade.getPrints().get(0);
        printingFacade.createPrintTask(print.getName(), List.of("Red"), FilamentType.PLA);
        printingFacade.startPrintQueue();
        PrintTask printTask = printingFacade.getCurrentTaskOfAPrinter(printingFacade.getPrinters().get(0));
        assertEquals(print.getName(), printTask.getPrint().getName());

        printingFacade.registerFailedPrinter(printingFacade.getPrinters().get(0).getId());

        //Task tries to run again on the printer
        assertEquals(print.getName(), printTask.getPrint().getName());
    }

    @Test
    public void addingAPrintTaskUsingAPrintNameThatDoesNotExistDoesNotAddAPrintTask() {
        printingFacade.createPrintTask("I do not exist", List.of("Red"), FilamentType.PLA);
        System.out.println(printingFacade.getPendingPrintTasks());
        assertTrue(printingFacade.getPendingPrintTasks().isEmpty());
    }

    @Test
    public void addingAPrintTaskUsingEmptyListOfColorsDoesNotAddAPrintTask() {
        Print print = printingFacade.getPrints().get(0);
        printingFacade.createPrintTask(print.getName(), List.of(), FilamentType.PLA);
        System.out.println(printingFacade.getPendingPrintTasks());
        assertTrue(printingFacade.getPendingPrintTasks().isEmpty());
    }

    @Test
    public void changingPrintingStrategySuccessful() {
        printingFacade.changePrintingStrategy(new OptimalSpoolUsageStrategy());
        assertEquals(new OptimalSpoolUsageStrategy().toString() ,printingFacade.getPrintingStrategy().toString());

        selectFirstPrintingStrategy();
    }
    @Test
    public void addingPrintTaskWithInvalidFilamentTypeFails() {
        Print print = printingFacade.getPrints().get(0);
        printingFacade.createPrintTask(print.getName(), List.of("Red"), null); //null for filament type
        assertTrue(printingFacade.getPendingPrintTasks().isEmpty());
    }

    //Test for Concurrent Print Task Handling
    @Test
    public void handlingMultipleConcurrentPrintTasks() {
        Print print1 = printingFacade.getPrints().get(0);
        Print print2 = printingFacade.getPrints().get(1);

        printingFacade.createPrintTask(print1.getName(), List.of("Red"), FilamentType.PLA);
        printingFacade.createPrintTask(print2.getName(), List.of("Blue"), FilamentType.ABS);

        printingFacade.startPrintQueue();
        assertNotNull(printingFacade.getCurrentTaskOfAPrinter(printingFacade.getPrinters().get(0)));
        assertNotNull(printingFacade.getCurrentTaskOfAPrinter(printingFacade.getPrinters().get(1)));

        printingFacade.registerSucceededPrinter(1);
        printingFacade.registerSucceededPrinter(2);
    }

    @Test
    public void creatingAndStartingAPrintTaskWithOptimalSpoolUsageStrategy() {
        printingFacade.changePrintingStrategy(new OptimalSpoolUsageStrategy());

        Print print = printingFacade.getPrints().get(0);
        printingFacade.createPrintTask(print.getName(), List.of("Red"), FilamentType.PLA);
        printingFacade.startPrintQueue();
        PrintTask printTask = printingFacade.getCurrentTaskOfAPrinter(printingFacade.getPrinters().get(0));
        assertEquals(print.getName(), printTask.getPrint().getName());

        printingFacade.registerSucceededPrinter(printingFacade.getPrinters().get(0).getId());

        assertNull(printingFacade.getCurrentTaskOfAPrinter(printingFacade.getPrinters().get(0)));

        selectFirstPrintingStrategy();
    }

    @Test
    public void creatingAndStartingMultiplePrintTasksWithOptimalSpoolUsageStrategy() {
        printingFacade.changePrintingStrategy(new OptimalSpoolUsageStrategy());

        Print print1 = printingFacade.getPrints().get(0);
        Print print2 = printingFacade.getPrints().get(1);

        printingFacade.createPrintTask(print1.getName(), List.of("Red"), FilamentType.PLA);
        printingFacade.createPrintTask(print2.getName(), List.of("Blue"), FilamentType.PLA);

        printingFacade.startPrintQueue();
        assertNotNull(printingFacade.getCurrentTaskOfAPrinter(printingFacade.getPrinters().get(0)));
        assertNotNull(printingFacade.getCurrentTaskOfAPrinter(printingFacade.getPrinters().get(1)));

        printingFacade.registerSucceededPrinter(1);
        printingFacade.registerSucceededPrinter(2);

        assertNull(printingFacade.getCurrentTaskOfAPrinter(printingFacade.getPrinters().get(0)));
        assertNull(printingFacade.getCurrentTaskOfAPrinter(printingFacade.getPrinters().get(1)));

        selectFirstPrintingStrategy();
    }


}
