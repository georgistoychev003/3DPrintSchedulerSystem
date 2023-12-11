package nl.saxion;

import nl.saxion.Models.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.print.DocFlavor;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    Scanner scanner = new Scanner(System.in);
    private PrintingFacade printingFacade = new PrintingFacade();

    private String printStrategy = "Less Spool Changes";

    public static void main(String[] args) {
        new Main().run(args);
    }

    public void run(String[] args) {
        if(args.length > 0) {
            printingFacade.readPrintsFromFile(args[0]);
            printingFacade.readSpoolsFromFile(args[1]);
           printingFacade.readPrintersFromFile(args[2]);
        } else {
           printingFacade.readPrintsFromFile("");
            printingFacade.readSpoolsFromFile("");
            printingFacade.readPrintersFromFile("");
        }
        int choice = 1;
        while (choice > 0 && choice < 10) {
            menu();
            choice = menuChoice(9);
            System.out.println("-----------------------------------");
            if (choice == 1) {
                addNewPrintTask();
            } else if (choice == 2) {
                registerPrintCompletion();
            } else if (choice == 3) {
                registerPrinterFailure();
            } else if (choice == 4) {
                changePrintStrategy();
            } else if (choice == 5) {
               printingFacade.startPrintQueue();
            } else if (choice == 6) {
                printingFacade.showPrints();
            } else if (choice == 7) {
                printingFacade.showPrinters();
            } else if (choice == 8) {
                printingFacade.showSpools();
            } else if (choice == 9) {
                printingFacade.showPendingPrintTasks();
            }
        }
        exit();
    }

    public void menu() {
        System.out.println("------------- Menu ----------------");
        System.out.println("- 1) Add new Print Task");
        System.out.println("- 2) Register Printer Completion");
        System.out.println("- 3) Register Printer Failure");
        System.out.println("- 4) Change printing style");
        System.out.println("- 5) Start Print Queue");
        System.out.println("- 6) Show prints");
        System.out.println("- 7) Show printers");
        System.out.println("- 8) Show spools");
        System.out.println("- 9) Show pending print tasks");
        System.out.println("- 0) Exit");

    }

    private void exit() {

    }

    // This method only changes the name but does not actually work.
    // It exists to demonstrate the output.
    // in the future strategy might be added.
    private void changePrintStrategy() {
        System.out.println("---------- Change Strategy -------------");
        System.out.println("- Current strategy: " + printStrategy);
        System.out.println("- 1: Less Spool Changes");
        System.out.println("- 2: Efficient Spool Usage");
        System.out.println("- Choose strategy: ");
        int strategyChoice = numberInput(1, 2);
        if(strategyChoice == 1) {
            printStrategy = "- Less Spool Changes";
        } else if( strategyChoice == 2) {
            printStrategy = "- Efficient Spool Usage";
        }
        System.out.println("-----------------------------------");
    }

    // TODO: This should be based on which printer is finished printing.
    private void registerPrintCompletion() {
        //Print running printers
        int numberOfRunningPrinters = printingFacade.printCurrentlyRunningPrinters();

        System.out.print("- Printer that is done (ID): ");
        int printerId = numberInput(-1, numberOfRunningPrinters);

        printingFacade.registerSucceededPrinter(printerId);

        System.out.println("-----------------------------------");
    }

    private void registerPrinterFailure() {
        //Print running printers
        int numberOfRunningPrinters = printingFacade.printCurrentlyRunningPrinters();

        System.out.print("- Printer ID that failed: ");
        int printerId = numberInput(1, numberOfRunningPrinters);

        printingFacade.registerFailedPrinter(printerId);

        System.out.println("-----------------------------------");
    }

    private void addNewPrintTask() {
        //Selecting the print
        Print print = selectPrint();
        String printName = print.getName();

        //Select filament type
        FilamentType type = selectFilamentType();
        if (type == null){return;}

        //Select available colors
        List<String> colors = selectColors(type, print);

        //Create the print task
        printingFacade.createPrintTask(printName, colors, type);
        System.out.println("----------------------------");
    }

    private Print selectPrint() {
        List<Print> prints = printingFacade.showPrints();
        System.out.print("- Print number: ");
        int printNumber = numberInput(1, prints.size());
        System.out.println("--------------------------------------");
        return printingFacade.findSelectedPrint(printNumber);
    }

    private FilamentType selectFilamentType() {
        List<FilamentType> filamentTypes = printingFacade.showFilamentTypes();
        System.out.print("- Filament type number: ");
        int ftype = numberInput(1, filamentTypes.size());
        System.out.println("--------------------------------------");
        return printingFacade.getSelectedFilamentType(ftype, filamentTypes);
    }

    private List<String> selectColors(FilamentType type, Print print) {
        List<String> colors = new ArrayList<>();
        List<String> availableColors = printingFacade.getAvailableColors(type);
        System.out.print("- Color number: ");
        int colorChoice = numberInput(1, availableColors.size());
        colors.add(availableColors.get(colorChoice-1));

        for(int i = 1; i < print.getFilamentLength().size(); i++) {
            System.out.print("- Color number: ");
            colorChoice = numberInput(1, availableColors.size());
            colors.add(availableColors.get(colorChoice-1));
        }
        System.out.println("--------------------------------------");
        return colors;
    }


    //todo : move all reads to their own classes (single - responsibility principle)

    public int menuChoice(int max) {
        int choice = -1;
        while (choice < 0 || choice > max) {
            System.out.print("- Choose an option: ");
            try {
                choice = scanner.nextInt();
            } catch (InputMismatchException e) {
                //try again after consuming the current line
                System.out.println("- Error: Invalid input");
                scanner.nextLine();
            }
        }
        return choice;
    }

    public String stringInput() {
        String input = null;
        while(input == null || input.length() == 0){
            input = scanner.nextLine();
        }
        return input;
    }

    public int numberInput() {
        int input = scanner.nextInt();
        return input;
    }

    public int numberInput(int min, int max) {
        int input = numberInput();
        while (input < min || input > max) {
            input = numberInput();
        }
        return input;
    }
}
