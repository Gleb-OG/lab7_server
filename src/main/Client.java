package main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    public static Invoker inv = new Invoker();
    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public Client(int PORT) {
        this.PORT = PORT;
    }

    //Добавить проверку на ^C и иные экстренные шатдауны.
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
        if (command == null){
            return;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)){
            oos.writeObject(command);
            buffer.put(baos.toByteArray());
        }

        buffer.flip();
        while (buffer.hasRemaining()){
            channel.write(buffer);
        }

        buffer.clear();
        int bytesRead = channel.read(buffer);
        if (bytesRead == -1){
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
    }

    public static void addRequest(Request request){
        requests.add(request);
    }

    public static Request startConsoleSession(Scanner scanner) {
        try {
            String line = scanner.nextLine();
            return executeCommandFromConsole(line);
        } catch (NoSuchElementException e) {
            //ЧЕГО?!
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
        int argsAmount = command.getArgsAmount();

        if (args.length != (argsAmount + 1)){
            logger.error("Ошибка: команда {} принимает ровно {} строковых аргументов.", commandName, argsAmount);
            return null;
        }

        if (argsAmount == 0) return new Request(commandName);

        return new Request(commandName, args[1]);
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client(2225);
        client.start();
    }
}
