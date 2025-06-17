package main.commands;

import main.Server;
import main.managers.ScriptManager;
import main.network.Request;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Команда, запускающая скрипт из файла.
 * Пустые поля вводимого объекта обозначать как "_".
 */
public class ExecuteScript extends Command {
    List<String> list = Arrays.asList("insert", "update", "remove_key",
            "replace_if_lower", "remove_greater", "remove_lower_key",
            "filter_greater_than_official_address", "filter_by_annual_turnover");


    public ExecuteScript() {
        super("execute_script <filename>", "Считывание и исполнение скрипта из указанного файла.", 1);
    }

    @Override
    public String execute(Request request) {
        Server.scriptMode = true;
        try {
            String file = request.getCommandArg();
            ScriptManager scriptManager = new ScriptManager(file);
            return Server.runManager.runScript(scriptManager, list);
        } catch (IndexOutOfBoundsException e) {
            return ("Не указано имя файла для выполнения скрипта.");
        } catch (IOException e) {
            return ("Ошибка при чтении скрипта: " + e.getMessage());
        } finally {
            Server.scriptMode = false;
            Server.currentScriptReader = null;
        }
    }
}