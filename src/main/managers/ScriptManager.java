package main.managers;

import main.commands.Command;
import main.exceptions.WrongArgsNumber;
import main.invoker.Invoker;
import main.network.Request;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * Менеджер скриптов: запускает скрипты.
 */
public class ScriptManager {
    private Invoker inv;

    public ScriptManager(Invoker inv) {
        this.inv = inv;
    }

    public String runScript(String filePath, List<String> commandsWithArgsList) throws IOException {
        try (BufferedReader reader = getBufferedReader(filePath)) {
            String line;
            String strOutput = "";
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] tokens = Arrays.stream(line.split(" "))
                        .filter(s -> !s.isEmpty())
                        .toArray(String[]::new);

                Command command = inv.getClientCommands().get(tokens[0]);
                if (command == null) {
                    continue;
                }

                if ("execute_script".equals(command.getNameOfCommand())) {
                    break;
                }

                if (tokens.length - 1 != command.getArgsAmount()) {
                    strOutput += ("Ошибка при чтении скрипта: неверное количество аргументов.");
                    continue;
                }

                try {
                    if (tokens.length == 1 || tokens.length == 2) {
                        if (tokens.length == 2 && command.check(new Request(tokens[0], tokens[1]))) {
                            strOutput += ("Выполнение команды: " + line);
                        } else if (tokens.length == 1) {
                            strOutput += ("Выполнение команды: " + line);
                        }
                    } else {
                        throw new WrongArgsNumber(command.getArgsAmount());
                    }

                    if (commandsWithArgsList.contains(tokens[0])) {
                        strOutput += command.execute(Arrays.copyOfRange(tokens, 1, tokens.length));
                    } else {
                        strOutput += command.execute(new Request(command.getNameOfCommand()));
                    }
                } catch (Exception e) {
                    strOutput += ("Ошибка при выполнении команды: " + e.getMessage());
                    return strOutput;
                }
            }
            return strOutput;
        }
    }

    public BufferedReader getBufferedReader(String filePath) throws IOException {
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
