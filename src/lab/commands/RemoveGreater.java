package lab.commands;

import lab.Server;
import lab.model.Organization;
import lab.exceptions.InvalidDataException;
import lab.managers.KeyManager;
import lab.network.Request;
import lab.network.Response;
import lab.utils.Validator;
import java.io.IOException;
import java.util.*;

/**
 * Команда, удаляющая все организации из коллекции, годовой оборот которых больше введенного.
 */
public class RemoveGreater extends Command {

    public RemoveGreater() {
        super("remove_greater <annual turnover>", "Удаление элементов, превышающих введенный(сравнение по годовому обороту)", 1);
    }

    @Override
    public boolean check(String[] args) {
        if (!args[0].matches("^\\d+$")) return false;
        long annualTurnover = Long.parseLong(args[0]);
        return annualTurnover > 0;
    }

    @Override
    public Response execute(Request request) throws IOException {
        try {
            String sizeOfAnnualTurnoverString = Server.console.getToken(1);
            List<Organization> old_collection = List.copyOf(collectionManager.getCollection().values());

            if (!sizeOfAnnualTurnoverString.matches("^\\d+$")) {
                throw new InvalidDataException("Это поле может быть только числом.");
            }

            long sizeOfAnnualTurnover = Validator.parseAnnualTurnover(sizeOfAnnualTurnoverString);
            Organization compareOrg = new Organization();
            compareOrg.setAnnualTurnover(sizeOfAnnualTurnover);

            Iterator<Map.Entry<Integer, Organization>> iterator = collectionManager.getCollection().entrySet().iterator();

            int countToRemove = 0;
            while (iterator.hasNext()) {
                Map.Entry<Integer, Organization> entry = iterator.next();
                if (entry.getValue().compareTo(compareOrg) > 0) {
                    iterator.remove();
                    KeyManager.releaseKey(entry.getKey());
                    countToRemove++;
                }
            }

            if (countToRemove == 0 && !collectionManager.getCollection().values().isEmpty()) {
                System.out.println("Нет организаций, у которых годовой оборот больше чем " + sizeOfAnnualTurnoverString);
            } else if (old_collection.isEmpty()) {
                System.out.println("Коллекция пуста.");
            } else {
                System.out.println("Удалено " + countToRemove +
                        " организаций с годовым оборотом больше чем " + sizeOfAnnualTurnoverString + ".");
            }
        } catch (NumberFormatException e) {
            System.out.println("Слишком большое число.");
        } catch (InvalidDataException e) {
            System.out.println("Это поле может быть только положительным числом.");
        }
    }

    @Override
    public Response execute(String[] args) {
        String annualTurnoverString = args[0];
        List<Organization> old_collection = List.copyOf(collectionManager.getCollection().values());

        long annualTurnover = Long.parseLong(annualTurnoverString);
        Organization compareOrg = new Organization();
        compareOrg.setAnnualTurnover(annualTurnover);

        Iterator<Map.Entry<Integer, Organization>> iterator = collectionManager.getCollection().entrySet().iterator();

        int countToRemove = 0;
        while (iterator.hasNext()) {
            Map.Entry<Integer, Organization> entry = iterator.next();
            if (entry.getValue().compareTo(compareOrg) > 0) {
                iterator.remove();
                KeyManager.releaseKey(entry.getKey());
                countToRemove++;
            }
        }

        if (countToRemove == 0 && !collectionManager.getCollection().values().isEmpty()) {
            System.out.println("Нет организаций, у которых годовой оборот больше чем " + annualTurnoverString);
        } else if (old_collection.isEmpty()) {
            System.out.println("Коллекция пуста.");
        } else {
            System.out.println("Удалено " + countToRemove +
                    " организаций с годовым оборотом больше чем " + annualTurnoverString + ".");
        }
    }
}