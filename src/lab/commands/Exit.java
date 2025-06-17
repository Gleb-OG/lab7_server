package lab.commands;

import lab.network.Request;
import lab.network.Response;

/**
 * Команда, завершающая работу программы без сохранения.
 */
public class Exit extends Command {

    public Exit() {
        super("exit", "Завершение программы без сохранения в файл", 0);
    }

    @Override
    public Response execute(Request request) {
        System.exit(0);
    }
}