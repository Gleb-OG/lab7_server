package lab.invoker;

import lab.commands.*;
import lab.exceptions.NoSuchCommandException;
import lab.network.Request;
import lab.network.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Инвокер, инициализирующий и хранящий в map команды и открывающий доступ к ним через геттер.
 */
public class Invoker {
    private final Map<String, Command> commands = new HashMap<>();

    public Invoker() {
        commands.put("help", new Help());
        commands.put("info", new Info());
        commands.put("show", new Show());
        commands.put("insert", new InsertElement());
        commands.put("update", new UpdateID());
        commands.put("remove_key", new RemoveKey());
        commands.put("clear", new Clear());
        commands.put("save", new Save());
        commands.put("execute_script", new ExecuteScript());
        commands.put("exit", new Exit());
        commands.put("remove_greater", new RemoveGreater());
        commands.put("replace_if_lower", new ReplaceIfLower());
        commands.put("remove_lower_key", new RemoveLowerKey());
        commands.put("sum_of_annual_turnover", new SumOfAnnualTurnover());
        commands.put("filter_by_annual_turnover", new FilterByAnnualTurnover());
        commands.put("filter_greater_than_official_address", new FilterGreaterThanOfficialAddress());
    }

    public Map<String, Command> getCommands() {
        return commands;
    }

    public Command getCommandByKey(String key) {
        return commands.get(key);
    }

    public Response call(Request request) {
        try {
            Command command = getCommandByKey(request.getCommandName());
            return command.execute(request.getCommandArg(), request.getCommandObjArg());
        } catch (NoSuchCommandException e){
            return new Response("Не нашел команду: " + e.getMessage());
        }
    }
}