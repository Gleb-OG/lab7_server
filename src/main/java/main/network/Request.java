package main.network;

import java.io.Serializable;

public class Request implements Serializable {
    private final String commandName;
    private final String commandArg;
    private final Serializable commandObjArg;
    private final String login;
    private final String password;

    public Request(String commandName, String commandArg, Serializable commandObjArg, String login, String password) {
        this.commandName = commandName;
        this.commandArg = commandArg;
        this.commandObjArg = commandObjArg;
        this.login = login;
        this.password = password;
    }

    public Request(String commandName, String commandArg, String login, String password) {
        this(commandName, commandArg, null, login, password);
    }

    public Request(String commandName, String login, String password) {
        this(commandName, null, null, login, password);
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

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "lab.main.network.Request{" +
                "commandName='" + commandName + '\'' +
                ", commandStrArg='" + commandArg + '\'' +
                ", commandObjArg=" + commandObjArg +
                '}';
    }
}