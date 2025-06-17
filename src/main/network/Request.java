package main.network;

import java.io.Serializable;

public class Request implements Serializable {
    private final String commandName;
    private final String commandArg;
    private final Serializable commandObjArg;

    public Request(String commandName, String commandArg, Serializable commandObjArg) {
        this.commandName = commandName;
        this.commandArg = commandArg;
        this.commandObjArg = commandObjArg;
    }

    public Request(String commandName, String commandArg) {
        this(commandName, commandArg, null);
    }


    public String getCommandName() {
        return commandName;
    }

    public String getCommandArg() {
        return commandArg;
    }

    public Serializable getCommandObjArg() {
        return commandObjArg;
    }

    @Override
    public String toString() {
        return "lab.network.Request{" +
                "commandName='" + commandName + '\'' +
                ", commandStrArg='" + commandArg + '\'' +
                ", commandObjArg=" + commandObjArg +
                '}';
    }
}