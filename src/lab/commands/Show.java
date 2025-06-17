package lab.commands;

import lab.model.Organization;
import lab.network.Request;
import lab.network.Response;

import java.util.TreeMap;

/**
 * Команда, выводящая информацию о каждой организации в коллекции.
 */
public class Show extends Command {

    public Show() {
        super("show", "Вывод всех элементов коллекции", 0);
    }

    @Override
    public Response execute(Request request) {
        TreeMap<Integer, Organization> organizations = collectionManager.getCollection();
        for (Integer key : organizations.keySet()) {
            System.out.println("---------Organization---------" +
                    "\nkey = " + key +
                    "\n" + organizations.get(key));
        }
        System.out.println("Количество элементов коллекции: " + organizations.size());
    }
}
