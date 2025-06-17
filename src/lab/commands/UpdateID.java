package lab.commands;

import lab.Server;
import lab.model.*;
import lab.exceptions.InvalidDataException;
import lab.network.Request;
import lab.network.Response;
import lab.utils.CSVProcessor;
import lab.utils.IDGenerator;
import lab.utils.Validator;
import java.util.TreeMap;

/**
 * Команда, обновляющая по id организацию из коллекции.
 */
public class UpdateID extends Command {

    public UpdateID() {
        super("update <id>", "Обновление значения элемента коллекции по его id.", 1);
    }

    @Override
    public int getArgsAmount() {
        return Server.scriptMode ? 2 : 1;
    }

    @Override
    public boolean check(String[] args) {
        if (!args[0].matches("^\\d+$")) return false;
        int id = Integer.parseInt(args[0]);
        return IDGenerator.checkIdExisting(id);
    }

    @Override
    public Response execute(Request request) {
        try {
            String updatingID = Server.console.getToken(1);
            if (!updatingID.matches("^\\d+$")) {
                throw new InvalidDataException("id может быть только натуральным числом.");
            }

            int id = Validator.validateInt(updatingID);
            TreeMap<Integer, Organization> collection = collectionManager.getCollection();
            int key = 0;
            for (int k : collection.keySet()) if (collection.get(k).getID() == id) key = k;

            if (key == 0) {
                System.out.println("В коллекции отсутствует элемент с id " + id + ".");
                return;
            }

            collectionManager.updateKey(key);
            System.out.println("Элемент c id " + id + " успешно обновлен.");
        } catch (InvalidDataException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Response execute(String[] args) throws InvalidDataException {
        int id = Integer.parseInt(args[0]);
        TreeMap<Integer, Organization> collection = collectionManager.getCollection();

        int key = 0;
        for (int k : collection.keySet()) if (collection.get(k).getID() == id) key = k;
        if (key == 0) {
            System.out.println("В коллекции отсутствует элемент с id " + id + ".");
            return;
        }

        collectionManager.removeOrganizationByKey(key);
        Organization newOrganization = CSVProcessor.parseOrganizationFromString(args[1]);
        collectionManager.addOrganization(key, newOrganization);
        System.out.println("Элемент c id " + id + " успешно обновлен.");
    }
}
