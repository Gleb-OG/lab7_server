package main.managers;

import main.Server;
import main.commands.Command;
import main.invoker.Invoker;
import main.network.Request;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * Менеджер, управляющий ходом работы программы: запрашивает пользовательский ввод и в целом реализует общую логику программы.
 */
public class RunManager {
    private Invoker inv;
    private ConsoleManager console;

    public RunManager(Invoker inv) {
        this.inv = inv;
        this.console = new ConsoleManager();
    }

    public String runScript(ScriptManager scriptManager, List<String> commandsList) throws IOException {
        try (BufferedReader reader = scriptManager.getBufferedReader()) {
            Server.currentScriptReader = reader;
            String line;
            String strOutput = "";
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] tokens = Arrays.stream(line.split(" "))
                        .filter(s -> !s.isEmpty())
                        .toArray(String[]::new);

                console.setTokens(tokens);

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
                    if (tokens.length == 1 || tokens.length == 2 || tokens.length == 3) {
                        if (tokens.length == 2 && command.check(new Request(tokens[0], tokens[1]))) {
                            strOutput += ("Выполнение команды: " + line);
                        } else if (tokens.length == 3 && command.check(new Request(tokens[0], tokens[1], tokens[2]))) {
                            strOutput += ("Выполнение команды: " + line);
                        } else {
                            strOutput += ("Выполнение команды: " + line);
                        }
                    }
                    if (commandsList.contains(tokens[0])) {
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
}
