package nl.saxion;

import nl.saxion.Models.Print;

import java.util.ArrayList;
import java.util.List;

public class PrintManager {

    private List<Print> prints = new ArrayList<Print>(); //TODO use interface


    //FIXME: code smell // too many arguments / all can be replaced by a Print object
    public void addPrint(String name, int height, int width, int length, ArrayList<Double> filamentLength, int printTime) {
        Print p = new Print(name, height, width, length, filamentLength, printTime);
        prints.add(p);
    }

    public List<Print> getPrints() {
        return prints;
    }
    //FIXME: code smell // make methods that are used only within this class private
    //FIXME: code smell??? // can be replaced with stream ( i think)
    public Print findPrint(String printName) {
        for (Print p : prints) {
            if (p.getName().equals(printName)) {
                return p;
            }
        }
        return null;
    }


    public Print findPrint(int index) {
        if(index > prints.size() -1) {
            return null;
        }
        return prints.get(index);
    }


}
