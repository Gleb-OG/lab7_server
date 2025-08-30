package main.network;

import java.io.Serializable;

public class Response implements Serializable {
    private final String message;
    private final String commandName;

    public Response(String message) {
        this.message = message;
        this.commandName = "";
    }

    public Response(String message, String commandName) {
        this.message = message;
        this.commandName = commandName;
    }

    public String getMessage() {
        return message;
    }

    public String getCommandName() {
        return commandName;
    }
}