package lab.commands;

import lab.Server;
import lab.exceptions.InvalidDataException;
import lab.managers.KeyManager;
import lab.network.Request;
import lab.network.Response;

/**
 * Команда, удаляющая организацию из коллекции по ключу.
 */
public class RemoveKey extends Command {

    public RemoveKey() {
        super("remove_key <key>", "Удаление элемента по ключу", 1);
    }

    @Override
    public boolean check(String[] args) {
        if (!args[0].matches("^\\d+$")) return false;
        int key = Integer.parseInt(args[0]);
        return collectionManager.getOrganizationByKey(key) != null;
    }

    @Override
    public Response execute(Request request) {
        try {
            String removingKey = Server.console.getToken(1);
            if (!removingKey.matches("^\\d+$")) {
                throw new InvalidDataException("Ключ должен быть строго больше нуля.");
            }
            int key = Integer.parseInt(removingKey);
            if (KeyManager.checkKeyExisting(key)) {
                collectionManager.removeOrganizationByKey(key);
                System.out.println("Элемент с ключом " + key + " удалён из коллекции.");
            } else {
                System.out.println("Элемента с ключом " + key + " не обнаружено в коллекции.");
            }
        } catch (InvalidDataException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Response execute(String[] args) {
        int key = Integer.parseInt(args[0]);
        if (KeyManager.checkKeyExisting(key)) {
            collectionManager.removeOrganizationByKey(key);
            System.out.println("Элемент с ключом " + key + " удалён из коллекции.");
        } else {
            System.out.println("Элемента с ключом " + key + " не обнаружено в коллекции.");
        }
    }
}
