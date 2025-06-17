package main.commands;

import main.model.Organization;
import main.network.Request;

import java.util.Map;
import java.util.TreeMap;

/**
 * Команда, выводящая информацию о каждой организации в коллекции.
 */
public class Show extends Command {

    public Show() {
        super("show", "Вывод всех элементов коллекции", 0);
    }

    @Override
    public String execute(Request request) {
        String str = "";
        Map<Integer, Organization> collection = collectionManager.getCollection().entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .collect(
                        TreeMap::new,
                        (m, e) -> m.put(e.getKey(), e.getValue()),
                        TreeMap::putAll
                );
        for (int key : collection.keySet()) {
            str += ("---------Organization---------" +
                    "\nkey = " + key +
                    "\n" + collection.get(key));
        }
        str += ("Количество элементов коллекции: " + collection.size());
        return str;
    }
}
