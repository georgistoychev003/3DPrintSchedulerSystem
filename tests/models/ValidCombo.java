package models;

public class ValidCombo {
    private String print;
    private String printer;
    private String filament;

    public ValidCombo(String print, String printer, String filament) {
        this.print = print;
        this.printer = printer;
        this.filament = filament;
    }

    public String getFilament() {
        return filament;
    }

    public String getPrint() {
        return print;
    }

    public String getPrinter() {
        return printer;
    }
}
