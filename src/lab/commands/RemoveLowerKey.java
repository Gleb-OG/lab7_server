package lab.commands;

import lab.Server;
import lab.model.Organization;
import lab.exceptions.InvalidDataException;
import lab.managers.KeyManager;
import lab.network.Request;
import lab.network.Response;
import lab.utils.Validator;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Удаляет все организации из коллекции, ключ которых меньше введенного.
 */
public class RemoveLowerKey extends Command {

    public RemoveLowerKey() {
        super("remove_lower_key <key>", "Удаление элементов, ключ которых меньше введенного", 1);
    }

    @Override
    public boolean check(String[] args) {
        return args[0].matches("^\\d+$");
    }

    @Override
    public Response execute(Request request) throws IOException {
        try {
            boolean values = collectionManager.getCollection().values().isEmpty();
            String input = Server.console.getToken(1);
            int key = Validator.validateInt(input);

            if (!input.matches("^\\d+$")) {
                throw new InvalidDataException("Это поле может быть только числом.");
            }

            Iterator<Map.Entry<Integer, Organization>> iterator = collectionManager.getCollection().entrySet().iterator();

            int countToRemove = 0;
            while (iterator.hasNext()) {
                Map.Entry<Integer, Organization> entry = iterator.next();
                if (entry.getKey() < key) {
                    iterator.remove();
                    KeyManager.releaseKey(entry.getKey());
                    countToRemove++;
                }
            }

            if (countToRemove == 0 || values) {
                if (countToRemove == 0 && !values) {
                    System.out.println("Нет элементов, у которых ключ меньше " + key + ".");
                } else System.out.println("Коллекция пуста.");
            } else {
                System.out.println("Удалено " + countToRemove +
                        " организаций с ключами меньше " + key + ".");
            }
        } catch (NumberFormatException e) {
            System.out.println("Слишком большое число.");
        } catch (InvalidDataException e) {
            System.out.println("Это поле может быть только положительным числом.");
        }
    }

    @Override
    public Response execute(String[] args) {
        boolean values = collectionManager.getCollection().values().isEmpty();
        String input = args[0];
        int key = Integer.parseInt(input);

        Iterator<Map.Entry<Integer, Organization>> iterator = collectionManager.getCollection().entrySet().iterator();

        int countToRemove = 0;
        while (iterator.hasNext()) {
            Map.Entry<Integer, Organization> entry = iterator.next();
            if (entry.getKey() < key) {
                iterator.remove();
                KeyManager.releaseKey(entry.getKey());
                countToRemove++;
            }
        }

        if (countToRemove == 0 || values) {
            if (countToRemove == 0 && !values) {
                System.out.println("Нет элементов, у которых ключ меньше " + key + ".");
            } else System.out.println("Коллекция пуста.");
        } else {
            System.out.println("Удалено " + countToRemove +
                    " организаций с ключами меньше " + key + ".");
        }
    }
}
