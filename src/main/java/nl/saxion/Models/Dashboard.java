package nl.saxion.Models;

public class Dashboard implements Observer {
    private int totalSpoolChanges = 0;
    private int totalPrintsDone = 0;

    @Override
    public void update(String eventType, Object data) {
        if (eventType.equals("spoolChange")) {
            totalSpoolChanges++;
        } else if (eventType.equals("printComplete")) {
            totalPrintsDone++;
        }
    }

    public void displayStats() {
        System.out.println("Total Spool Changes: " + totalSpoolChanges);
        System.out.println("Total Prints Done: " + totalPrintsDone);
    }
}
