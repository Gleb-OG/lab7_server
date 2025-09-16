package main.commands;

import main.exceptions.InvalidDataException;
import main.managers.KeyManager;
import main.network.Request;
import static main.Server.collectionManager;

/**
 * Команда, удаляющая организацию из коллекции по ключу.
 */
public class RemoveKey extends Command {

    public RemoveKey() {
        super("remove_key <key>", "Удаление элемента по ключу", 1, 0);
    }

    @Override
    public boolean check(Request request) {
        if (!request.getCommandArg().matches("^\\d+$")) return false;
        int key = Integer.parseInt(request.getCommandArg());
        try {
            return collectionManager.getOrganizationByKey(key) != null;
        } catch (InvalidDataException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String execute(Request request) {
        try {
            String removingKey = request.getCommandArg();
            if (!removingKey.matches("^\\d+$")) {
                throw new InvalidDataException("Ключ должен быть строго больше нуля.");
            }
            int key = Integer.parseInt(removingKey);
            if (KeyManager.checkKeyExisting(key) && collectionManager.checkAccessToOrganization(key, request.getLogin())) {
                collectionManager.removeOrganizationByKey(key);
                return ("Элемент с ключом " + key + " удалён из коллекции.");
            } else {
                return ("Элемент с ключом " + key + " недоступен или отсутствует.");
            }
        } catch (InvalidDataException e) {
            return e.getMessage();
        }
    }

    @Override
    public String execute(String[] args) {
        int key = Integer.parseInt(args[0]);
        if (KeyManager.checkKeyExisting(key) && collectionManager.checkAccessToOrganization(key, "default")) {
            collectionManager.removeOrganizationByKey(key);
            return ("Элемент с ключом " + key + " удалён из коллекции.");
        } else {
            return ("Элемент с ключом " + key + " недоступен или отсутствует.");
        }
    }
}
