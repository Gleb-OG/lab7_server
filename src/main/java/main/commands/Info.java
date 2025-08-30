package main.commands;

import main.network.Request;
import static main.Server.collectionManager;

/**
 * Выводит информацию о коллекции.
 */
public class Info extends Command {

    public Info() {
        super("info", "Информация о коллекции", 0, 0);
    }

    @Override
    public String execute(Request request) {
        return collectionManager.info();
    }
}
