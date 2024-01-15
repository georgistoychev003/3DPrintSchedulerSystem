package nl.saxion;


import nl.saxion.Models.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSONDomainReader implements DomainReader {

    private JSONParser jsonParser = new JSONParser();
    private String printersFilename;
    private String printsFilename;
    private String spoolsFilename;


    public JSONDomainReader() {
        // Setting the default file paths here
        this.printersFilename = "src/main/resources/printers.json";
        this.printsFilename = "src/main/resources/prints.json";
        this.spoolsFilename = "src/main/resources/spools.json";
    }


    @Override
    public List<Printer> readPrinters() {
        JSONArray printersJson = readJsonArrayFromFile(printersFilename);
        List<Printer> printers = new ArrayList<>();
        for (Object p : printersJson) {
            JSONObject printerJson = (JSONObject) p;
            try {
                printers.add(convertJsonToPrinter(printerJson));
            } catch (PrinterDataException e) {
                throw new RuntimeException(e);
            }
        }
        return printers;
    }

    @Override
    public List<Print> readPrints() {
        JSONArray printsJson = readJsonArrayFromFile(printsFilename);
        List<Print> prints = new ArrayList<>();
        for (Object p : printsJson) {
            JSONObject printJson = (JSONObject) p;
            prints.add(convertJsonToPrint(printJson));
        }
        return prints;
    }

    public List<Spool> readSpools() {
        JSONArray spoolsJson = readJsonArrayFromFile(spoolsFilename);
        List<Spool> spools = new ArrayList<>();
        for (Object s : spoolsJson) {
            JSONObject spoolJson = (JSONObject) s;
            try {
                spools.add(convertJsonToSpool(spoolJson));
            } catch (InvalidFilamentTypeException e) {
                System.out.println(e.getMessage());
            }
        }
        return spools;
    }

    private Printer convertJsonToPrinter(JSONObject printerJson) throws PrinterDataException {
        try {
            int id = ((Long) printerJson.get("id")).intValue();
            String name = (String) printerJson.get("name");
            String manufacturer = (String) printerJson.get("manufacturer");
            int maxX = ((Long) printerJson.get("maxX")).intValue();
            int maxY = ((Long) printerJson.get("maxY")).intValue();
            int maxZ = ((Long) printerJson.get("maxZ")).intValue();
            int maxColors = ((Long) printerJson.get("maxColors")).intValue();

            // Useing a ConcretePrinter instead of Printer as Printer is an abstract class
            if(maxColors == 1){
                return new StandardFDM(id, name, manufacturer, maxX, maxY, maxZ);
            }else{
                return new MultiColor(id, name, manufacturer, maxX, maxY, maxZ, maxColors);
            }

        } catch (NullPointerException | ClassCastException e) {
            throw new PrinterDataException("Invalid printer data format: " + e.getMessage());
        }
    }

    private Print convertJsonToPrint(JSONObject printJson) {
        String name = (String) printJson.get("name");
        int height = ((Long) printJson.get("height")).intValue();
        int width = ((Long) printJson.get("width")).intValue();
        int length = ((Long) printJson.get("length")).intValue();
        JSONArray fLength = (JSONArray) printJson.get("filamentLength");
        int printTime = ((Long) printJson.get("printTime")).intValue();
        ArrayList<Double> filamentLength = new ArrayList<>();
        for (Object lengthValue : fLength) {
            filamentLength.add(((Number) lengthValue).doubleValue());
        }
        return new Print(name, height, width, length, filamentLength, printTime);
    }
    private Spool convertJsonToSpool(JSONObject spoolJson) throws InvalidFilamentTypeException {
        int id = ((Long) spoolJson.get("id")).intValue();
        String color = (String) spoolJson.get("color");
        String filamentTypeStr = (String) spoolJson.get("filamentType");
        double length = ((Number) spoolJson.get("length")).doubleValue();

        FilamentType filamentType;
        try {
            filamentType = FilamentType.valueOf(filamentTypeStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidFilamentTypeException("Not a valid filamentType: " + filamentTypeStr);
        }

        return new Spool(id, color, filamentType, length);
    }


    private JSONArray readJsonArrayFromFile(String filename) {
        try (FileReader reader = new FileReader(filename)) {
            Object obj = jsonParser.parse(reader);
            if (obj instanceof JSONArray) {
                return (JSONArray) obj;
            } else {
                throw new ParseException(ParseException.ERROR_UNEXPECTED_TOKEN, "Expected JSONArray, found different structure");
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }


}


