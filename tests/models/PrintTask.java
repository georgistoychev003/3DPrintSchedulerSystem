package models;

import java.util.ArrayList;
import java.util.List;

public class PrintTask implements ParsedString {
    private Print print;
    private List<String> colors = new ArrayList<>();
    private String filamentType;

    public PrintTask(Print print, String filamentType, String[] colors) {
        this.print = print;
        this.filamentType = filamentType;
        for(int i = 0; i < colors.length; i++) {
            this.colors.add(colors[i]);
        }
    }

    public PrintTask(Print print) {
        this.print = print;
    }

    public String getName() {
        return print.getName() + " - " + filamentType;
    }

    public boolean matches(Print print, String filamentType, String[] colors) {
        if(!print.equals(this.print))
            return false;
        if(!(filamentType.equals((this.filamentType))))
            return false;
        for(int i = 0; i < colors.length; i++) {
            if(!colors[i].equals(this.colors.get(i))) {
                return false;
            }
        }
        return true;
    }

    public String parsedString() {
        String output = print.getName() + " " + filamentType +" [";
        if(colors.size()>1) {
            for(String c: colors) {
               output += c + ", ";
            }
        } else {
            output += colors.get(0);
        }
        output += "]";
        return output;
    }
}
