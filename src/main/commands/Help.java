package main.commands;

import main.network.Request;

import static main.Server.inv;

/**
 * Выводит справку по доступным командам.
 */
public class Help extends Command {

    public Help() {
        super("help", "Справка по доступным командам", 0);
    }

    @Override
    public String execute(Request request) {
        String str = "";
        for (Command command : inv.getClientCommands().values()) {
            str += ("- " + command.nameOfCommand + ": " + command.getDescription());
        }
        return str;
    }
}
