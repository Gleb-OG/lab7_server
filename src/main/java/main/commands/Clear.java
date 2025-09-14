package main.commands;

import static main.Server.collectionManager;

import main.managers.KeyManager;
import main.network.Request;
import java.util.TreeMap;

/**
 * Команда, очищающая коллекцию организаций.
 */
public class Clear extends Command {

    public Clear() {
        super("clear", "Очищение коллекции", 0, 0);
    }

    @Override
    public String execute(Request request) {
        if (collectionManager.getCollection().isEmpty()) {
            return "Коллекция итак пустая.";
        } else {
            collectionManager.clearCollection();
            KeyManager.clearAllKeys();
            return "Коллекция очищена.";
        }
    }
}
