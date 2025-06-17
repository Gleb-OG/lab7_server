package main.commands;

import main.Server;
import main.exceptions.InvalidDataException;
import main.network.Request;
import main.utils.Validator;
import java.io.IOException;

/**
 * Команда, выводящая организации из коллекции, годовой оборот которых равен введенному.
 */
public class FilterByAnnualTurnover extends Command {

    public FilterByAnnualTurnover() {
        super("filter_by_annual_turnover <annual turnover>", "Вывод организаций, годовой оборот которых равен введенному", 1);
    }

    @Override
    public boolean check(String[] args) {
        return args[0].matches("^\\d+$");
    }

    @Override
    public String execute(Request request) throws InvalidDataException, IOException {
        try {
            String sizeOfAnnualTurnover = Server.console.getToken(1);
            long annualTurnover = Validator.parseAnnualTurnover(sizeOfAnnualTurnover);

            int count = 0;
            for (int key : collectionManager.getCollection().keySet()) {
                if (collectionManager.getCollection().get(key).getAnnualTurnover() == annualTurnover) {
                    System.out.println("-------Organization-------" + "\nkey = " + key + "\n" +
                            collectionManager.getCollection().get(key));
                    count++;
                }
            }
            if (count == 0 || collectionManager.getCollection().values().isEmpty()) {
                if (count == 0 && !collectionManager.getCollection().values().isEmpty()) {
                    System.out.println("В коллекции отсутствуют организации, годовой оборот которых равен " + sizeOfAnnualTurnover + ".");
                } else {
                    System.out.println("Коллекция пуста.");
                }
            } else {
                System.out.println("Все организации, годовой оборот которых равен " + sizeOfAnnualTurnover + ".");
            }
        } catch (InvalidDataException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public String execute(String[] args) throws InvalidDataException {
        long annualTurnover = Long.parseLong(args[0]);
        int count = 0;

        for (int key : collectionManager.getCollection().keySet()) {
            if (collectionManager.getCollection().get(key).getAnnualTurnover() == annualTurnover) {
                System.out.println("-------Organization-------" + "\nkey = " + key + "\n" +
                        collectionManager.getCollection().get(key));
                count++;
            }
        }

        if (count == 0 || collectionManager.getCollection().values().isEmpty()) {
            if (count == 0 && !collectionManager.getCollection().values().isEmpty()) {
                System.out.println("В коллекции отсутствуют организации, годовой оборот которых равен " + annualTurnover + ".");
            } else {
                System.out.println("Коллекция пуста.");
            }
        } else {
            System.out.println("Все организации, годовой оборот которых равен " + annualTurnover + ".");
        }
    }
}