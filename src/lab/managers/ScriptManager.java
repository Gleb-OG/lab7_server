package lab.managers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Менеджер скриптов: хранит путь к файлу скрипта, проверяет существование этого файла.
 */
public class ScriptManager {

    private final String filePath;

    public ScriptManager(String filePath) {
        this.filePath = filePath;
    }

    public BufferedReader getBufferedReader() throws IOException {
        File scriptFile = new File(filePath);

        if (!scriptFile.exists()) {
            throw new IOException("Файл скрипта не найден: " + filePath);
        }

        if (!scriptFile.canRead()) {
            throw new IOException("Нет прав на чтение файла: " + filePath);
        }

        return new BufferedReader(new FileReader(scriptFile));
    }
}
