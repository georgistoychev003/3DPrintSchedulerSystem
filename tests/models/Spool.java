package models;

public class Spool implements ParsedString{
      private int id;
      private String color;
      private String filamentType;
      private double length;

      public Spool(int id, String color, String filamentType, double length) {
          this.id = id;
          this.color = color;
          this.filamentType = filamentType;
          this.length = length;
      }

      public void reduceLength(double shorten) {
          length -= shorten;
      }

    public String getColor() {
        return color;
    }

    public String getFilamentType() {
        return filamentType;
    }

    public String getName() {
          return filamentType + " - " + color;
    }

    public int getId() {
        return id;
    }

    public String parsedString() {
        String spool = "===== Spool " + id + " =====" + System.lineSeparator() +
                "color: " + color + System.lineSeparator() +
                "filamentType: " + filamentType + System.lineSeparator() +
                "length: " + length + System.lineSeparator();
        return spool;
    }

    @Override
    public String toString() {
          String spool = "===== Spool " + id + " =====" + System.lineSeparator() +
                  "color: " + color + System.lineSeparator() +
                  "filamentType: " + filamentType + System.lineSeparator() +
                  "length: " + length + System.lineSeparator();
        return spool;
    }
}
