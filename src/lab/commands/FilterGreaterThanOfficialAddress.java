package lab.commands;

import lab.Server;
import lab.exceptions.InvalidDataException;
import lab.network.Request;
import lab.network.Response;
import lab.utils.Validator;
import java.io.IOException;

/**
 * Команда, выводящая организации, название улицы которых по длине больше введенного.
 */
public class FilterGreaterThanOfficialAddress extends Command {

    public FilterGreaterThanOfficialAddress() {
        super("filter_greater_than_official_address <street name>", "Вывод организаций, официальный адрес (название улицы) которых по длине больше введенного", 1);
    }

    @Override
    public boolean check(String[] args) {
        return !args[0].isEmpty() && (args[0].length() <= 103);
    }

    @Override
    public Response execute(Request request) throws InvalidDataException, IOException {

        if (collectionManager.getCollection().values().isEmpty()) {
            System.out.println("Коллекция пуста.");
            return;
        }

        try {
            String streetName = Validator.validateStreetName(Server.console.getToken(1));

            int count = 0;
            for (int key : collectionManager.getCollection().keySet()) {
                if (collectionManager.getCollection().get(key).getOfficialAddress() != null
                        && collectionManager.getCollection().get(key).getOfficialAddress()
                        .getStreet().length() > streetName.length()) {
                    System.out.println("-------Organization-------" + "\nkey = " + key + "\n" +
                            collectionManager.getCollection().get(key));
                    count++;
                }
            }

            if (count == 0 || collectionManager.getCollection().values().isEmpty()) {
                if (count == 0 && !collectionManager.getCollection().values().isEmpty()) {
                    System.out.println("В коллекции отсутствуют организации, длина адреса которых больше " + streetName.length() + ".");
                } else {
                    System.out.println("Коллекция пуста.");
                }
            } else {
                System.out.println("Все организации, длина адреса которых больше " + streetName.length() + ".");
            }
        } catch (InvalidDataException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Response execute(String[] args) throws InvalidDataException {
        String streetName = args[0];

        int count = 0;
        for (int key : collectionManager.getCollection().keySet()) {
            if (collectionManager.getCollection().get(key).getOfficialAddress() != null
                    && collectionManager.getCollection().get(key).getOfficialAddress()
                    .getStreet().length() > streetName.length()) {
                System.out.println("-------Organization-------" + "\nkey = " + key + "\n" +
                        collectionManager.getCollection().get(key));
                count++;
            }
        }
        if (count == 0 || collectionManager.getCollection().values().isEmpty()) {
            if (count == 0 && !collectionManager.getCollection().values().isEmpty()) {
                System.out.println("В коллекции отсутствуют организации, длина адреса которых больше " + streetName.length() + ".");
            } else {
                System.out.println("Коллекция пуста.");
            }
        } else {
            System.out.println("Все организации, длина адреса которых больше " + streetName.length() + ".");
        }
    }
}