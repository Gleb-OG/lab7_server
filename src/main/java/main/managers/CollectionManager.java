package main.managers;

import main.database.DBManager;
import main.model.Organization;
import main.exceptions.InvalidDataException;
import main.utils.IDGenerator;

import java.time.LocalDate;
import java.util.*;

import static main.Server.collectionManager;

/**
 * Менеджер, управляющий коллекцией: хранит коллекцию, предоставляет доступ
 * к ее редактированию и информации о ней.
 */
public class CollectionManager {
    private TreeMap<Integer, Organization> collection = new TreeMap<>();
    private final LocalDate initializationDate = LocalDate.now();
    private static DBManager dbManager = new DBManager();

    public void loadCollection(TreeMap<Integer, Organization> collection) {
        this.collection = collection;
    }

    public synchronized void loadCollectionFromDB() {
        TreeMap<Integer, Organization> organizations = dbManager.getOrganizations();
        TreeMap<Integer, Organization> newcollection = new TreeMap<>();
        for (Map.Entry<Integer, Organization> entry : organizations.entrySet()) {
            Organization organization = entry.getValue();
            newcollection.put(entry.getKey(), organization);
        }

        this.collection = newcollection;
    }

    public synchronized boolean checkAccessToOrganization(int key, String login) {
        Organization organization = collection.get(key);
        return organization != null && organization.getUsername().equals(login);
    }

    public synchronized void clearCollection(String login) {
        List<Integer> keysToRemove = new ArrayList<>();

        for (Map.Entry<Integer, Organization> entry : collectionManager.getCollection().entrySet()) {
            if (entry.getValue() != null && entry.getValue().getUsername().equals(login)) {
                keysToRemove.add(entry.getKey());
            }
        }

        for (int k : keysToRemove) {
            collectionManager.removeOrganizationByKey(k);
            KeyManager.releaseKey(k);
        }

        dbManager.deleteAllOrganizations(login);
    }

    public synchronized void addOrganization(Organization organization) throws InvalidDataException {
        int key = KeyManager.generateKey();
        try {
            KeyManager.registerKey(key);
        } catch (IllegalArgumentException e) {
            throw new InvalidDataException(e.getMessage());
        }
        collection.put(key, organization);
        dbManager.insertOrganizations(key, organization);
    }

    public synchronized void insertOrganization(int key, Organization organization) {
        organization.setID(IDGenerator.generateID());
        TreeMap<Integer, Organization> tempMap = new TreeMap<>();
        tempMap.put(key, organization);

        for (Integer oldKey : collection.keySet()) {
            if (oldKey == key) {
                try {
                    KeyManager.registerKey(oldKey + 1);
                } catch (IllegalArgumentException ignore) {
                }
                tempMap.put(oldKey + 1, collection.get(oldKey));
            } else if (oldKey > key) {
                try {
                    KeyManager.releaseKey(oldKey);
                    KeyManager.registerKey(oldKey + 1);
                } catch (IllegalArgumentException ignore) {
                }
                tempMap.put(oldKey + 1, collection.get(oldKey));
            } else {
                tempMap.put(oldKey, collection.get(oldKey));
            }
        }

        this.collection = tempMap;
        dbManager.insertOrganizations(key, organization);

        try {
            KeyManager.registerKey(key);
        } catch (IllegalArgumentException ignore) {
        }
    }

    public synchronized TreeMap<Integer, Organization> getCollection() {
        return collection;
    }

    public synchronized Organization getOrganizationByKey(int key) throws InvalidDataException {
        if (collection.containsKey(key)) {
            return collection.get(key);
        }
        if (!main.Server.scriptMode) throw new InvalidDataException("Элемента с таким ключом не обнаружено.");
        return null;
    }

    public synchronized void removeOrganizationByKey(int key) {
        String login = collection.get(key).getUsername();
        collection.remove(key);
        dbManager.deleteOrganization(key);
        KeyManager.releaseKey(key);
    }


    public synchronized void updateKey(int key, Organization organization) throws InvalidDataException {
        organization.setID(IDGenerator.generateID());
        try {
            Organization oldOrganization = getOrganizationByKey(key);
            if (oldOrganization != null) {
                collection.remove(key);
                collection.put(key, organization);
            }
            dbManager.updateOrganization(key, organization);
        } catch (IndexOutOfBoundsException e) {
            throw new InvalidDataException("Введите натуральное число.");
        } catch (InvalidDataException e) {
            throw new InvalidDataException(e.getMessage());
        }
    }

    public synchronized String info() {
        return ("Тип коллекции: TreeMap, \n" +
                "Дата создания: " + initializationDate + ",\n" +
                "Количество элементов: " + collection.size());
    }
}