package main.commands;

import main.Server;
import main.model.*;
import main.exceptions.InvalidDataException;
import main.network.Request;
import main.network.Response;
import main.utils.CSVProcessor;
import main.utils.InteractiveParser;
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
    public boolean check(String[] args) {
        return args[0].matches("^\\d+$");
    }

    @Override
    public String execute(Request request) throws InvalidDataException {
        try {
            String updatingKey = request.getCommandObjArg().toString();
            int key = Validator.validateInt(updatingKey);

            InteractiveParser parser = new InteractiveParser();
            Organization organization = parser.parseOrganization();
            if (collectionManager.getCollection().containsKey(key)) {
                collectionManager.removeOrganizationByKey(key);
            }
            collectionManager.addOrganization(key, organization);
            return new Response("Элемент успешно добавлен в коллекцию по ключу " + key);
        } catch (InvalidDataException e) {
            return new Response(e.getMessage());
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
        return new Response("Элемент успешно добавлен в коллекцию по ключу " + key);
    }
}