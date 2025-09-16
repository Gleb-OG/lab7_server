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


/**
 * Менеджер скриптов: запускает скрипты.
 */
public class ScriptManager {
    private Invoker inv;

    public ScriptManager(Invoker inv) {
        this.inv = inv;
    }

    public String runScript(String filePath, String login, String password) throws IOException {
        try (BufferedReader reader = getBufferedReader(filePath)) {
            String line;
            String strOutput = "";
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] tokens = Arrays.stream(line.split(" "))
                        .filter(s -> !s.isEmpty())
                        .toArray(String[]::new);

                try {
                    Command command = inv.getClientCommands().get(tokens[0]);

                    if (command == null) {
                        continue;
                    }

                    if ("execute_script".equals(command.getNameOfCommand())) {
                        continue;
                    }

                    if ((command.getStringArgsAmount() == 1 && command.getObjectArgsAmount() == 1 && tokens.length != 3) ||
                            (command.getStringArgsAmount() == 1 && command.getObjectArgsAmount() == 0 && tokens.length != 2) ||
                            (command.getStringArgsAmount() == 0 && tokens.length != 1)) {
                        strOutput += ("Ошибка при чтении скрипта: неверное количество аргументов." + "\n");
                        continue;
                    }
                    if (tokens.length == 1 || tokens.length == 2 || tokens.length == 3) {
                        if (tokens.length == 2 && command.check(new Request(tokens[0], tokens[1], login, password))) {
                            strOutput += ("Выполнение команды: " + line + "\n");
                        } else if (tokens.length == 1) {
                            strOutput += ("Выполнение команды: " + line + "\n");
                        }
                    } else {
                        throw new WrongArgsNumber(command.getStringArgsAmount());
                    }

                    if (command.getStringArgsAmount() == 1) {
                        strOutput += command.execute(Arrays.copyOfRange(tokens, 1, tokens.length)) + "\n";
                    } else {
                        strOutput += command.execute(new Request(command.getNameOfCommand(), login, password)) + "\n";
                    }

                    if (command.getNameOfCommand().equals("exit")) {
                        return strOutput;
                    }
                } catch (Exception e) {
                    strOutput += ("Ошибка при выполнении команды: " + e.getMessage() + "\n");
                    return strOutput;
                }
            }
            return strOutput;
        }
    }

    public BufferedReader getBufferedReader(String filePath) throws IOException {
        File scriptFile = new File(filePath);

        if (!scriptFile.exists()) {
            throw new IOException("Файл скрипта не найден: " + filePath  + "\n");
        }

        if (!scriptFile.canRead()) {
            throw new IOException("Нет прав на чтение файла: " + filePath  + "\n");
        }

        return new BufferedReader(new FileReader(scriptFile));
    }
}
