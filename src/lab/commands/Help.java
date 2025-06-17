package lab.commands;

import lab.network.Request;
import lab.network.Response;

import static lab.Server.inv;

/**
 * Выводит справку по доступным командам.
 */
public class Help extends Command {

    public Help() {
        super("help", "Справка по доступным командам", 0);
    }

    @Override
    public Response execute(Request request) {
        for (Command command : inv.getCommands().values()) {
            System.out.println("- " + command.nameOfCommand + ": " + command.getDescription());
        }
    }
}
