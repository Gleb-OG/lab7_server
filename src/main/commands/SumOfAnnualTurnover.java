package main.commands;

import main.network.Request;

/**
 * Выводит сумму годового оборота всех организаций.
 */
public class SumOfAnnualTurnover extends Command {

    public SumOfAnnualTurnover() {
        super("sum_of_annual_turnover", "Вывод суммы годового оборота всех организаций", 0);
    }

    @Override
    public String execute(Request request) {
        if (!collectionManager.getCollection().values().isEmpty()) {
            long sum = collectionManager.getCollection().values()
                    .stream().mapToLong(org -> org.getAnnualTurnover()).sum();
            System.out.println("Сумма годового оборота всех организаций: " + sum);
        } else {
            System.out.println("Коллекция пуста.");
        }
    }
}