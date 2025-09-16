package main.commands;

import main.network.Request;
import static main.Server.inv;

/**
 * Команда, завершающая работу программы без сохранения.
 */
public class Exit extends Command {

    public Exit() {
        super("exit", "Завершение программы без сохранения в файл", 0, 0);
    }

    @Override
    public String execute(Request request) {
        return inv.executeServerCommand(new Request("save", request.getLogin(), request.getPassword()));
    }
}