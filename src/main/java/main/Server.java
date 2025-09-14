package main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.database.DBManager;
import main.invoker.Invoker;
import main.managers.CollectionManager;
import main.managers.ScriptManager;
import main.network.Request;
import main.network.Response;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

//ssh -p 2222 s466366@helios.cs.ifmo.ru JYqp&1503 chcp 1251 dNifs7jxVOz4anOD2
public class Server {
    final int PORT;
    private static final Logger logger = LogManager.getLogger(Server.class);
    public static Invoker inv = new Invoker();
    public static CollectionManager collectionManager = new CollectionManager();
    private static DBManager dbManager = new DBManager();
    public static String filename;
    public static boolean scriptMode = false;
    public static ScriptManager scriptManager = new ScriptManager(inv);

    public Server(int PORT) {
        this.PORT = PORT;
    }

    public void start() {
//        try (Selector selector = Selector.open();
//             ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {
//
//            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
//            serverSocketChannel.configureBlocking(false);
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            ExecutorService processor = Executors.newCachedThreadPool();
            ExecutorService responder = Executors.newFixedThreadPool(4);

            logger.info("Сервер запущен на порте {} и ожидает подключения.", PORT);

//            ByteBuffer buffer = ByteBuffer.allocate(4096);

//            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//                try {
//                    CSVProcessor.saveToCSV(filename, collectionManager.getCollection());
//                } catch (IOException e) {
//                    logger.error("Ошибка: ", e);
//                    logger.info("Сервер завершил работу. Коллекция сохранена.");
//                    throw new RuntimeException(e);
//                }
//            }));
            while (!serverSocket.isClosed()) {
                Socket clientSocket = serverSocket.accept();

                new Thread(() -> {
                    logger.info("Установлено соединение с клиентом ({}).", clientSocket.getInetAddress());

                    try (ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
                         ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream())) {
                        while (true) {
                            Request request = (Request) in.readObject();

                            if (!dbManager.authenticateUser(request.getLogin(), request.getPassword())) {
                                out.writeObject(new Response("Аутентификация не была пройдена!"));
                                out.flush();
                                continue;
                            }

                            processor.submit(() -> {
                                Response response;

                                if (request.getCommandName().equals("exit")) {
                                    response = new Response("Результат выполнения команды " + request.getCommandName() + ", запрошенной клиентом ("
                                            + clientSocket.getInetAddress() + "): \n-----\n" + inv.executeClientCommand(request) + "\n-----", "exit");
                                } else {
                                    response = new Response("Результат выполнения команды " + request.getCommandName() + ", запрошенной клиентом ("
                                            + clientSocket.getInetAddress() + "): \n-----\n" + inv.executeClientCommand(request) + "\n-----");
                                }

                                logger.info("Отправлен результат выполнения команды {} клиенту ({}).", request.getCommandName(), clientSocket.getInetAddress());

                                responder.submit(() -> {
                                    try {
                                        synchronized (out) {
                                            out.writeObject(response);
                                            out.flush();
                                        }
                                    } catch (IOException e) {
                                        logger.error("Ошибка при передаче данных на клиенту: " + e);
                                    }
                                });
                            });
                        }
                    } catch (EOFException eof) {
                        logger.warn("Разорвано соединение с клиентом ({})." + eof, clientSocket.getInetAddress());
                    } catch (Exception e) {
                        logger.error("Ошибка: " + e);
                    }
                }).start();
            }
        } catch (IOException e) {
            logger.error("Критическая ошибка запуска сервера", e);
        }
    }

//    private void connectClient(Selector selector, ServerSocketChannel serverSocketChannel) throws IOException {
//        SocketChannel client = serverSocketChannel.accept();
//        client.configureBlocking(false);
//        client.register(selector, SelectionKey.OP_READ);
//        logger.info("Установлено соединение с клиентом ({}).", client.getRemoteAddress());
//    }

//    private void receiveThenSend(ByteBuffer buffer, SelectionKey key) throws IOException {
//        int bytesRead = -1;
//        SocketChannel client = (SocketChannel) key.channel();
//
//        buffer.clear();
//        try {
//            bytesRead = client.read(buffer);
//        } catch (SocketException e) {
//            logger.warn("Разорвано соединение с клиентом ({}).", client.getRemoteAddress());
//            client.close();
//            return;
//        }
//        if (bytesRead == -1) {
//            logger.warn("Разорвано соединение с клиентом ({}).", client.getRemoteAddress());
//            client.close();
//            return;
//        }
//
//        buffer.flip();
//        Request command = null;
//        try (ByteArrayInputStream bais = new ByteArrayInputStream(buffer.array());
//             ObjectInputStream ois = new ObjectInputStream(bais)) {
//            command = (Request) ois.readObject();
//        } catch (ClassNotFoundException e) {
//            logger.error("Ошибка: не удалось прочитать запрос от клиента.");
//        }
//
//        logger.info("Получен запрос на выполнение команды {} от клиента ({}).", command.getCommandName(), client.getRemoteAddress());
//
//        buffer.clear();
//        Response response;
//
//        if (!dbManager.authenticateUser(command.getLogin(), command.getPassword())) {
//            response = new Response("Требуется аутентификация пользователя.");
//        } else if (command.getCommandName().equals("exit")) {
//            response = new Response("Результат выполнения команды " + command.getCommandName() + ", запрошенной клиентом ("
//                    + client.getRemoteAddress() + "): \n-----\n" + inv.executeClientCommand(command) + "\n-----", "exit");
//        } else {
//            response = new Response("Результат выполнения команды " + command.getCommandName() + ", запрошенной клиентом ("
//                    + client.getRemoteAddress() + "): \n-----\n" + inv.executeClientCommand(command) + "\n-----");
//        }
//
//        logger.info("Отправлен результат выполнения команды {} клиенту ({}).", command.getCommandName(), client.getRemoteAddress());
//
//        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
//             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
//            oos.writeObject(response);
//            buffer.put(baos.toByteArray());
//        }
//
//        buffer.flip();
//        while (buffer.hasRemaining()) {
//            client.write(buffer);
//        }
//
//        buffer.clear();
//    }

//    private static String executeServerCommandFromConsole(String argString) throws IOException {
//        String[] args = argString.split(" ");
//        String commandName = args[0];
//
//        if (!inv.getServerCommands().containsKey(commandName)) {
//            logger.error("Ошибка: команды с таким именем не существует. Серверу доступна только одна команда: save.");
//            return null;
//        }
//
//        if (args.length != 1){
//            logger.error("Ошибка: команда {} не принимает строковых аргументов.", commandName);
//            return null;
//        }
//
//        return inv.executeServerCommand(new Request(commandName, server_login, server_password));
//    }

    public static void main(String[] args) {
        try {
            logger.info("Загружена коллекция из базы данных.");
            collectionManager.loadCollectionFromDB();
        } catch (Exception e) {
            logger.error("Завершение работы программы. Ошибка: " + e.getMessage());
            System.exit(-1);
        }

        int port = 9959;
        Server server = new Server(port);
        server.start();
    }
}
