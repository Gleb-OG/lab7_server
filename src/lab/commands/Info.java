package lab.commands;

import lab.network.Request;
import lab.network.Response;

/**
 * Выводит информацию о коллекции.
 */
public class Info extends Command {

    public Info() {
        super("info", "Информация о коллекции", 0);
    }

    @Override
    public Response execute(Request request) {
        collectionManager.info();
    }
}
