package main;

import main.commands.Command;
import main.database.DBManager;
import main.exceptions.InvalidDataException;
import main.exceptions.WrongArgsNumber;
import main.model.Organization;
import main.network.Request;
import main.network.Response;
import main.utils.InteractiveParser;
import main.utils.PasswordHashing;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;

import static main.Server.inv;


public class Client {
    private final String host;
    private final int port;
    private static String login;
    private static String password;
    private static final DBManager dbManager = new DBManager();
    private static final InteractiveParser interParser = new InteractiveParser();
    private static final Logger logger = LogManager.getLogger(Client.class);

    public Client(String host, int port, String login, String password) {
        this.host = host;
        this.port = port;
        Client.login = login;
        Client.password = password;
    }

    public void start() {
        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
             Scanner scanner = new Scanner(System.in)) {

            logger.info("Подключено к серверу " + host + ":" + port);

            while (true) {
                try {
                    logger.info("Введите команду: ");
                    String commandLine = scanner.nextLine().trim();
                    if (commandLine.isEmpty()) continue;

                    Request request = executeCommandFromConsole(commandLine);

                    out.writeObject(request);
                    out.flush();

                    Response response = (Response) in.readObject();
                    logger.info("Ответ сервера:\n" + response.getMessage());

                    if ("exit".equals(response.getCommandName())) {
                        logger.warn("Выход...");
                        break;
                    }
                } catch (NoSuchElementException e) {
                    logger.warn("Завершение работы.");
                    System.exit(-1);
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        } catch (Exception e) {
            logger.error("Ошибка клиента: " + e.getMessage());
        }
    }

    private static Request executeCommandFromConsole(String argString) throws InvalidDataException {
        String[] args = argString.split(" ");
        String commandName = args[0];

        if (!inv.getClientCommands().containsKey(commandName)) {
            throw new InvalidDataException("Ошибка: команды с таким именем не существует. Используйте команду help для полного списка команд.");
        }

        Command command = inv.getClientCommandByKey(commandName);
        int stringArgsAmount = command.getStringArgsAmount();
        int objectArgsAmount = command.getObjectArgsAmount();

        if (args.length != (stringArgsAmount + 1)){
            throw new InvalidDataException("Ошибка: команда " + commandName + " принимает ровно " + stringArgsAmount + " строковых аргументов.");
        }

        if (objectArgsAmount == 1) {
            Organization org = interParser.parseOrganization();
            org.setUsername(login);
            return new Request(commandName, args[1], org, login, password);
        }

        if (stringArgsAmount == 0) return new Request(commandName, login, password);

        return new Request(commandName, args[1], login, password);
    }

    public static void main(String[] args) {
        String host = "localhost";
        int port = 9959;

        try (Scanner sc = new Scanner(System.in)) {
            String user_login;
            String user_password;
            while (true) {
                logger.info("Регистрация/Вход? y/n ");
                String answer = sc.nextLine().trim();
                if (answer.isEmpty()) continue;
                if ("y".equals(answer) || "n".equals(answer)) {
                    logger.info("Введите логин: ");
                    String login = sc.nextLine();
                    logger.info("Введите пароль: ");
                    String plainPassword = sc.nextLine();

                    String passwordMd5 = PasswordHashing.md5(plainPassword);
                    if ("y".equals(answer) && !dbManager.registerUser(login, passwordMd5)) continue;
                    else if ("n".equals(answer) && !dbManager.authenticateUser(login, passwordMd5)) {
                        logger.warn("Неверный логин или пароль.");
                        continue;
                    }
                    user_login = login;
                    user_password = passwordMd5;
                    break;
                }
            }

            Client client = new Client(host, port, user_login, user_password);
            client.start();
        } catch (Exception e) {
            logger.error("Завершение работы: " + e.getMessage());
            System.exit(-1);
        }
    }
}

//public class Client {
//    private final int PORT;
//    private static LinkedList<Request> requests = new LinkedList<>();
//    private static final Logger logger = LogManager.getLogger(Client.class);
//    private static final InteractiveParser interParser = new InteractiveParser();
//    public static Invoker inv = new Invoker();
//
//
//
//    public Client(int PORT) {
//        this.PORT = PORT;
//    }
//
//    public void start() {
//        try (SocketChannel channel = SocketChannel.open();
//             Scanner scanner = new Scanner(System.in)) {
//
//            channel.connect(new InetSocketAddress(PORT));
//            ByteBuffer buffer = ByteBuffer.allocate(4096);
//
//            logger.info("Подключение к серверу ({}) успешно.", channel.getRemoteAddress());
//
//            while (true) {
//                if (requests.isEmpty()) {
//                    addRequest(startConsoleSession(scanner));
//                } else {
//                    sendThenReceive(buffer, channel);
//                }
//            }
//        } catch (IOException e) {
//            logger.error("Ошибка: не удалось подключиться к серверу. Пожалуйста, повторите попытку позже.");
//        }
//    }
//
//    private void sendThenReceive(ByteBuffer buffer, SocketChannel channel) throws IOException {
//        buffer.clear();
//        Request command = requests.poll();
//
//        if (command == null) {
//            return;
//        }
//
//        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
//             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
//            oos.writeObject(command);
//            buffer.put(baos.toByteArray());
//        }
//
//        buffer.flip();
//        while (buffer.hasRemaining()) {
//            channel.write(buffer);
//        }
//
//        buffer.clear();
//        int bytesRead = channel.read(buffer);
//        if (bytesRead == -1) {
//            logger.warn("Возникла проблема: получено пустое сообщение от сервера ({}).", channel.getRemoteAddress());
//            return;
//        }
//
//        buffer.flip();
//        Response response = null;
//        try (ByteArrayInputStream bais = new ByteArrayInputStream(buffer.array());
//             ObjectInputStream ois = new ObjectInputStream(bais)) {
//            response = (Response) ois.readObject();
//        } catch (ClassNotFoundException e) {
//            logger.error("Ошибка: не удалось прочитать запрос от сервера.");
//        }
//
//        logger.info("Получено сообщение от сервера ({}):\n{}", channel.getRemoteAddress(), response.getMessage());
//
//        if (response.getCommandName().equals("exit")) {
//            closeConnection();
//        }
//    }
//
//    public static void addRequest(Request request){
//        requests.add(request);
//    }
//
//    private static Request startConsoleSession(Scanner scanner) {
//        try {
//            String line = scanner.nextLine();
//            return executeCommandFromConsole(line);
//        } catch (NoSuchElementException e) {
//            logger.info("Завершение работы программы.");
//            System.exit(-1);
//            return null;
//        }
//    }
//
//    private static void closeConnection() {
//        logger.info("Завершение работы программы.");
//        System.exit(-1);
//    }
//
//    public static void main(String[] args) throws Exception {
//        Client client = new Client(9959);
//        client.start();
//    }
//}
