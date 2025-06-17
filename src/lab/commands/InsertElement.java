package lab.commands;

import lab.Server;
import lab.model.*;
import lab.exceptions.InvalidDataException;
import lab.network.Request;
import lab.network.Response;
import lab.utils.CSVProcessor;
import lab.utils.InteractiveParser;
import lab.utils.Validator;

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
    public Response execute(Request request) throws InvalidDataException {
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
    public Response execute(String[] args) throws InvalidDataException {
        int key = Integer.parseInt(args[0]);
        Organization organization = CSVProcessor.parseOrganizationFromString(args[1]);
        if (collectionManager.getCollection().containsKey(key)) {
            collectionManager.removeOrganizationByKey(key);
        }
        collectionManager.addOrganization(key, organization);
        return new Response("Элемент успешно добавлен в коллекцию по ключу " + key);
    }
}