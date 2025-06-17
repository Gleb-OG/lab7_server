package main.commands;

import main.managers.KeyManager;
import main.network.Request;
import main.network.Response;
import main.utils.IDGenerator;

import java.util.TreeMap;

/**
 * Команда, очищающая коллекцию организаций.
 */
public class Clear extends Command {

    public Clear() {
        super("clear", "Очищение коллекции", 0);
    }

    @Override
    public String execute(Request request) {
        if (collectionManager.getCollection().isEmpty()) {
            return "Коллекция итак пустая.";
        } else {
            collectionManager.loadCollection(new TreeMap<>());
            KeyManager.clearAllKeys();
            return "Коллекция очищена.";
        }
    }
}
