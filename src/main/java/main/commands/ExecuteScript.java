package main.commands;

import main.Server;
import main.network.Request;

import java.io.IOException;

/**
 * Команда, запускающая скрипт из файла.
 * Пустые поля вводимого объекта обозначать как "_".
 */
public class ExecuteScript extends Command {

    public ExecuteScript() {
        super("execute_script <filename>", "Считывание и исполнение скрипта из указанного файла.", 1, 0);
    }

    @Override
    public String execute(Request request) {
        main.Server.scriptMode = true;
        try {
            String filePath = request.getCommandArg();
            return Server.scriptManager.runScript(filePath, request.getLogin(), request.getPassword());
        } catch (IndexOutOfBoundsException e) {
            return ("Не указано имя файла для выполнения скрипта.");
        } catch (IOException e) {
            return ("Ошибка при чтении скрипта: " + e.getMessage());
        } finally {
            Server.scriptMode = false;
        }
    }
}