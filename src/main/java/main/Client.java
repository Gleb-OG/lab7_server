package main;

import main.model.Organization;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.utils.InteractiveParser;
import main.invoker.Invoker;
import main.network.Response;
import main.commands.Command;
import main.network.Request;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class Client {
    private final int PORT;
    private static LinkedList<Request> requests = new LinkedList<>();
    private static final Logger logger = LogManager.getLogger(Client.class);
    private static final InteractiveParser interParser = new InteractiveParser();
    public static Invoker inv = new Invoker();



    public Client(int PORT) {
        this.PORT = PORT;
    }

    public void start() {
        try (SocketChannel channel = SocketChannel.open();
             Scanner scanner = new Scanner(System.in)) {

            channel.connect(new InetSocketAddress(PORT));
            ByteBuffer buffer = ByteBuffer.allocate(4096);

            logger.info("Подключение к серверу ({}) успешно.", channel.getRemoteAddress());

            while (true) {
                if (requests.isEmpty()) {
                    addRequest(startConsoleSession(scanner));
                } else {
                    sendThenReceive(buffer, channel);
                }
            }
        } catch (IOException e) {
            logger.error("Ошибка: не удалось подключиться к серверу. Пожалуйста, повторите попытку позже.");
        }
    }

    private void sendThenReceive(ByteBuffer buffer, SocketChannel channel) throws IOException {
        buffer.clear();
        Request command = requests.poll();

        if (command == null) {
            return;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(command);
            buffer.put(baos.toByteArray());
        }

        buffer.flip();
        while (buffer.hasRemaining()) {
            channel.write(buffer);
        }

        buffer.clear();
        int bytesRead = channel.read(buffer);
        if (bytesRead == -1) {
            logger.warn("Возникла проблема: получено пустое сообщение от сервера ({}).", channel.getRemoteAddress());
            return;
        }

        buffer.flip();
        Response response = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(buffer.array());
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            response = (Response) ois.readObject();
        } catch (ClassNotFoundException e) {
            logger.error("Ошибка: не удалось прочитать запрос от сервера.");
        }

        logger.info("Получено сообщение от сервера ({}):\n{}", channel.getRemoteAddress(), response.getMessage());

        if (response.getCommandName().equals("exit")) {
            closeConnection();
        }
    }

    public static void addRequest(Request request){
        requests.add(request);
    }

    private static Request startConsoleSession(Scanner scanner) {
        try {
            String line = scanner.nextLine();
            return executeCommandFromConsole(line);
        } catch (NoSuchElementException e) {
            logger.info("Завершение работы программы.");
            System.exit(-1);
            return null;
        }
    }

    private static Request executeCommandFromConsole(String argString) {
        String[] args = argString.split(" ");
        String commandName = args[0];

        if (!inv.getClientCommands().containsKey(commandName)) {
            logger.error("Ошибка: команды с таким именем не существует. Используйте команду help для полного списка команд.");
            return null;
        }

        Command command = inv.getClientCommandByKey(commandName);
        int stringArgsAmount = command.getStringArgsAmount();
        int objectArgsAmount = command.getObjectArgsAmount();

        if (args.length != (stringArgsAmount + 1)){
            logger.error("Ошибка: команда {} принимает ровно {} строковых аргументов.", commandName, stringArgsAmount);
            return null;
        }

        if (objectArgsAmount == 1) {
            Organization org = interParser.parseOrganization();
            return new Request(commandName, args[1], org, login, password);
        }

        if (stringArgsAmount == 0) return new Request(commandName, login, password);

        return new Request(commandName, args[1], login, password);
    }

    private static void closeConnection() {
        logger.info("Завершение работы программы.");
        System.exit(-1);
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client(9959);
        client.start();
    }
}
