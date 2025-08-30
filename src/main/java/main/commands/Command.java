package main.commands;

import main.exceptions.InvalidDataException;
import main.interfaces.CommandInterface;
import main.network.Request;

/**
 * Абстрактный класс, задающий общую структуру команд и реализующий у каждой из них
 * геттеры их названия, описания и количества аргументов.
 */
public abstract class Command implements CommandInterface {

    protected String nameOfCommand;
    protected String description;
    protected int stringArgsAmount;
    protected int objectArgsAmount;

    public Command(String name, String description, int stringArgsAmount, int objectArgsAmount) {
        this.nameOfCommand = name;
        this.description = description;
        this.stringArgsAmount = stringArgsAmount;
        this.objectArgsAmount = objectArgsAmount;
    }

    public String getNameOfCommand() {
        return nameOfCommand;
    }

    public String getDescription() {
        return description;
    }

    public int getStringArgsAmount() {
        return stringArgsAmount;
    }

    public int getObjectArgsAmount() {
        return objectArgsAmount;
    }

    public boolean check(Request request) {
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