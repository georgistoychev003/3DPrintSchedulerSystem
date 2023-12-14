package nl.saxion;


public interface FileHandler {
    Object readFile(String filename);
    void writeFile(String filename, Object data);
}

