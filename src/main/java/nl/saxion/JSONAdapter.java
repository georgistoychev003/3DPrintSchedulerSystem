package nl.saxion;

import nl.saxion.Models.JSONFileHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;

public class JSONAdapter implements FileHandler {

    private JSONFileHandler jsonFileHandler;

    public JSONAdapter() {
        this.jsonFileHandler = new JSONFileHandler();
    }

    @Override
    public Object readFile(String filename) {
        try {
            JSONParser jsonParser = new JSONParser();
            try (FileReader reader = new FileReader(filename)) {
                Object obj = jsonParser.parse(reader);
                if (obj instanceof JSONArray) {
                    return (JSONArray) obj;
                } else if (obj instanceof JSONObject) {
                    return (JSONObject) obj;
                } else {
                    throw new ParseException(ParseException.ERROR_UNEXPECTED_TOKEN, "Expected JSONArray or JSONObject");
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void writeFile(String filename, Object data) {
        try {
            jsonFileHandler.writeJSONToFile(filename, data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
