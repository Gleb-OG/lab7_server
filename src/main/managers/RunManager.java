package main.managers;

import main.Server;
import main.commands.Command;
import main.exceptions.InvalidDataException;
import main.exceptions.WrongArgsNumber;
import main.invoker.Invoker;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Менеджер, управляющий ходом работы программы: запрашивает пользовательский ввод и в целом реализует общую логику программы.
 */
public class RunManager {
    private Invoker inv;
    private ConsoleManager console;
    private Scanner inputScanner;

    public RunManager(Invoker inv, ConsoleManager console, Scanner inputScanner) {
        this.inv = inv;
        this.console = console;
        this.inputScanner = inputScanner;
    }

    public String readLine() {
        if (!inputScanner.hasNextLine()) {
            System.out.println("Завершение работы консоли.");
        }
        return inputScanner.nextLine();
    }

    public void runConsole() {
        while (true) {
            System.out.print("Введите команду: ");
            String line;
            try {
                line = readLine().trim();
            } catch (NoSuchElementException e) {
                break;
            }
            if (line.isEmpty()) {
                System.out.println("Команда не может быть пустой.");
                continue;
            }

            try {
                String[] tokens = Arrays.stream(line.split(" "))
                        .filter(s -> !s.isEmpty())
                        .toArray(String[]::new);
                console.setTokens(tokens);
                Command command = inv.getClientCommands().get(tokens[0]);
                if (tokens.length == 1) {
                    if (command.getArgsAmount() != 0)
                        throw new WrongArgsNumber(command.getArgsAmount());
                    command.execute();
                }
                if (tokens.length == 2) {
                    if (command.getArgsAmount() == 0) throw new WrongArgsNumber(0);
                    command.execute();
                }
                if (tokens.length > 2)
                    if (command.getArgsAmount() != tokens.length - 1)
                        throw new WrongArgsNumber(command.getArgsAmount());
            } catch (NullPointerException e) {
                System.out.println("Неизвестная команда. Введите другую.");
            } catch (WrongArgsNumber | IOException | InvalidDataException e) {
                System.out.println(e.getMessage());
                System.out.println("Попробуйте ещё раз.");
            }
        }
    }

    public void runScript(ScriptManager scriptManager, List<String> commandsList) throws IOException {
        try (BufferedReader reader = scriptManager.getBufferedReader()) {
            Server.currentScriptReader = reader;
            String line;
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
                    System.out.println("Ошибка при чтении скрипта: неверное количество аргументов.");
                    continue;
                }

                try {
                    if (command.check(Arrays.copyOfRange(tokens, 1, tokens.length))) {
                        System.out.println("Выполнение команды: " + line);
                    }
                    if (commandsList.contains(tokens[0])) {
                        command.execute(Arrays.copyOfRange(tokens, 1, tokens.length));
                    } else {
                        command.execute();
                    }
                } catch (WrongArgsNumber | InvalidDataException e) {
                    System.out.println("Ошибка при выполнении команды: " + e.getMessage());
                }
            }
        }
    }
}
