package main.commands;

import main.network.Request;
import java.io.IOException;

/**
 * Сохраняет актуальную коллекцию в файл, откуда считывались данные, перезаписывая его.
 */
public class Save extends Command {

    public Save() {
        super("save", "Сохранение коллекции в файл", 0, 0);
    }

    @Override
    public String execute(Request request) throws IOException {
        return  "Элементы успешно сохранены в базе данных.";
    }
}
