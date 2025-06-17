package main.commands;

import main.Server;
import main.model.*;
import main.exceptions.InvalidDataException;
import main.network.Request;
import main.utils.CSVProcessor;
import main.utils.Validator;

/**
 * Команда, вставляющая введенную организацию по ключу.
 * Если ключ уже занят другой организацией, то значение по данном ключу перезаписывается новым.
 */
public class InsertElement extends Command {

    public InsertElement() {
        super("insert <key>", "Добавление элемента с заданным ключом", 1);
    }

    @Override
    public int getArgsAmount() {
        return Server.scriptMode ? 2 : 1;
    }

    @Override
    public boolean check(Request request) {
        return request.getCommandArg().matches("^\\d+$");
    }

    @Override
    public String execute(Request request) throws InvalidDataException {
        try {
            String updatingKey = request.getCommandArg();
            int key = Validator.validateInt(updatingKey);

            Organization organization = (Organization) request.getCommandObjArg();
            if (collectionManager.getCollection().containsKey(key)) {
                collectionManager.removeOrganizationByKey(key);
            }
            collectionManager.addOrganization(key, organization);
            return ("Элемент успешно добавлен в коллекцию по ключу " + key);
        } catch (InvalidDataException e) {
            return e.getMessage();
        }
    }

    @Override
    public String execute(String[] args) throws InvalidDataException {
        int key = Integer.parseInt(args[0]);
        Organization organization = CSVProcessor.parseOrganizationFromString(args[1]);
        if (collectionManager.getCollection().containsKey(key)) {
            collectionManager.removeOrganizationByKey(key);
        }
        collectionManager.addOrganization(key, organization);
        return ("Элемент успешно добавлен в коллекцию по ключу " + key);
    }
}