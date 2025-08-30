package main.commands;

import static main.Server.collectionManager;
import main.model.Organization;
import main.exceptions.InvalidDataException;
import main.managers.KeyManager;
import main.network.Request;
import main.utils.Validator;
import java.io.IOException;
import java.util.*;

/**
 * Команда, удаляющая все организации из коллекции, годовой оборот которых больше введенного.
 */
public class RemoveGreater extends Command {

    public RemoveGreater() {
        super("remove_greater <annual turnover>", "Удаление элементов, превышающих введенный(сравнение по годовому обороту)", 1, 0);
    }

    @Override
    public boolean check(Request request) {
        if (!request.getCommandArg().matches("^\\d+$")) return false;
        long annualTurnover = Long.parseLong(request.getCommandArg());
        return annualTurnover > 0;
    }

    @Override
    public String execute(Request request) throws IOException {
        try {
            String sizeOfAnnualTurnoverString = request.getCommandArg();
            if (collectionManager.getCollection().values().isEmpty()) {
                return ("Коллекция пуста.");
            }

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
                if (entry.getValue() == null) {continue;}
                if (entry.getValue().getAnnualTurnover() > compareOrg.getAnnualTurnover()) {
                    iterator.remove();
                    KeyManager.releaseKey(entry.getKey());
                    countToRemove++;
                }
            }

            if (countToRemove == 0) {
                return ("Нет организаций, у которых годовой оборот больше чем " + sizeOfAnnualTurnoverString);
            } else {
                return ("Удалено " + countToRemove +
                        " организаций с годовым оборотом больше чем " + sizeOfAnnualTurnoverString + ".");
            }
        } catch (NumberFormatException e) {
            return ("Слишком большое число.");
        } catch (InvalidDataException e) {
            return ("Это поле может быть только положительным числом.");
        }
    }

    @Override
    public String execute(String[] args) {
        String annualTurnoverString = args[0];
        if (collectionManager.getCollection().values().isEmpty()) {
            return ("Коллекция пуста.");
        }

        long annualTurnover = Long.parseLong(annualTurnoverString);
        Organization compareOrg = new Organization();
        compareOrg.setAnnualTurnover(annualTurnover);

        Iterator<Map.Entry<Integer, Organization>> iterator = collectionManager.getCollection().entrySet().iterator();

        int countToRemove = 0;
        while (iterator.hasNext()) {
            Map.Entry<Integer, Organization> entry = iterator.next();
            if (entry.getValue() == null) {continue;}
            if (entry.getValue().getAnnualTurnover() > compareOrg.getAnnualTurnover()) {
                iterator.remove();
                KeyManager.releaseKey(entry.getKey());
                countToRemove++;
            }
        }

        if (countToRemove == 0) {
            return ("Нет организаций, у которых годовой оборот больше чем " + annualTurnoverString);
        } else {
            return ("Удалено " + countToRemove +
                    " организаций с годовым оборотом больше чем " + annualTurnoverString + ".");
        }
    }
}