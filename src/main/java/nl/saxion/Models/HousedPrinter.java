package nl.saxion.Models;

/* Printer capable of printing ABS */

//FIXME the code is simply an extention of standardFDM and it does not introduce any new logic
// which makes it seem redundant???
public class HousedPrinter extends StandardFDM {
    public HousedPrinter(int id, String printerName, String manufacturer, int maxX, int maxY, int maxZ) {
        super(id, printerName, manufacturer, maxX, maxY, maxZ);
    }
}
