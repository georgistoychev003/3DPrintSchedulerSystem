package nl.saxion;

import nl.saxion.Models.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class PrintingFacade implements IFacade{


    @Override
    public int printCurrentlyRunningPrinters() {
        List<Printer> printers = getPrinterManager().getPrinters();
        System.out.println("---------- Currently Running Printers ----------");
        for(Printer p: printers) {
            PrintTask printerCurrentTask= getPrintTaskManager().getPrinterCurrentTask(p);
            if(printerCurrentTask != null) {
                System.out.println("- " + p.getId() + ": " +p.getName() + " - " + printerCurrentTask);
            }
        }

        return printers.size();
    }

    @Override
    public void startPrintQueue() {
        getPrintTaskManager().startInitialQueue();
    }

    @Override
    public void registerSucceededPrinter(int printerId) {
        getPrintTaskManager().registerCompletion(printerId);
    }

    @Override
    public void registerFailedPrinter(int printerId) {
        getPrintTaskManager().registerPrinterFailure(printerId);
    }

    @Override
    public void createPrintTask(String printName, List<String> colors, FilamentType type) {
        getPrintTaskManager().addPrintTask(printName, colors, type);
    }

    @Override
    public List<String> getAvailableColors(FilamentType filamentType) {
        List<Spool> spools = getSpools();
        Set<String> availableColors = new HashSet<>();
        for (Spool spool : spools) {
            String colorString = spool.getColor();
            if(filamentType == spool.getFilamentType()) {
                availableColors.add(colorString);
            }
        }
        return availableColors.stream().toList();
    }

    @Override
    public Print findSelectedPrint(int printNumber) {
        return getPrintManager().findPrint(printNumber - 1);
    }

    @Override
    public List<FilamentType> getFilamentTypes() {
        return new ArrayList<>(EnumSet.allOf(FilamentType.class));
    }

    @Override
    public FilamentType getSelectedFilamentType(int filamentTypeNumber, List<FilamentType> filamentTypes) {
        if (filamentTypeNumber > filamentTypes.size() || filamentTypeNumber < 1){
            // TODO: throw exception
            System.out.println("Not a valid filament type.");
            return null;
        }
        return filamentTypes.get(filamentTypeNumber);
    }

    @Override
    public void printPrints() {

    }

    @Override
    public List<Print> getPrints() {
        return getPrintManager().getPrints();
    }

    @Override
    public List<Spool> getSpools() {
        return getSpoolManager().getSpools();
    }

    @Override
    public List<Printer> getPrinters() {
        return getPrinterManager().getPrinters();
    }

    @Override
    public PrintTask getCurrentTaskOfAPrinter(Printer p) {
        return getPrintTaskManager().getPrinterCurrentTask(p);
    }

    @Override
    public List<PrintTask> getPendingPrintTasks() {
        return getPrintTaskManager().getPendingPrintTasks();
    }

//    public List<PrinterDTO> getPrinters() {
//        return getPrinterManager().getPrinters().stream().map(p -> p.asDTO()).collect(Collectors.toList());
//    }

    @Override
    public void readPrintsFromFile(String filename) {
        JSONParser jsonParser = new JSONParser();
        if(filename.length() == 0) {
            filename = "prints.json";
        }
        URL printResource = getClass().getResource("/" + filename);
        if (printResource == null) {
            System.err.println("Warning: Could not find prints.json file");
            return;
        }
        try (FileReader reader = new FileReader(URLDecoder.decode(printResource.getPath(), StandardCharsets.UTF_8))) {
            JSONArray prints = (JSONArray) jsonParser.parse(reader);
            for (Object p : prints) {
                JSONObject print = (JSONObject) p;
                String name = (String) print.get("name");
                int height = ((Long) print.get("height")).intValue();
                int width = ((Long) print.get("width")).intValue();
                int length = ((Long) print.get("length")).intValue();
                //int filamentLength = ((Long) print.get("filamentLength")).intValue();
                JSONArray fLength = (JSONArray) print.get("filamentLength");
                int printTime = ((Long) print.get("printTime")).intValue();
                ArrayList<Double> filamentLength = new ArrayList();
                for(int i = 0; i < fLength.size(); i++) {
                    filamentLength.add(((Double) fLength.get(i)));
                }
                getPrintManager().addPrint(name, height, width, length, filamentLength, printTime);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void readPrintersFromFile(String filename) {
        JSONParser jsonParser = new JSONParser();
        if(filename.length() == 0) {
            filename = "printers.json";
        }
        URL printersResource = getClass().getResource("/" + filename);
        if (printersResource == null) {
            System.err.println("Warning: Could not find printers.json file");
            return;
        }
        try (FileReader reader = new FileReader(URLDecoder.decode(printersResource.getPath(), StandardCharsets.UTF_8))) {
            JSONArray printers = (JSONArray) jsonParser.parse(reader);
            for (Object p : printers) {
                JSONObject printer = (JSONObject) p;
                int id = ((Long) printer.get("id")).intValue();
                int type = ((Long) printer.get("type")).intValue();
                String name = (String) printer.get("name");
                String manufacturer = (String) printer.get("manufacturer");
                int maxX = ((Long) printer.get("maxX")).intValue();
                int maxY = ((Long) printer.get("maxY")).intValue();
                int maxZ = ((Long) printer.get("maxZ")).intValue();
                int maxColors = ((Long) printer.get("maxColors")).intValue();
                getPrinterManager().addPrinter(id, type, name, manufacturer, maxX, maxY, maxZ, maxColors);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void readSpoolsFromFile(String filename) {
        JSONParser jsonParser = new JSONParser();
        if(filename.length() == 0) {
            filename = "spools.json";
        }
        URL spoolsResource = getClass().getResource("/" + filename);
        if (spoolsResource == null) {
            System.err.println("Warning: Could not find spools.json file");
            return;
        }
        try (FileReader reader = new FileReader(URLDecoder.decode(spoolsResource.getPath(), StandardCharsets.UTF_8))) {
            JSONArray spools = (JSONArray) jsonParser.parse(reader);
            for (Object p : spools) {
                JSONObject spool = (JSONObject) p;
                int id = ((Long) spool.get("id")).intValue();
                String color = (String) spool.get("color");
                String filamentType = (String) spool.get("filamentType");
                double length = (Double) spool.get("length");
                FilamentType type;
                switch (filamentType) {
                    case "PLA":
                        type = FilamentType.PLA;
                        break;
                    case "PETG":
                        type = FilamentType.PETG;
                        break;
                    case "ABS":
                        type = FilamentType.ABS;
                        break;
                    default:
                        System.out.println("- Not a valid filamentType, bailing out");
                        return;
                }
                getSpoolManager().addSpool(new Spool(id, color, type, length));
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private PrinterManager getPrinterManager() {
        return PrinterManager.getInstance();
    }
    private PrintTaskManager getPrintTaskManager() {
        return PrintTaskManager.getInstance();
    }
    private PrintManager getPrintManager() {
        return PrintManager.getInstance();
    }

    private SpoolManager getSpoolManager() {
        return SpoolManager.getInstance();
    }

}
