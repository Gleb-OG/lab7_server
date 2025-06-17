package lab.commands;

import lab.Server;
import lab.network.Request;
import lab.network.Response;
import lab.utils.CSVProcessor;
import java.io.IOException;

/**
 * Сохраняет актуальную коллекцию в файл, откуда считывались данные, перезаписывая его.
 */
public class Save extends Command {

    public Save() {
        super("save", "Сохранение коллекции в файл", 0);
    }

    @Override
    public Response execute(Request request) throws IOException {
        try {
        CSVProcessor.saveToCSV(Server.saveFilename, Server.collectionManager.getCollection());
        System.out.println("Элементы успешно сохранены в файл.");
        } catch (IOException e) {
            throw new IOException("Доступ к файлу отсутствует.");
        }
    }
}
