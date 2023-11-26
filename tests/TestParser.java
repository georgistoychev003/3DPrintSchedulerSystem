import models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Read this parser at your own peril.
 *
 * This is not a correct way to test code or programs. It exists to make life easier and allow a form of black box testing.
 */
public class TestParser {
    private ArrayList<String> errorMessages = new ArrayList<>();
    private ArrayList<Printer> printers = new ArrayList<>();
    private ArrayList<Print> prints = new ArrayList<>();
    private ArrayList<Spool> spools = new ArrayList<>();
    private ArrayList<PrintTask> printTasks = new ArrayList<>();
    private ArrayList<PrintTask> expectedPrintTasks = new ArrayList<>();
    private ArrayList<String> printList = new ArrayList<>();
    private ArrayList<ValidCombo> validCombos = new ArrayList<>();

    public TestParser() {
        addPrinters();
        addPrints();
        addSpools();
        addValidCombos();
    }

    public void parse(String outputString) {
        outputString = outputString.replaceAll("\\r\\n|\\r|\\n", " ");
        String[] outputList = outputString.split(">> <<");


        for (int i = 0; i < outputList.length; i++) {
            if (outputList[i].contains("Menu")) {
                checkMenu(outputList[i]);
            } else if (outputList[i].contains("New Print Task")) {
                // errorMessages.add("New Print task checked");
            } else if (outputList[i].contains("Pending Print Tasks")) {
                checkItems("Pending Print Tasks", outputList[i], (ArrayList<ParsedString>) expectedPrintTasks.clone());
            } else if (outputList[i].contains("Available prints")) {
                checkItems("Prints", outputList[i], (ArrayList<ParsedString>) prints.clone());
            } else if (outputList[i].contains("Available printers")) {
                checkItems("Printers", outputList[i], (ArrayList<ParsedString>) printers.clone());
            } else if (outputList[i].contains("Available spools")) {
                checkItems("Spools", outputList[i], (ArrayList<ParsedString>) spools.clone());
            } else if (outputList[i].contains("Starting Print Queue")) {
                checkPrintTasks(outputList[i]);
            }
        }
    }

    private void checkMenu(String menu) {
        String menuString = "------------- Menu ----------------" +
                " - 1) Add new Print Task" +
                " - 2) Register Printer Completion" +
                " - 3) Register Printer Failure" +
                " - 4) Change printing style" +
                " - 5) Start Print Queue" +
                " - 6) Show prints" +
                " - 7) Show printers" +
                " - 8) Show spools" +
                " - 9) Show pending print tasks" +
                " - 0) Exit" +
                " - Choose an option: -----------------------------------";
        menuString = menuString.replace(" ", "");
        String compactedString = menu.replace(" ", "").replace("<<", "").replace(">>", "");
        if (!menuString.equals(compactedString)) {
            errorMessages.add("Menu does not match.");
        }
    }

    private void checkItems(String itemName, String itemString, ArrayList<ParsedString> itemsRemaining) {
        String[] itemList = itemString.split("> <");

        for (int i = 1; i < itemList.length - 1; i++) {
            String itemInfo = itemList[i].replace("--------", "").trim();
            int foundIndex = -1;
            for (ParsedString p : itemsRemaining) {
                if (itemInfo.equals(p.parsedString())) {
                    foundIndex = itemsRemaining.indexOf(p);
                }
            }
            if (foundIndex > -1) {
                indexNumbers(itemName, itemsRemaining.get(foundIndex).getName());
                itemsRemaining.remove(foundIndex);
            } else {
                errorMessages.add("Listed " + itemName + " should not exist or is incorrect: " + itemInfo);
            }
        }
        if (itemsRemaining.size() > 0) {
            String missingItems = "";
            for (ParsedString p : itemsRemaining) {
                missingItems += p.parsedString() + System.lineSeparator();
            }
            errorMessages.add("There are missing " + itemName + ": " + System.lineSeparator() + missingItems);
        }
    }

    public void checkPrintTasks(String itemString) {
        Pattern patternSpool = Pattern.compile("spool \\d+ ");
        Pattern patternPrinter = Pattern.compile("printer \\w+");
        Pattern patternPosition = Pattern.compile("position \\w+");
        Pattern patternTask = Pattern.compile(" < \\w.+ > ");
        String[] itemList = itemString.split("- ");

        for (int i = 2; i < itemList.length; i++) {

            Matcher matcher = patternPrinter.matcher(itemList[i]);
            if(matcher.find()) {
                String printer = matcher.group(0).split(" ")[1];
                if (itemList[i].contains("Spool change: ")) {
                    matcher = patternSpool.matcher(itemList[i]);
                    matcher.find();
                    int spool = Integer.parseInt(matcher.group(0).split(" ")[1]);
                    matcher = patternPosition.matcher(itemList[i]);
                    int position = 1;
                    if(matcher.find()) {
                        position = Integer.parseInt(matcher.group(0).split(" ")[1]);
                    }
                    assignSpoolToPrinter(spool, printer, position);
                } else if (itemList[i].contains("Started task: ")) {
                    matcher = patternTask.matcher(itemList[i]);
                    matcher.find();
                    String task = matcher.group(0);
                    assignPrintToPrinter(task, printer);
                }
            }


        }
    }

    public void assignSpoolToPrinter(int spool, String printer, int position) {
        Printer chosenPrinter = findPrinterByName(printer);
        if (chosenPrinter != null && !(isSpoolFree(spool) || chosenPrinter.getCurrentSpool() == spool)) {
            chosenPrinter.setSpool(spool, position);
        }
    }

    public void assignPrintToPrinter(String task, String printer) {
        Printer chosenPrinter = findPrinterByName(printer);

        Pattern colorPattern = Pattern.compile("\\[\\w.+\\]");
        Matcher matcher = colorPattern.matcher(task);
        matcher.find();
        String[] colors = matcher.group(0).replaceAll("\\[|\\]", "").split(", ");

        Pattern filamentPattern = Pattern.compile("PLA|PETG|ABS");
        matcher = filamentPattern.matcher(task);
        matcher.find();
        String filament = matcher.group(0);

        String print = task.replaceAll(" (PLA|PETG|ABS) \\[\\w.+\\]", "");
        print = print.replaceAll(" < | > ", "");

        if(checkPrinterTaskCompability(print, printer, filament)) {
            int errorsize = errorMessages.size();
            for (int i = 1; i <= colors.length; i++) {
                if (chosenPrinter.getSpool(i) == -1) {
                    errorMessages.add("Printer has no spool in position: " + i);
                } else {
                    Spool printerSpool = findSpoolByNr(chosenPrinter.getSpool(i));
                    if (!printerSpool.getColor().equals(colors[i-1])) {
                        errorMessages.add("Current spool " + printerSpool.getColor() + " is not the correct color " + colors[i-1]);
                    }
                    if (!printerSpool.getFilamentType().equals(filament)) {
                        errorMessages.add("Current spool " + printerSpool.getFilamentType() + " on printer is not " + filament);
                    }
                }
            }
            if(chosenPrinter.getCurrentPrint().length() > 0) {
                errorMessages.add("Printer already has a task assigned: " + chosenPrinter.getCurrentPrint());
            }

            if(errorsize == errorMessages.size()) {
                System.err.println("Moo!");
                chosenPrinter.setCurrentPrint(task);
                System.err.println(chosenPrinter.getCurrentPrint());
                expectedPrintTasks.remove(getPrintTask(findPrintByName(print), filament, colors));
            }
        } else {
            errorMessages.add("Print " + print + " does not fit on printer " + printer);
        }
    }

    public boolean checkPrinterTaskCompability(String print, String printer, String filament) {
        for(ValidCombo vc: validCombos) {
            if(vc.getPrint().equals(print) && vc.getPrinter().equals(printer) && vc.getFilament().equals(filament)) {
                return true;
            }
        }
        return false;
    }

    public PrintTask getPrintTask(Print print, String filament, String[] colors) {
        for(PrintTask pt: expectedPrintTasks) {
            if(pt.matches(print, filament, colors)) {
                return pt;
            }
        }
        return null;
    }

    public Printer findPrinterByName(String printer) {
        Printer foundPrinter = null;
        for (Printer p : printers) {
            if (p.getName().equals(printer)) {
                foundPrinter = p;
                break;
            }
        }
        if (foundPrinter == null) {
            errorMessages.add("Printer " + printer + " not found.");
        }
        return foundPrinter;
    }

    public Print findPrintByName(String print) {
        Print foundPrint = null;
        for (Print p : prints) {
            if (p.getName().equals(print)) {
                foundPrint = p;
                break;
            }
        }
        if (foundPrint == null) {
            errorMessages.add("Printer " + print + " not found.");
        }
        return foundPrint;
    }

    public Spool findSpoolByNr(int spool) {
        Spool chosenSpool = null;
        for (Spool s : spools) {
            if (s.getId() == spool) {
                chosenSpool = s;
            }
        }
        if (chosenSpool == null) {
            errorMessages.add("Spool " + spool + " not found.");
        }
        return chosenSpool;
    }

    public boolean isSpoolFree(int spool) {
        if (findSpoolByNr(spool) != null) {
            for (Printer p : printers) {
                if (p.getCurrentSpool() == spool) {
                    return true;
                }
            }
        }
        return false;
    }

    public void indexNumbers(String itemName, String listName) {
        if (itemName.equals("Prints")) {
            printList.add(listName);
        }
    }

    public int getPrintNr(String printName) {
        for (String s : printList) {
            if (s.equals(printName)) {
                return printList.indexOf(printName) + 1;
            }
        }
        return -1;
    }

    public int getColorNr(String color, String filament) {
        List<String> colors = new ArrayList<>();
        for(Spool s: spools) {
            if(s.getFilamentType().equals(filament) && !colors.contains(s.getColor())){
                colors.add(s.getColor());
            }
        }
        return colors.indexOf(color) + 1;
    }

    public void addExpectedPrintTask(String printname, String colors, String filamentType) {
        Print print;
        for (Print p : prints) {
            if (p.getName().equals(printname)) {
                print = p;
                expectedPrintTasks.add(new PrintTask(print, filamentType, colors.split(",")));
                break;
            }
        }
    }

    public int getPrinterIDWithTask(String task) {
        Printer foundPrinter = null;
        for (Printer p : printers) {
            System.err.println(p.getCurrentPrint());
            if (p.getCurrentPrint().equals(task)) {
                foundPrinter = p;
                break;
            }
        }
        if (foundPrinter == null) {
            errorMessages.add("Task " + task + " not assigned to any printer.");
        }
        return foundPrinter != null ? foundPrinter.getId() : -1;
    }

    public boolean noErrors() {
        if (errorMessages.size() == 0) {
            return true;
        }
        for (String message : errorMessages) {
            System.err.println(message);
        }
        return false;
    }


    private void addPrinters() {
        printers.add(new Printer(1, "Enterprise", "Ender 3", "Creality", 220, 220, 250, 1));
        printers.add(new Printer(2, "Serenity", "Ender 3v2", "Creality", 220, 220, 250, 1));
        printers.add(new Printer(3, "Red Dwarf", "Ender 5 Plus", "Creality", 350, 350, 350, 1));
        printers.add(new Printer(4, "Heart of Gold", "i3 MKS3S+", "Prusa", 210, 210, 210, 4));
        printers.add(new Printer(5, "Tardis", "Mini+", "Prusa", 180, 180, 180, 1));
        printers.add(new Printer(6, "Rocinante", "Ultimaker 2+", "Ultimaker", 223, 220, 205, 1));
        printers.add(new Printer(7, "Bebop", "Voxel", "Monoprice", 150, 150, 150, 1));
    }

    private void addPrints() {
        prints.add(new Print("Acoustic Guitar Cooky Cutter", 43, 104, 13, 31, 1.25));
        prints.add(new Print("Stegosaurus Pickholder", 79, 155, 43, 315, 14.68));
        prints.add(new Print("Collapsing Jian", 32, 111, 199, 1308, 50.45));
        prints.add(new Print("Earth Globe", 57, 57, 57, 770, 9.05, 1.41, 13.95, 0.31));
        prints.add(new Print("Moon Lamp", 258, 258, 258, 6841, 147.45));
        prints.add(new Print("Cathedral", 47, 60, 63, 237, 7.19, 0.97, 0.18, 0.02));
        prints.add(new Print("Fucktopus", 56, 59, 32, 101, 2.86));
        prints.add(new Print("Lizard", 145, 81, 23, 180, 3.1, 3.73, 0.15));
        prints.add(new Print("Tree Frog", 36, 50, 23, 62, 1.86, 0.73));
    }

    private void addSpools() {
        spools.add(new Spool(1, "Blue", "PLA", 90.0));
        spools.add(new Spool(2, "Red", "PLA", 42.54));
        spools.add(new Spool(3, "Green", "PLA", 116.84));
        spools.add(new Spool(4, "Blue", "PETG", 135.64));
        spools.add(new Spool(5, "Red", "PETG", 95.15));
        spools.add(new Spool(6, "Green", "PETG", 119.60));
        spools.add(new Spool(7, "Pink", "PETG", 150.72));
        spools.add(new Spool(8, "Blue", "ABS", 30.56));
        spools.add(new Spool(9, "Red", "ABS", 85.46));
        spools.add(new Spool(10, "Green", "ABS", 48.13));
        spools.add(new Spool(11, "Blue", "PLA", 132.96));
        spools.add(new Spool(12, "Red", "PLA", 130.84));
        spools.add(new Spool(13, "Green", "PLA", 145.30));
        spools.add(new Spool(14, "Blue", "PETG", 138.60));
        spools.add(new Spool(15, "Red", "PETG", 79.74));
        spools.add(new Spool(16, "Green", "PETG", 103.70));
        spools.add(new Spool(17, "Pink", "PETG", 56.11));
        spools.add(new Spool(18, "Blue", "ABS", 84.11));
        spools.add(new Spool(19, "Red", "ABS", 23.52));
        spools.add(new Spool(20, "Green", "ABS", 62.87));
        spools.add(new Spool(21, "Pink", "PLA", 150.72));
    }

    private void addValidCombos() {
        String print = "Acoustic Guitar Cooky Cutter";
        String[] printers = new String[]{"Enterprise", "Serenity", "Red Dwarf", "Heart of Gold", "Tardis", "Rocinante", "Bebop"};
        for (int i = 0; i < printers.length; i++) {
            validCombos.add(new ValidCombo(print, printers[i], "PLA"));
            validCombos.add(new ValidCombo(print, printers[i], "PETG"));
        }
        print = "Stegosaurus Pickholder";
        printers = new String[]{"Enterprise", "Serenity", "Red Dwarf", "Heart of Gold", "Tardis", "Rocinante"};
        for (int i = 0; i < printers.length; i++) {
            validCombos.add(new ValidCombo(print, printers[i], "PLA"));
            validCombos.add(new ValidCombo(print, printers[i], "PETG"));
        }
        print = "Collapsing Jian";
        printers = new String[]{"Enterprise", "Serenity", "Red Dwarf", "Heart of Gold", "Rocinante"};
        for (int i = 0; i < printers.length; i++) {
            validCombos.add(new ValidCombo(print, printers[i], "PLA"));
            validCombos.add(new ValidCombo(print, printers[i], "PETG"));
        }
        print = "Earth Globe";
        printers = new String[]{"Heart of Gold"};
        for (int i = 0; i < printers.length; i++) {
            validCombos.add(new ValidCombo(print, printers[i], "PLA"));
            validCombos.add(new ValidCombo(print, printers[i], "PETG"));
        }
        print = "Moon Lamp";
        printers = new String[]{"Red Dwarf"};
        for (int i = 0; i < printers.length; i++) {
            validCombos.add(new ValidCombo(print, printers[i], "PLA"));
            validCombos.add(new ValidCombo(print, printers[i], "PETG"));
        }
        print = "Cathedral";
        printers = new String[]{"Heart of Gold"};
        for (int i = 0; i < printers.length; i++) {
            validCombos.add(new ValidCombo(print, printers[i], "PLA"));
            validCombos.add(new ValidCombo(print, printers[i], "PETG"));
        }
        print = "Fucktopus";
        printers = new String[]{"Enterprise", "Serenity", "Red Dwarf", "Heart of Gold", "Tardis", "Rocinante", "Bebop"};
        for (int i = 0; i < printers.length; i++) {
            validCombos.add(new ValidCombo(print, printers[i], "PLA"));
            validCombos.add(new ValidCombo(print, printers[i], "PETG"));
        }
        print = "Lizard";
        printers = new String[]{"Heart of Gold"};
        for (int i = 0; i < printers.length; i++) {
            validCombos.add(new ValidCombo(print, printers[i], "PLA"));
            validCombos.add(new ValidCombo(print, printers[i], "PETG"));
        }
        print = "Tree Frog";
        printers = new String[]{"Heart of Gold"};
        for (int i = 0; i < printers.length; i++) {
            validCombos.add(new ValidCombo(print, printers[i], "PLA"));
            validCombos.add(new ValidCombo(print, printers[i], "PETG"));
        }
        print = "Acoustic Guitar Cooky Cutter";
        printers = new String[]{"Serenity", "Rocinante", "Bebop"};
        for (int i = 0; i < printers.length; i++) {
            validCombos.add(new ValidCombo(print, printers[i], "ABS"));
        }
        print = "Stegosaurus Pickholder";
        printers = new String[]{"Serenity", "Rocinante"};
        for (int i = 0; i < printers.length; i++) {
            validCombos.add(new ValidCombo(print, printers[i], "ABS"));
        }
        print = "Collapsing Jian";
        printers = new String[]{"Serenity", "Rocinante"};
        for (int i = 0; i < printers.length; i++) {
            validCombos.add(new ValidCombo(print, printers[i], "ABS"));
        }
        print = "Fucktopus";
        printers = new String[]{"Serenity", "Rocinante", "Bebop"};
        for (int i = 0; i < printers.length; i++) {
            validCombos.add(new ValidCombo(print, printers[i], "ABS"));
        }
    }
}