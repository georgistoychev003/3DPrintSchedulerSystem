package nl.saxion.Models;

import nl.saxion.observer.Observer;

import java.util.ArrayList;
import java.util.List;

public abstract class Printer {
    private int id;
    private String name;
    private String manufacturer;
    private List<Observer> observers = new ArrayList<>();


    public Printer(int id, String printerName, String manufacturer) {
        this.id = id;
        this.name = printerName;
        this.manufacturer = manufacturer;
    }

    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    public void unregisterObserver(Observer observer) {
        observers.remove(observer);
    }

    protected void notifyObservers(String eventType, Object data) {
        for (Observer observer : observers) {
            observer.update(eventType, data);
        }
    }

    public int getId() {
        return id;
    }


    public abstract Spool[] getCurrentSpools();

    public abstract void setCurrentSpools(List<Spool> spools);

    public abstract boolean printFits(Print print);

    public void onPrintComplete() {
        notifyObservers("printComplete", null);
    }
    @Override
    public String toString() {
        return  "--------" + System.lineSeparator() +
                "- ID: " + id + System.lineSeparator() +
                "- Name: " + name + System.lineSeparator() +
                "- Manufacturer: " + manufacturer + System.lineSeparator() +
                "--------";
    }

    public String getName(){
        return name;
    }
}

