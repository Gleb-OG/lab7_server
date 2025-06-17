package lab.managers;
import lab.Server;
import lab.model.Organization;
import lab.exceptions.InvalidDataException;
import lab.utils.InteractiveParser;
import java.time.LocalDate;
import java.util.*;


/**
 * Менеджер, управляющий коллекцией: хранит коллекцию, предоставляет доступ
 * к ее редактированию и информации о ней.
 */
public class CollectionManager {

    private TreeMap<Integer, Organization> collection = new TreeMap<>();
    private final LocalDate initializationDate = LocalDate.now();

    public void loadCollection(TreeMap<Integer, Organization> collection) {
        this.collection = collection;
    }

    public void loadCollectionWithoutKeys(List<Organization> organizations) {
        TreeMap<Integer, Organization> newcollection = new TreeMap<>();
        organizations.forEach(org -> newcollection.put(KeyManager.generateKey(), org));
        this.collection = newcollection;
    }

    public void addOrganization(int key, Organization organization) {
        try {
            KeyManager.registerKey(key);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        collection.put(key, organization);
    }

    public TreeMap<Integer, Organization> getCollection() {
        return collection;
    }

    public Organization getOrganizationByKey(int key) {
        if (collection.containsKey(key)) {
            return collection.get(key);
        }
        if (!Server.scriptMode) System.out.println("Элемента с таким ключом не обнаружено.");
        return null;
    }

    public void removeOrganizationByKey(int key) {
        Organization organization = getOrganizationByKey(key);
        if (organization != null) {
            collection.remove(key);
            KeyManager.releaseKey(key);
        }
    }

    public void updateKey(int key) {
        InteractiveParser parser = new InteractiveParser();
        try {
            Organization oldOrganization = getOrganizationByKey(key);
            if (oldOrganization != null) {
                collection.remove(key);
                Organization newOrganization = parser.parseOrganization();
                collection.put(key, newOrganization);
            }
        } catch (IndexOutOfBoundsException e) {
            System.out.println("Введите натуральное число.");
        } catch (InvalidDataException e) {
            System.out.println(e.getMessage());
        }
    }

    public void info() {
        System.out.println("Тип коллекции: TreeMap, \n" +
                "Дата создания: " + initializationDate + ",\n" +
                "Количество элементов: " + collection.size());
    }
}