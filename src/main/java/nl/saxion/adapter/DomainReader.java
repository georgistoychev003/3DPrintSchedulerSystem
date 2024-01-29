package nl.saxion.adapter;


import nl.saxion.Models.Print;
import nl.saxion.Models.Printer;
import nl.saxion.Models.Spool;

import java.util.List;

public interface DomainReader {
    List<Printer> readPrinters(String filePath);
    List<Print> readPrints(String filePath);
    List<Spool> readSpools(String filePath);
    boolean supportsFileType(String filename);
}


