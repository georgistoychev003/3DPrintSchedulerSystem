package nl.saxion;

import nl.saxion.Models.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVDomainReader implements DomainReader {

    private String spoolsFilename = "src/main/resources/spools.csv";

    @Override
    public List<Printer> readPrinters() {
        //we have no csv for the printers
        return new ArrayList<>();
    }

    @Override
    public List<Print> readPrints() {
        //we have no csv for the prints
        return new ArrayList<>();
    }



    public List<Spool> readSpools() {
        List<Spool> spools = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(spoolsFilename))) {
            // skip header
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                Spool spool = new Spool(
                        Integer.parseInt(values[0]), // id
                        values[1], // color
                        FilamentType.valueOf(values[2]), // filamentType
                        Double.parseDouble(values[3]) // length
                );
                spools.add(spool);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return spools;
    }
}
