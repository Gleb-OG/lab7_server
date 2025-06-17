package main.commands;

import main.Server;
import main.model.*;
import main.exceptions.InvalidDataException;
import main.managers.KeyManager;
import main.network.Request;
import main.utils.CSVProcessor;
import main.utils.IDGenerator;
import main.utils.Validator;
import java.util.TreeMap;

/**
 * Заменяет по ключу существующую в коллекции организацию на введенную, если годовой оборот новой организации меньше значения существующей.
 */
public class ReplaceIfLower extends Command {

    public ReplaceIfLower() {
        super("replace_if_lower <key>", "Замена элемента по ключу, если годовой оборот новой организации меньше старого", 1);
    }

    @Override
    public int getArgsAmount() {
        return Server.scriptMode ? 2 : 1;
    }

    @Override
    public boolean check(Request request) {
        if (!request.getCommandArg().matches("^\\d+$")) return false;
        int key = Integer.parseInt(request.getCommandArg());
        return IDGenerator.checkIdExisting(key);
    }

    @Override
    public String execute(Request request) {
        try {
            String updatingKey = request.getCommandArg();
            if (!updatingKey.matches("^\\d+$")) {
                throw new InvalidDataException("Ключ может быть только натуральным числом.");
            }
            int key = Validator.validateInt(updatingKey);

            if (KeyManager.checkKeyExisting(key)) {
                TreeMap<Integer, Organization> collection = collectionManager.getCollection();
                try {
                    Organization newOrganization = (Organization) request.getCommandObjArg();

                    if (collection.get(key).getAnnualTurnover() > newOrganization.getAnnualTurnover()) {
                        collectionManager.removeOrganizationByKey(key);
                        collectionManager.addOrganization(key, newOrganization);
                        return ("Элемент с ключом " + key + " успешно обновлен.");
                    } else {
                        return ("Элемент с ключом " + key + " не был обновлен, " +
                                "так как у введенной организации годовой оборот больше или равен нынешнему.");
                    }
                } catch (IndexOutOfBoundsException ex) {
                    return "Введите натуральное число.";
                } catch (InvalidDataException e) {
                    return e.getMessage();
                }
            } else {
                return "Элемент с ключом " + key + " отсутствует.";
            }
        } catch (InvalidDataException e) {
            return e.getMessage();
        }
    }

    @Override
    public String execute(String[] args) throws InvalidDataException {
        String updatingKey = args[0];
        int key = Integer.parseInt(updatingKey);

        if (KeyManager.checkKeyExisting(key)) {
            TreeMap<Integer, Organization> collection = collectionManager.getCollection();
            try {
                Organization newOrganization = CSVProcessor.parseOrganizationFromString(args[1]);

                if (collection.get(key).getAnnualTurnover() > newOrganization.getAnnualTurnover()) {
                    collectionManager.removeOrganizationByKey(key);
                    collectionManager.addOrganization(key, newOrganization);
                    return ("Элемент с ключом " + key + " успешно обновлен.");
                } else {
                    return ("Элемент с ключом " + key + " не был обновлен, " +
                            "так как у введенной организации годовой оборот больше или равен нынешнему.");
                }
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                throw new InvalidDataException("Введите натуральное число.");
            } catch (InvalidDataException e) {
                throw new InvalidDataException(e.getMessage());
            }
        } else {
            return ("Элемент с ключом " + key + " отсутствует.");
        }
    }
}
