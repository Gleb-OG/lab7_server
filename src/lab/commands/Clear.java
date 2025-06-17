package lab.commands;

import lab.managers.KeyManager;
import lab.network.Request;
import lab.network.Response;

import java.util.TreeMap;

/**
 * Команда, очищающая коллекцию организаций.
 */
public class Clear extends Command {

    public Clear() {
        super("clear", "Очищение коллекции", 0);
    }

    @Override
    public Response execute(Request request) {
        if (collectionManager.getCollection().isEmpty()) {
            System.out.println("Коллекция итак пустая.");
        } else {
            collectionManager.loadCollection(new TreeMap<>());
            KeyManager.clearAllKeys();
            System.out.println("Коллекция очищена.");
        }
    }
}
