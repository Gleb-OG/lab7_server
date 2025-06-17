package main.managers;
import main.Server;
import main.model.Organization;
import main.exceptions.InvalidDataException;
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

    public void addOrganization(int key, Organization organization) throws InvalidDataException {
        try {
            KeyManager.registerKey(key);
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException(e.getMessage());
        }
        collection.put(key, organization);
    }

    public TreeMap<Integer, Organization> getCollection() {
        return collection;
    }

    public Organization getOrganizationByKey(int key) throws InvalidDataException {
        if (collection.containsKey(key)) {
            return collection.get(key);
        }
        if (!Server.scriptMode) throw new InvalidDataException("Элемента с таким ключом не обнаружено.");
        return null;
    }

    public void removeOrganizationByKey(int key) throws InvalidDataException {
        Organization organization = getOrganizationByKey(key);
        if (organization != null) {
            collection.remove(key);
            KeyManager.releaseKey(key);
        }
    }

    public void updateKey(int key, Organization organization) throws InvalidDataException {
        try {
            Organization oldOrganization = getOrganizationByKey(key);
            if (oldOrganization != null) {
                collection.remove(key);
                Organization newOrganization = organization;
                collection.put(key, newOrganization);
            }
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidDataException("Введите натуральное число.");
        } catch (InvalidDataException e) {
            throw new InvalidDataException (e.getMessage());
        }
    }

    public String info() {
        return ("Тип коллекции: TreeMap, \n" +
                "Дата создания: " + initializationDate + ",\n" +
                "Количество элементов: " + collection.size());
    }
}