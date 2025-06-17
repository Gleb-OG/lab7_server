package main.invoker;

import main.commands.*;
import main.exceptions.InvalidDataException;
import main.exceptions.NoSuchCommandException;
import main.network.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Инвокер, инициализирующий и хранящий в map команды и открывающий доступ к ним через геттер.
 */
public class Invoker {
    private final Map<String, Command> clientCommands = new HashMap<>();
    private final Map<String, Command> serverCommands = new HashMap<>();

    public Invoker() {
        clientCommands.put("help", new Help());
        clientCommands.put("info", new Info());
        clientCommands.put("show", new Show());
        clientCommands.put("insert", new InsertElement());
        clientCommands.put("update", new UpdateID());
        clientCommands.put("remove_key", new RemoveKey());
        clientCommands.put("clear", new Clear());
        clientCommands.put("execute_script", new ExecuteScript());
        clientCommands.put("exit", new Exit());
        clientCommands.put("remove_greater", new RemoveGreater());
        clientCommands.put("replace_if_lower", new ReplaceIfLower());
        clientCommands.put("remove_lower_key", new RemoveLowerKey());
        clientCommands.put("sum_of_annual_turnover", new SumOfAnnualTurnover());
        clientCommands.put("filter_by_annual_turnover", new FilterByAnnualTurnover());
        clientCommands.put("filter_greater_than_official_address", new FilterGreaterThanOfficialAddress());

        serverCommands.put("save", new Save());
    }

    public Map<String, Command> getClientCommands() {
        return clientCommands;
    }

    public Map<String, Command> getServerCommands() {
        return serverCommands;
    }

    public Command getClientCommandByKey(String key) {
        return clientCommands.get(key);
    }

    public Command getServerCommandByKey(String key) {
        return serverCommands.get(key);
    }

    public String executeClientCommand(Request request) {
        try {
            Command command = getClientCommandByKey(request.getCommandName());
            return command.execute(request);
        } catch (NoSuchCommandException | InvalidDataException | IOException e){
            e.printStackTrace();
            return "Не найдена команда: " + e.getMessage() + ".";
        }
    }

    public String executeServerCommand(Request request) {
        try {
            Command command = getServerCommandByKey(request.getCommandName());
            return command.execute(request);
        } catch (NoSuchCommandException | InvalidDataException | IOException e){
            e.printStackTrace();
            return "Не найдена команда: " + e.getMessage() + ".";
        }
    }
}