package nl.saxion;


import nl.saxion.Models.Print;
import nl.saxion.Models.Printer;
import nl.saxion.Models.Spool;

import java.util.List;

public interface DomainReader {
    List<Printer> readPrinters();
    List<Print> readPrints();
    List<Spool> readSpools();
}


