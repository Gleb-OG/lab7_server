package main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import main.invoker.Invoker;
import main.managers.CollectionManager;
import main.managers.ScriptManager;
import main.model.Organization;
import main.network.Request;
import main.utils.CSVProcessor;
import main.network.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;


public class Server {
    final int PORT;
    private static final Logger logger = LogManager.getLogger(Server.class);
    public static Invoker inv = new Invoker();
    public static CollectionManager collectionManager = new CollectionManager();
    public static String filename;
    public static boolean scriptMode = false;
    public static ScriptManager scriptManager = new ScriptManager(inv);

    public Server(int PORT) {
        this.PORT = PORT;
    }

    public void start() {
        try (Selector selector = Selector.open();
             ServerSocketChannel serverSocketChannel = ServerSocketChannel.open()) {

            serverSocketChannel.socket().bind(new InetSocketAddress(PORT));
            serverSocketChannel.configureBlocking(false);
            logger.info("Сервер запущен на порте {} и ожидает подключения.", PORT);

            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            ByteBuffer buffer = ByteBuffer.allocate(4096);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    CSVProcessor.saveToCSV(filename, collectionManager.getCollection());
                } catch (IOException e) {
                    logger.error("Ошибка: ", e);
                    logger.info("Сервер завершил работу. Коллекция сохранена.");
                    throw new RuntimeException(e);
                }
            }));

            while (true) {
                try {
                    if (selector.select() == 0) {
                        continue;
                    }
                    for (SelectionKey key : selector.selectedKeys()) {
                        if (key.isAcceptable()) {
                            connectClient(selector, serverSocketChannel);
                        }
                        if (key.isReadable()) {
                            receiveThenSend(buffer, key);
                        }
                    }
                    selector.selectedKeys().clear();
                } catch (IOException e) {
                    logger.error("Ошибка при обработке подключения", e);
                }
            }
        } catch (IOException e) {
            logger.error("Критическая ошибка запуска сервера", e);
        }
    }

    private void connectClient(Selector selector, ServerSocketChannel serverSocketChannel) throws IOException {
        SocketChannel client = serverSocketChannel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        logger.info("Установлено соединение с клиентом ({}).", client.getRemoteAddress());
    }

    private void receiveThenSend(ByteBuffer buffer, SelectionKey key) throws IOException {
        int bytesRead = -1;
        SocketChannel client = (SocketChannel) key.channel();

        buffer.clear();
        try {
            bytesRead = client.read(buffer);
        } catch (SocketException e) {
            logger.warn("Разорвано соединение с клиентом ({}).", client.getRemoteAddress());
            client.close();
            return;
        }
        if (bytesRead == -1) {
            logger.warn("Разорвано соединение с клиентом ({}).", client.getRemoteAddress());
            client.close();
            return;
        }

        buffer.flip();
        Request command = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(buffer.array());
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            command = (Request) ois.readObject();
        } catch (ClassNotFoundException e) {
            logger.error("Ошибка: не удалось прочитать запрос от клиента.");
        }

        logger.info("Получен запрос на выполнение команды {} от клиента ({}).", command.getCommandName(), client.getRemoteAddress());

        buffer.clear();
        Response response;

        if (command.getCommandName().equals("exit")) {
            response = new Response("Результат выполнения команды "+ command.getCommandName() + ", запрошенной клиентом ("
                    + client.getRemoteAddress() + "): \n-----\n" + inv.executeClientCommand(command) + "\n-----", "exit");
        } else {
            response = new Response("Результат выполнения команды " + command.getCommandName() + ", запрошенной клиентом ("
                    + client.getRemoteAddress() + "): \n-----\n" + inv.executeClientCommand(command) + "\n-----");
        }

        logger.info("Отправлен результат выполнения команды {} клиенту ({}).", command.getCommandName(), client.getRemoteAddress());

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(response);
            buffer.put(baos.toByteArray());
        }

        buffer.flip();
        while (buffer.hasRemaining()) {
            client.write(buffer);
        }

        buffer.clear();
    }

    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                logger.warn("Не введено название csv-файла. Загружена пустая коллекция.");
                filename = "OrganizationsBase";
                List<Organization> organizations = CSVProcessor.loadFromCSV(filename);
                collectionManager.loadCollectionWithoutKeys(organizations);
            } else {
                filename = args[0];
                List<Organization> organizations = CSVProcessor.loadFromCSV(filename);
                collectionManager.loadCollectionWithoutKeys(organizations);
            }
        } catch (Exception e) {
            logger.error("Завершение работы программы.");
            System.exit(-1);
        }

        int port = 9959;
        Server server = new Server(port);
        server.start();
    }
}