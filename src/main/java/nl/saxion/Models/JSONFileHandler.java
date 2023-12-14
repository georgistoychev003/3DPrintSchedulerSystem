package nl.saxion.Models;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.*;

public class JSONFileHandler {

    private JSONParser jsonParser = new JSONParser();

    public JSONArray readJSONArrayFromFile(String filename) throws IOException, ParseException {
        FileReader reader = new FileReader(filename);
        Object obj = jsonParser.parse(reader);
        if (obj instanceof JSONArray) {
            return (JSONArray) obj;
        } else {
            throw new ParseException(ParseException.ERROR_UNEXPECTED_TOKEN, "Expected JSONArray, found different structure");
        }
    }

    public JSONObject readJSONObjectFromFile(String filename) throws IOException, ParseException {
        FileReader reader = new FileReader(filename);
        Object obj = jsonParser.parse(reader);
        if (obj instanceof JSONObject) {
            return (JSONObject) obj;
        } else {
            throw new ParseException(ParseException.ERROR_UNEXPECTED_TOKEN, "Expected JSONObject, found different structure");
        }
    }

    public void writeJSONToFile(String filename, Object data) throws IOException {
        if (!(data instanceof JSONArray || data instanceof JSONObject)) {
            throw new IllegalArgumentException("Data must be an instance of JSONArray or JSONObject");
        }
        try (FileWriter writer = new FileWriter(filename)) {
            writer.write(data.toString());
        }
    }
}
