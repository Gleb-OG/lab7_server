package main.commands;

import main.Server;
import main.exceptions.InvalidDataException;
import main.managers.KeyManager;
import main.network.Request;

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
    public String execute(Request request) {
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
    public String execute(String[] args) {
        int key = Integer.parseInt(args[0]);
        if (KeyManager.checkKeyExisting(key)) {
            collectionManager.removeOrganizationByKey(key);
            System.out.println("Элемент с ключом " + key + " удалён из коллекции.");
        } else {
            System.out.println("Элемента с ключом " + key + " не обнаружено в коллекции.");
        }
    }
}
