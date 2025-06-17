package lab.commands;

import lab.Server;
import lab.model.*;
import lab.exceptions.InvalidDataException;
import lab.managers.KeyManager;
import lab.network.Request;
import lab.network.Response;
import lab.utils.CSVProcessor;
import lab.utils.IDGenerator;
import lab.utils.InteractiveParser;
import lab.utils.Validator;
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
    public boolean check(String[] args) {
        if (!args[0].matches("^\\d+$")) return false;
        int key = Integer.parseInt(args[0]);
        return IDGenerator.checkIdExisting(key);
    }

    @Override
    public Response execute(Request request) {
        try {
            String updatingKey = Server.console.getToken(1);
            if (!updatingKey.matches("^\\d+$")) {
                throw new InvalidDataException("Ключ может быть только натуральным числом.");
            }
            int key = Validator.validateInt(updatingKey);

            if (KeyManager.checkKeyExisting(key)) {
                TreeMap<Integer, Organization> collection = collectionManager.getCollection();
                InteractiveParser parser = new InteractiveParser();
                try {
                    Organization oldOrganization = collectionManager.getOrganizationByKey(key);
                    if (oldOrganization != null) {
                        Organization newOrganization = parser.parseOrganization();

                        if (collection.get(key).getAnnualTurnover() > newOrganization.getAnnualTurnover()) {
                            collectionManager.removeOrganizationByKey(key);
                            collectionManager.addOrganization(key, newOrganization);
                            System.out.println("Элемент с ключом " + key + " успешно обновлен.");
                        } else {
                            System.out.println("Элемент с ключом " + key + " не был обновлен, " +
                                    "так как у введенной организации годовой оборот больше или равен нынешнему.");
                        }
                    }
                } catch (IndexOutOfBoundsException ex) {
                    System.out.println("Введите натуральное число.");
                } catch (InvalidDataException ignore) {
                }
            } else {
                System.out.println("Элемент с ключом " + key + " отсутствует.");
            }
        } catch (InvalidDataException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Response execute(String[] args) throws InvalidDataException {
        String updatingKey = args[0];
        int key = Integer.parseInt(updatingKey);

        if (KeyManager.checkKeyExisting(key)) {
            TreeMap<Integer, Organization> collection = collectionManager.getCollection();
            try {
                Organization oldOrganization = collectionManager.getOrganizationByKey(key);
                if (oldOrganization != null) {
                    Organization newOrganization = CSVProcessor.parseOrganizationFromString(args[1]);

                    if (collection.get(key).getAnnualTurnover() > newOrganization.getAnnualTurnover()) {
                        collectionManager.removeOrganizationByKey(key);
                        collectionManager.addOrganization(key, newOrganization);
                        System.out.println("Элемент с ключом " + key + " успешно обновлен.");
                    } else {
                        System.out.println("Элемент с ключом " + key + " не был обновлен, " +
                                "так как у введенной организации годовой оборот больше или равен нынешнему.");
                    }
                }
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                throw new InvalidDataException("Введите натуральное число.");
            } catch (InvalidDataException e) {
                throw new InvalidDataException(e.getMessage());
            }
        } else {
            System.out.println("Элемент с ключом " + key + " отсутствует.");
        }
    }
}
