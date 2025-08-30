package main.commands;

import static main.Server.collectionManager;
import static main.Server.filename;
import main.network.Request;
import main.utils.CSVProcessor;
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
        try {
            CSVProcessor.saveToCSV(filename, collectionManager.getCollection());
            return  "Элементы успешно сохранены в файл.";
        } catch (IOException e) {
            throw new IOException("Доступ к файлу отсутствует.");
        }
    }
}
