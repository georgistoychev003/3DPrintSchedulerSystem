package nl.saxion;

import nl.saxion.Models.HousedMultiColorPrinter;
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
        } else if (printerType == 3) {
            return new MultiColor(id, printerName, manufacturer, maxX, maxY, maxZ, maxColors);
        } else if (printerType == 4) {
            return new HousedMultiColorPrinter(id, printerName, manufacturer, maxX, maxY, maxZ, maxColors);
        }

        return null;
    }
}
