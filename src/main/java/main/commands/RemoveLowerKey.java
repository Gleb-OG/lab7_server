package main.commands;

import static main.Server.collectionManager;
import main.model.Organization;
import main.exceptions.InvalidDataException;
import main.managers.KeyManager;
import main.network.Request;
import main.utils.Validator;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Удаляет все организации из коллекции, ключ которых меньше введенного.
 */
public class RemoveLowerKey extends Command {

    public RemoveLowerKey() {
        super("remove_lower_key <key>", "Удаление элементов, ключ которых меньше введенного", 1, 0);
    }

    @Override
    public boolean check(Request request) {
        return request.getCommandArg().matches("^\\d+$");
    }

    @Override
    public String execute(Request request) throws IOException {
        try {
            boolean values = collectionManager.getCollection().values().isEmpty();
            String input = request.getCommandArg();
            int key = Validator.validateInt(input);

            if (!input.matches("^\\d+$")) {
                throw new InvalidDataException("Это поле может быть только числом.");
            }

            HashSet<Integer> keysToRemove = new HashSet<>();
            Iterator<Map.Entry<Integer, Organization>> iterator = collectionManager.getCollection().entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Integer, Organization> entry = iterator.next();
                if (entry.getKey() < key && collectionManager.checkAccessToOrganization(entry.getKey(), request.getLogin())) {
                    keysToRemove.add(entry.getKey());
                }
            }

            int countToRemove = 0;
            for (int k : keysToRemove) {
                collectionManager.removeOrganizationByKey(k);
                countToRemove++;
            }

            if (countToRemove == 0 || values) {
                if (countToRemove == 0 && !values) {
                    return ("Нет доступных элементов, у которых ключ меньше " + key + ".");
                } else return ("Коллекция пуста.");
            } else {
                return ("Удалено " + countToRemove + " организаций с ключами меньше " + key + ".");
            }
        } catch (NumberFormatException e) {
            System.out.println("Слишком большое число.");
            return "Слишком большое число.";
        } catch (InvalidDataException e) {
            System.out.println("Это поле может быть только положительным числом.");
            return "Это поле может быть только положительным числом.";
        }
    }

    @Override
    public String execute(String[] args) {
        boolean values = collectionManager.getCollection().values().isEmpty();
        String input = args[0];
        int key = Integer.parseInt(input);

        HashSet<Integer> keysToRemove = new HashSet<>();
        Iterator<Map.Entry<Integer, Organization>> iterator = collectionManager.getCollection().entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Integer, Organization> entry = iterator.next();
            if (entry.getKey() < key && collectionManager.checkAccessToOrganization(entry.getKey(), "default")) {
                keysToRemove.add(entry.getKey());
            }
        }

        int countToRemove = 0;
        for (int k : keysToRemove) {
            collectionManager.removeOrganizationByKey(k);
            countToRemove++;
        }

        if (countToRemove == 0 || values) {
            if (countToRemove == 0 && !values) {
                return ("Нет элементов, у которых ключ меньше " + key + ".");
            } else return ("Коллекция пуста.");
        } else {
            return ("Удалено " + countToRemove +
                    " организаций с ключами меньше " + key + ".");
        }
    }
}
