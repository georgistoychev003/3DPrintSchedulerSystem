package nl.saxion.Models;

public interface Observer {
    void update(String eventType, Object data);
}
