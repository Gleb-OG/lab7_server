package lab.managers;

import java.util.HashSet;

/**
 * Менеджер, управляющий ключами TreeMap: хранит использованные ключи, генерирует новые и проверяет их уникальность.
 */
public class KeyManager {

    private static HashSet<Integer> usedKeys = new HashSet<>();

    public static int generateKey() {
        int newKey = 1;
        while (usedKeys.contains(newKey)) {
            newKey++;
        }
        usedKeys.add(newKey);
        return newKey;
    }

    public static void registerKey(int key) {
        if (usedKeys.contains(key)) {
            throw new IllegalArgumentException("Ключ " + key + " уже используется.");
        }
        usedKeys.add(key);
    }

    public static boolean checkKeyExisting(int key) {
        return usedKeys.contains(key);
    }

    public static void releaseKey(int key) {
        usedKeys.remove(key);
    }

    public static void clearAllKeys() {
        usedKeys.clear();
    }
}
