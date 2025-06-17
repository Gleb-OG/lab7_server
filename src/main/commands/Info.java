package main.commands;

import main.network.Request;

/**
 * Выводит информацию о коллекции.
 */
public class Info extends Command {

    public Info() {
        super("info", "Информация о коллекции", 0);
    }

    @Override
    public String execute(Request request) {
        collectionManager.info();
    }
}
