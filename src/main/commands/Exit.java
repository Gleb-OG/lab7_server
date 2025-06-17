package main.commands;

import main.network.Request;

/**
 * Команда, завершающая работу программы без сохранения.
 */
public class Exit extends Command {

    public Exit() {
        super("exit", "Завершение программы без сохранения в файл", 0);
    }

    @Override
    public String execute(Request request) {
        System.exit(0);
    }
}