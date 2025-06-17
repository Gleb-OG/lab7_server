package main.commands;

import main.Server;
import main.exceptions.InvalidDataException;
import main.interfaces.CommandInterface;
import main.managers.CollectionManager;
import main.network.Response;

/**
 * Абстрактный класс, задающий общую структуру команд и реализующий у каждой из них
 * геттеры их названия, описания и количества аргументов.
 */
public abstract class Command implements CommandInterface {

    protected static CollectionManager collectionManager = Server.collectionManager;
    protected String nameOfCommand;
    protected String description;
    protected int argsAmount;

    public Command(String name, String description, int argsAmount) {
        this.nameOfCommand = name;
        this.description = description;
        this.argsAmount = argsAmount;
    }

    public String getNameOfCommand() {
        return nameOfCommand;
    }

    public String getDescription() {
        return description;
    }

    public int getArgsAmount() {
        return argsAmount;
    }

    public boolean check(String[] args) {
        return true;
    }

    public String execute(String[] args) throws InvalidDataException {
        return "";
    }

    @Override
    public String description() {
        return nameOfCommand + " - " + description;
    }
}