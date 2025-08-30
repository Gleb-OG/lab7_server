package main.commands;

import main.model.Organization;
import main.network.Request;

import java.util.Objects;

import static main.Server.collectionManager;

/**
 * Выводит сумму годового оборота всех организаций.
 */
public class SumOfAnnualTurnover extends Command {

    public SumOfAnnualTurnover() {
        super("sum_of_annual_turnover", "Вывод суммы годового оборота всех организаций", 0, 0);
    }

    @Override
    public String execute(Request request) {
        if (!collectionManager.getCollection().values().isEmpty()) {
            long sum = collectionManager.getCollection().values()
                    .stream().filter(Objects::nonNull).mapToLong(Organization::getAnnualTurnover).sum();
            return ("Сумма годового оборота всех организаций: " + sum);
        } else {
            return ("Коллекция пуста.");
        }
    }
}