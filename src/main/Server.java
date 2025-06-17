package main;

import main.invoker.Invoker;
import main.managers.CollectionManager;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.List;

import main.model.Organization;
import main.network.Request;
import main.utils.CSVProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Server extends Thread {
    final int PORT;
    public static Invoker inv = new Invoker();
    public static CollectionManager collectionManager = new CollectionManager();
    public static String filename;
    public static boolean scriptMode = false;
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public Server(int PORT) {
        this.PORT = PORT;
    }

    public void start() {
        try (ServerSocketChannel channel = ServerSocketChannel.open();
             Selector selector = Selector.open();
             ServerSocket serverSocket = new ServerSocket(PORT)) {

            channel.socket().bind(new InetSocketAddress(9999));
            channel.configureBlocking(false);
            channel.register(selector, SelectionKey.OP_ACCEPT);
            ByteBuffer buffer = ByteBuffer.allocate(4096);

            while (true){

                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    try {
                        CSVProcessor.saveToCSV(filename, collectionManager.getCollection());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    System.out.println("Server stopped. Collection saved.");
                }));

                if (selector.select() == 0){
                    continue;
                }
                for (SelectionKey key: selector.selectedKeys()){
                    if (key.isAcceptable()){
                        connectClient(selector, key);
                    }
                    if (key.isReadable()){
                        receiveThenSend(buffer, key);
                    }
                }
                selector.selectedKeys().clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void connectClient(Selector selector, SelectionKey key) throws IOException{
        if (!(key.channel() instanceof ServerSocketChannel channel)){
            logger.error("Обнаружена попытка подключения по неизвестному каналу.");
            System.out.println("Обнаружена попытка подключения по неизвестному каналу.");
            return;
        }
        SocketChannel client = channel.accept();
        client.configureBlocking(false);
        client.register(selector, SelectionKey.OP_READ);
        System.out.println("(:^D) Установлено соединение с клиентом (" + client.getRemoteAddress() + ").");
        logger.info("(:^D) Установлено соединение с клиентом (" + client.getRemoteAddress() + ").");
    }

    private void receiveThenSend(ByteBuffer buffer, SelectionKey key) throws IOException{
        if (!(key.channel() instanceof SocketChannel client)){
            logger.error("Получено сообщение по неизвестному каналу.");
            System.out.println("Получено сообщение по неизвестному каналу.");
            return;
        }

        buffer.clear();
        int bytesRead = -1;
        try {
            bytesRead = client.read(buffer);
        } catch (SocketException e) {
            System.out.println("|X| Разорвано соединение с клиентом (" + client.getRemoteAddress() + ").");
            logger.warn("|X| Разорвано соединение с клиентом (" + client.getRemoteAddress() + ").");
            client.close();
            return;
        }
        if (bytesRead == -1){
            System.out.println("|X| Разорвано соединение с клиентом (" + client.getRemoteAddress() + ").");
            logger.warn("|X| Разорвано соединение с клиентом (" + client.getRemoteAddress() + ").");
            client.close();
            return;
        }

        buffer.flip();
        Request command = null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(buffer.array());
             ObjectInputStream ois = new ObjectInputStream(bais)){
            command = (Request) ois.readObject();
        }
        catch (ClassNotFoundException e){
            logger.error("Ошибка: не удалось прочитать запрос от клиента.");
            System.out.println("Ошибка: не удалось прочитать запрос от клиента.");
        }

        System.out.println("|R| Получен запрос на выполнение команды /" + command.getCommandName() + " от клиента (" + client.getRemoteAddress() + ").");
        logger.info("|R| Получен запрос на выполнение команды /" + command.getCommandName() + " от клиента (" + client.getRemoteAddress() + ").");
        buffer.clear();

        String response = "Результат выполнения команды /"+ command.getCommandName() + ", запрошенной клиентом ("
                + client.getRemoteAddress() + "): \n-----\n" + inv.executeCommand(command) + "\n-----";
        logger.info(response);
        System.out.println(response);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)){
            oos.writeObject(response);
            buffer.put(baos.toByteArray());
        }
        buffer.flip();
        while (buffer.hasRemaining()){
            client.write(buffer);
        }
        buffer.clear();
    }

    public static CollectionManager getCollectionManager () {
        return collectionManager;
    }
    public static void setCollection(CollectionManager collectionManager){
        Server.collectionManager = collectionManager;
    }

    public static void setInvoker(Invoker invoker){
        Server.inv = invoker;
    }

    public static Server setFilename (String filename){
        Server.filename = filename;
        return null;
    }

    public static void main (String[]args) {
        try {
            if (args.length == 0) {
                System.out.println("Не введено название csv-файла. Загружена пустая коллекция.");
            } else {
                List<Organization> organizations = CSVProcessor.loadFromCSV(args[0]);
                collectionManager.loadCollectionWithoutKeys(organizations);
            }
        } catch (Exception e) {
            System.out.println("Завершение работы программы.");
            logger.error("Завершение работы программы.");
            System.exit(-1);
        }

        int port = 2225;
        Server server = new Server(port);
        System.out.println("Сервер запущен на порте " + port + ".");
        logger.info("Сервер запущен на порте " + port + ".");
        server.start();
    }
}