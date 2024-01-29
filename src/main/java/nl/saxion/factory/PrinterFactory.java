package nl.saxion.factory;

import nl.saxion.Models.MultiColor;
import nl.saxion.Models.Printer;
import nl.saxion.Models.StandardFDM;

public class PrinterFactory {

    public static Printer createPrinterInstance(int id, int printerType, String printerName, String manufacturer, int maxX, int maxY, int maxZ, int maxColors) {
        if (printerType == 1 || printerType == 2) {
            StandardFDM printer = new StandardFDM(id, printerName, manufacturer, maxX, maxY, maxZ);
            if (printerType == 2) {
                printer.setHoused(true);
            }
            return printer;
        } else if (printerType == 3 || printerType == 4) {
            MultiColor printer = new MultiColor(id, printerName, manufacturer, maxX, maxY, maxZ, maxColors);
            if (printerType == 4) {
                printer.setHoused(true);
            }
            return printer;
        }

        return null;
    }
}
