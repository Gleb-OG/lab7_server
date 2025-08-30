package main.commands;

import main.network.Request;

import static main.Server.inv;

/**
 * Выводит справку по доступным командам.
 */
public class Help extends Command {

    public Help() {
        super("help", "Справка по доступным командам", 0, 0);
    }

    @Override
    public String execute(Request request) {
        StringBuilder str = new StringBuilder();
        int cnt = 0;
        for (Command command : inv.getClientCommands().values()) {
            str.append("- ").append(command.nameOfCommand).append(": ").append(command.getDescription());
            cnt++;
            if (cnt != inv.getClientCommands().size()) {
                str.append("\n");
            }
        }
        return str.toString();
    }
}
