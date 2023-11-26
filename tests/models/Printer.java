package models;

public class Printer implements ParsedString {
    private int id;
    private String name;
    private String model;
    private String manufacturer;
    private int maxX;
    private int maxY;
    private int maxZ;
    private int maxColors = -1;
    private int currentSpool = -1;
    private int spool2 = -1;
    private int spool3 = -1;
    private int spool4 = -1;
    private String currentPrint = "";

    public Printer(int id, String name, String model, String manufacturer, int maxX, int maxY, int maxZ, int maxColors){
        this.id = id;
        this.name = name;
        this.model = model;
        this.manufacturer = manufacturer;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
        this.maxColors = maxColors;
    }

    public void setCurrentPrint(String print) {
        this.currentPrint = print;
    }

    public int getCurrentSpool() {
        return currentSpool;
    }

    public boolean printing() {
        return currentPrint.length() > 0;
    }

    public String activePrintString() {
        return id + ": " + name + " - " + currentPrint;
    }

    public void setCurrentSpool(int spool) {
        currentSpool = spool;
    }
    public void setCurrentSpool(int spool, int spool2, int spool3, int spool4) {
        currentSpool = spool;
        this.spool2 = spool2;
        this.spool3 = spool3;
        this.spool4 = spool4;
    }

    public int getSpool(int nr) {
        if(nr == 1 ) {
            return currentSpool;
        } else if(nr == 2) {
            return spool2;
        } else if(nr == 3) {
            return spool3;
        } else if(nr == 4) {
            return spool4;
        }
        return -1;
    }

    public void setSpool(int spool, int position) {
        if(position == 1 ) {
            currentSpool = spool;
        } else if(position == 2) {
            spool2 = spool;
        } else if(position == 3) {
            spool3 = spool;
        } else if(position == 4) {
            spool4 = spool;
        }
    }

    public String getCurrentPrint() {
        return currentPrint;
    }

    public int getId() {
        return id;
    }

    public String parsedString() {
        String toPrint =
                "- ID: " + id +
                        " - Name: " + name +
                        " - Manufacturer: " + manufacturer +
                        " - maxX: " + maxX +
                        " - maxY: " + maxY +
                        " - maxZ: " + maxZ;


        if(currentSpool > -1) {
            toPrint += " - Spool(s): " + currentSpool;
        }
        if(spool2 > -1) {
            toPrint += ", " + spool2;
        }
        if(spool3 > -1) {
            toPrint += ", " + spool3;
        }
        if(spool3 > -1) {
            toPrint += ", " + spool4;
        }
        if(maxColors > 1) {
            toPrint += " - maxColors: " + maxColors;
        }
        if(currentPrint.length()> 0) {
            toPrint += " - Current Print Task: " + currentPrint ;
        }
        return toPrint;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        String toPrint =
                "- ID: " + id + System.lineSeparator() +
                " - Name: " + name + System.lineSeparator() +
                " - Manufacturer: " + manufacturer + System.lineSeparator() +
                " - maxX: " + maxX + System.lineSeparator() +
                " - maxY: " + maxY + System.lineSeparator() +
                " - maxZ: " + maxZ + System.lineSeparator();



        if(currentSpool > -1) {
            toPrint += " - Spool(s): " + currentSpool;
        }
        if(spool2 > -1) {
            toPrint += ", " + spool2;
        }
        if(spool3 > -1) {
            toPrint += ", " + spool3;
        }
        if(spool3 > -1) {
            toPrint += ", " + spool4;
        }
        if(currentSpool > -1) {
            toPrint += System.lineSeparator();
        }
        if(maxColors > 1) {
            toPrint += " - maxColors: " + maxColors + System.lineSeparator();
        }

        if(currentPrint.length()> 0) {
            toPrint += " - Current Print Task: " + currentPrint + System.lineSeparator();
        }
        toPrint += "" + System.lineSeparator();
        return toPrint;
    }
}
