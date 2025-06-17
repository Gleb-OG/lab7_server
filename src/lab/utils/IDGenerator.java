package lab.utils;

import java.util.HashSet;

/**
 * Отвечает за работу с id: генерирует id, проверяет его уникальность.
 */
public class IDGenerator {

    private static int idCounter = 1;

    private static HashSet<Integer> usedIDs = new HashSet<>();

    public static int generateID() {
        int newID = 1;
        while (usedIDs.contains(newID)) {
            newID++;
        }
        usedIDs.add(newID);
        return newID;
    }

    public static void registerID(int id) {
        if (usedIDs.contains(id)) {
            throw new IllegalArgumentException("ID " + id + " уже используется.");
        }
        usedIDs.add(id);
        idCounter = Math.max(idCounter, id + 1);
    }

    public static boolean checkIdExisting(int id) {
        return usedIDs.contains(id);
    }
}

